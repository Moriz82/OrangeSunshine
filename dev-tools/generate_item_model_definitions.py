#!/usr/bin/env python3
"""
Generate assets/<namespace>/items/<id>.json for each mod item (MC 1.21.2+).

Vanilla expects item model definitions under assets/<namespace>/items/, each
referencing orangesunshine:item/<id> or a block model when no item model exists.
Re-run when PSItems / registerBlock items change.
"""
from __future__ import annotations

import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JAVA_ITEMS = ROOT / "src/main/java/moriz/orangesunshine/item/PSItems.java"
JAVA_BLOCKS = ROOT / "src/main/java/moriz/orangesunshine/block/PSBlocks.java"
ITEMS_DIR = ROOT / "src/main/resources/assets/orangesunshine/items"
MODELS_ITEM = ROOT / "src/main/resources/assets/orangesunshine/models/item"

# Item ids whose inventory model lives under models/block/ only (no models/item/<id>.json)
MODEL_OVERRIDES: dict[str, str] = {
    "ayahuasca_block": "orangesunshine:block/ayahuasca_stage0",
    "cut_poppy": "orangesunshine:block/cut_poppy_stage0",
    "mixing_table": "orangesunshine:block/mixing_table",
    "mortar_pestle": "orangesunshine:block/mortar_pestle",
    "san_pedro_plant": "orangesunshine:block/san_pedro_plant_stage0",
}


def collect_item_ids() -> set[str]:
    items: set[str] = set()
    text = JAVA_ITEMS.read_text(encoding="utf-8")
    for m in re.finditer(r"\bregister\(\s*\"([^\"]+)\"", text):
        items.add(m.group(1))
    for m in re.finditer(r"new CompoundItem\(\s*\"([^\"]+)\"", text):
        items.add(m.group(1))
    for m in re.finditer(r"new MixtureItem\(\s*\"([^\"]+)\"", text):
        items.add(m.group(1))
    btext = JAVA_BLOCKS.read_text(encoding="utf-8")
    for m in re.finditer(r"registerBlock\(\s*\"([^\"]+)\"", btext):
        items.add(m.group(1))
    return items


def model_path_for(item_id: str) -> str:
    if item_id in MODEL_OVERRIDES:
        return MODEL_OVERRIDES[item_id]
    item_path = MODELS_ITEM / f"{item_id}.json"
    if not item_path.exists():
        raise FileNotFoundError(
            f"No models/item/{item_id}.json and no MODEL_OVERRIDES entry for {item_id!r}"
        )
    return f"orangesunshine:item/{item_id}"


def main() -> None:
    ids = sorted(collect_item_ids())
    ITEMS_DIR.mkdir(parents=True, exist_ok=True)
    for item_id in ids:
        model = model_path_for(item_id)
        data = {"model": {"type": "minecraft:model", "model": model}}
        out = ITEMS_DIR / f"{item_id}.json"
        out.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")
    print(f"Wrote {len(ids)} files under {ITEMS_DIR.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
