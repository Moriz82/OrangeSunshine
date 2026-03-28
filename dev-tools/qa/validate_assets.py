#!/usr/bin/env python3
"""
Offline asset integrity validator for OrangeSunshine mod.

Checks that item definitions, models, textures, blockstates, and recipes
are internally consistent -- no game server required.
"""

from __future__ import annotations

import argparse
import json
import os
import sys
from pathlib import Path
from typing import List, Set


def find_project_root() -> Path:
    """Walk up from this script to find the project root (contains src/)."""
    candidate = Path(__file__).resolve().parent
    for _ in range(10):
        if (candidate / "src" / "main" / "resources").is_dir():
            return candidate
        candidate = candidate.parent
    sys.exit("ERROR: could not locate project root (no src/main/resources found)")


ASSETS_NS = "orangesunshine"


class Validator:
    def __init__(self, root: Path, verbose: bool):
        self.root = root
        self.verbose = verbose
        self.assets = root / "src" / "main" / "resources" / "assets" / ASSETS_NS
        self.data = root / "src" / "main" / "resources" / "data" / ASSETS_NS
        self.errors: List[str] = []
        self.warnings: List[str] = []
        self.checked = 0

    def log(self, msg: str) -> None:
        if self.verbose:
            print(f"  [info] {msg}")

    def error(self, msg: str) -> None:
        self.errors.append(msg)
        print(f"  [ERROR] {msg}")

    def warn(self, msg: str) -> None:
        self.warnings.append(msg)
        if self.verbose:
            print(f"  [WARN] {msg}")

    # ------------------------------------------------------------------
    # Helpers
    # ------------------------------------------------------------------

    def _resolve_namespaced_ref(self, ref: str, subdir: str, ext: str) -> Path | None:
        """Resolve a namespaced reference (e.g. 'orangesunshine:item/foo') to a file path.
        Returns None for external namespaces."""
        if ":" in ref:
            ns, path = ref.split(":", 1)
        else:
            ns, path = "minecraft", ref

        if ns != ASSETS_NS:
            return None

        return self.assets / subdir / (path + ext)

    def _resolve_model_path(self, ref: str) -> Path | None:
        return self._resolve_namespaced_ref(ref, "models", ".json")

    def _resolve_texture_path(self, ref: str) -> Path | None:
        return self._resolve_namespaced_ref(ref, "textures", ".png")

    def _load_json(self, path: Path) -> dict | None:
        try:
            with open(path, encoding="utf-8") as f:
                return json.load(f)
        except json.JSONDecodeError as exc:
            self.error(f"Invalid JSON in {path}: {exc}")
            return None
        except OSError as exc:
            self.error(f"Cannot read {path}: {exc}")
            return None

    def _collect_json_files(self, directory: Path) -> List[Path]:
        """Recursively collect all .json files under a directory."""
        if not directory.is_dir():
            return []
        results = []
        for dirpath, _dirnames, filenames in os.walk(directory):
            for name in sorted(filenames):
                if name.endswith(".json"):
                    results.append(Path(dirpath) / name)
        return results

    # ------------------------------------------------------------------
    # Item definitions (1.21.11 format)
    # ------------------------------------------------------------------

    def validate_item_definitions(self) -> None:
        items_dir = self.assets / "items"
        if not items_dir.is_dir():
            self.warn(f"No items directory: {items_dir}")
            return

        print(f"[items] Scanning {items_dir}")
        for json_path in sorted(items_dir.iterdir()):
            if not json_path.name.endswith(".json"):
                continue
            self.checked += 1
            data = self._load_json(json_path)
            if data is None:
                continue

            model_block = data.get("model")
            if not isinstance(model_block, dict):
                self.log(f"{json_path.name}: no model block, skipping")
                continue

            model_ref = model_block.get("model")
            if not model_ref:
                self.log(f"{json_path.name}: model block has no model ref")
                continue

            resolved = self._resolve_model_path(model_ref)
            if resolved is None:
                self.log(f"{json_path.name}: external model ref {model_ref}, skipping")
                continue

            if not resolved.is_file():
                self.error(f"{json_path.name}: model file missing: {resolved}")
            else:
                self.log(f"{json_path.name}: model OK -> {model_ref}")
                self._validate_model_textures(resolved)

    # ------------------------------------------------------------------
    # Model texture validation
    # ------------------------------------------------------------------

    def _validate_model_textures(self, model_path: Path, visited: Set[Path] | None = None) -> None:
        if visited is None:
            visited = set()
        if model_path in visited:
            return
        visited.add(model_path)

        data = self._load_json(model_path)
        if data is None:
            return

        # Check parent model
        parent = data.get("parent")
        if parent:
            parent_path = self._resolve_model_path(parent)
            if parent_path is not None and not parent_path.is_file():
                self.error(f"{model_path.name}: parent model missing: {parent_path}")
            elif parent_path is not None:
                self._validate_model_textures(parent_path, visited)

        # Check textures
        textures = data.get("textures", {})
        for key, ref in textures.items():
            if not isinstance(ref, str):
                continue
            if ref.startswith("#"):
                continue  # texture variable reference
            tex_path = self._resolve_texture_path(ref)
            if tex_path is None:
                continue  # external namespace
            if not tex_path.is_file():
                self.error(f"{model_path.name}: texture missing for '{key}': {tex_path}")
            else:
                self.log(f"{model_path.name}: texture '{key}' OK -> {ref}")

        # Check override models
        for override in data.get("overrides", []):
            override_ref = override.get("model")
            if override_ref:
                override_path = self._resolve_model_path(override_ref)
                if override_path is not None and not override_path.is_file():
                    self.error(f"{model_path.name}: override model missing: {override_path}")
                elif override_path is not None:
                    self._validate_model_textures(override_path, visited)

    # ------------------------------------------------------------------
    # Blockstates
    # ------------------------------------------------------------------

    def validate_blockstates(self) -> None:
        bs_dir = self.assets / "blockstates"
        if not bs_dir.is_dir():
            self.warn(f"No blockstates directory: {bs_dir}")
            return

        print(f"[blockstates] Scanning {bs_dir}")
        for json_path in sorted(bs_dir.iterdir()):
            if not json_path.name.endswith(".json"):
                continue
            self.checked += 1
            data = self._load_json(json_path)
            if data is None:
                continue

            model_refs: Set[str] = set()

            # variants format
            variants = data.get("variants", {})
            for _variant_key, variant_val in variants.items():
                entries = variant_val if isinstance(variant_val, list) else [variant_val]
                for entry in entries:
                    if isinstance(entry, dict) and "model" in entry:
                        model_refs.add(entry["model"])

            # multipart format
            for part in data.get("multipart", []):
                apply_block = part.get("apply", {})
                entries = apply_block if isinstance(apply_block, list) else [apply_block]
                for entry in entries:
                    if isinstance(entry, dict) and "model" in entry:
                        model_refs.add(entry["model"])

            for ref in sorted(model_refs):
                resolved = self._resolve_model_path(ref)
                if resolved is None:
                    continue
                if not resolved.is_file():
                    self.error(f"{json_path.name}: blockstate model missing: {resolved}")
                else:
                    self.log(f"{json_path.name}: model OK -> {ref}")

    # ------------------------------------------------------------------
    # Recipes
    # ------------------------------------------------------------------

    def validate_recipes(self) -> None:
        recipes_dir = self.data / "recipes"
        if not recipes_dir.is_dir():
            self.warn(f"No recipes directory: {recipes_dir}")
            return

        print(f"[recipes] Scanning {recipes_dir}")
        recipe_files = self._collect_json_files(recipes_dir)
        for json_path in recipe_files:
            self.checked += 1
            data = self._load_json(json_path)
            if data is None:
                continue

            rel = json_path.relative_to(recipes_dir)

            # Recipe type must be present
            recipe_type = data.get("type")
            if not recipe_type:
                self.error(f"recipes/{rel}: missing 'type' field")
                continue

            if not isinstance(recipe_type, str) or ":" not in recipe_type:
                self.error(f"recipes/{rel}: invalid 'type' format: {recipe_type}")
                continue

            self.log(f"recipes/{rel}: type={recipe_type}")

            # Validate item references in ingredients and results
            self._check_recipe_items(data, f"recipes/{rel}")

    def _check_recipe_items(self, data: dict, label: str) -> None:
        """Check that item references in a recipe use valid 'namespace:name' format."""

        def check_item_ref(ref: str | dict, context: str) -> None:
            if isinstance(ref, dict):
                item = ref.get("item")
                tag = ref.get("tag")
                if item and isinstance(item, str):
                    if ":" not in item:
                        self.error(f"{label}: {context} has unqualified item ref: {item}")
                if tag and isinstance(tag, str):
                    if ":" not in tag:
                        self.error(f"{label}: {context} has unqualified tag ref: {tag}")
            elif isinstance(ref, str):
                if ":" not in ref:
                    self.error(f"{label}: {context} has unqualified ref: {ref}")

        # ingredient (single or array)
        ingredient = data.get("ingredient")
        if isinstance(ingredient, dict):
            check_item_ref(ingredient, "ingredient")
        elif isinstance(ingredient, list):
            for i, ing in enumerate(ingredient):
                check_item_ref(ing, f"ingredient[{i}]")

        # key (shaped recipes)
        key = data.get("key")
        if isinstance(key, dict):
            for k, v in key.items():
                check_item_ref(v, f"key[{k}]")

        # result
        result = data.get("result")
        if result:
            check_item_ref(result, "result")

    # ------------------------------------------------------------------
    # Run all checks
    # ------------------------------------------------------------------

    def run(self) -> int:
        print(f"Project root: {self.root}")
        print()

        self.validate_item_definitions()
        print()
        self.validate_blockstates()
        print()
        self.validate_recipes()
        print()

        print(f"Checked {self.checked} files")
        if self.warnings:
            print(f"Warnings: {len(self.warnings)}")
        if self.errors:
            print(f"ERRORS: {len(self.errors)}")
            return 1

        print("All checks passed.")
        return 0


def main() -> int:
    parser = argparse.ArgumentParser(description="Validate OrangeSunshine mod assets offline")
    parser.add_argument("--verbose", "-v", action="store_true", help="Show detailed output")
    parser.add_argument("--root", type=Path, default=None, help="Project root (auto-detected if omitted)")
    args = parser.parse_args()

    root = args.root or find_project_root()
    validator = Validator(root=root, verbose=args.verbose)
    return validator.run()


if __name__ == "__main__":
    raise SystemExit(main())
