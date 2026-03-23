#!/usr/bin/env python3
"""
Orange Sunshine automated visual & drug-state test orchestrator.

Tiers
-----
  compile  — :fabric:compileJava only (fastest, no display needed)
  smoke    — compile + headless client bootstrap (checks CLIENT_SMOKE_OK)
  server   — compile + dev server + RCON drug scenario (no display needed)
  full     — all of the above + optional ffmpeg frame capture

Usage
-----
  python3 dev-tools/visual_test/run_visual_tests.py [--tier compile|smoke|server|full]
                                                    [--log PATH] [--no-color]
"""
from __future__ import annotations

import argparse
import os
import pathlib
import subprocess
import sys
from dataclasses import dataclass, field
from typing import List

ROOT = pathlib.Path(__file__).resolve().parents[2]


# ---------------------------------------------------------------------------
# Colour helpers
# ---------------------------------------------------------------------------

def _use_color(no_color: bool) -> bool:
    return not no_color and not os.environ.get("NO_COLOR") and sys.stdout.isatty()


def _fmt(label: str, ansi: str, msg: str, color: bool) -> str:
    if color:
        return f"{ansi}[{label}]\033[0m {msg}"
    return f"[{label}] {msg}"


def _pass(msg: str, color: bool) -> None:
    print(_fmt("PASS", "\033[32m", msg, color))


def _fail(msg: str, color: bool) -> None:
    print(_fmt("FAIL", "\033[31m", msg, color))


def _info(msg: str, color: bool) -> None:
    print(_fmt("INFO", "\033[33m", msg, color))


# ---------------------------------------------------------------------------
# Results tracker
# ---------------------------------------------------------------------------

@dataclass
class Results:
    passed: int = 0
    total: int = 0
    details: List[str] = field(default_factory=list)

    def record(self, ok: bool, msg: str, color: bool) -> None:
        self.total += 1
        if ok:
            self.passed += 1
            _pass(msg, color)
        else:
            _fail(msg, color)
            self.details.append(msg)

    def all_passed(self) -> bool:
        return self.passed == self.total

    def summary(self, color: bool) -> None:
        msg = f"{self.passed}/{self.total} checks passed"
        print()
        if self.all_passed():
            _pass(f"=== {msg} ===", color)
        else:
            _fail(f"=== {msg} ===", color)
            for d in self.details:
                print(f"  - {d}")


# ---------------------------------------------------------------------------
# Tier implementations
# ---------------------------------------------------------------------------

def run_compile(results: Results, color: bool) -> None:
    _info("Tier: compile — running :fabric:compileJava", color)
    ret = subprocess.run(
        ["./gradlew", ":fabric:compileJava", "--no-daemon"],
        cwd=ROOT,
    )
    results.record(ret.returncode == 0, "compile :fabric:compileJava", color)


def run_smoke(results: Results, log_path: pathlib.Path, color: bool) -> None:
    _info("Tier: smoke — running headless client bootstrap", color)
    ret = subprocess.run(
        ["bash", str(ROOT / "dev-tools" / "run_client_smoke.sh")],
        cwd=ROOT,
    )
    if log_path.is_file():
        text = log_path.read_text(errors="ignore")
        results.record("CLIENT_SMOKE_OK" in text, "CLIENT_SMOKE_OK in log", color)

        optional_markers = [
            "S2C_RECEIVERS_REGISTERED",
            "RENDERERS_OK",
            "PARTICLES_OK",
            "SHADERS_OK",
        ]
        for m in optional_markers:
            if m in text:
                _info(f"optional marker present: {m}", color)
            else:
                _info(f"optional marker missing (not a failure): {m}", color)

        results.record(
            "Post-effect shader pipeline:" in text,
            "Post-effect shader pipeline loaded (ShaderLoader)",
            color,
        )

        drug_lines = [
            line for line in text.splitlines()
            if "[OrangeSunshine] drug client tick" in line
            or "[OrangeSunshine] S2C drug" in line
        ]
        _info(f"drug debug lines in log: {len(drug_lines)} (requires player in-world)", color)
    else:
        results.record(False, f"log file not found: {log_path}", color)
        return

    results.record(ret.returncode in (0, 124), "client smoke exit code", color)


def run_server(results: Results, color: bool) -> None:
    _info("Tier: server — running dev server + RCON drug scenario", color)
    scenario_dir = ROOT / "dev-tools" / "qa" / "scenarios" / "runtime"
    ret = subprocess.run(
        [
            "bash",
            str(ROOT / "dev-tools" / "smoke-test.sh"),
            "--skip-build",
            "--loader", "fabric",
            "--scenario-dir", str(scenario_dir),
        ],
        cwd=ROOT,
    )
    results.record(ret.returncode == 0, "server smoke + drug RCON scenario", color)


def run_full_capture(results: Results, color: bool) -> None:
    _info("Tier: full — optional ffmpeg frame capture", color)
    gate = ROOT / "dev-tools" / "visual_test" / "screenshot_gate.py"
    if not gate.is_file():
        _info("screenshot_gate.py not found — skipping frame capture", color)
        return
    ret = subprocess.run(["python3", str(gate), "capture"], cwd=ROOT)
    results.record(ret.returncode == 0, "screenshot_gate capture frame", color)


# ---------------------------------------------------------------------------
# Entry point
# ---------------------------------------------------------------------------

def main() -> int:
    parser = argparse.ArgumentParser(
        description="Orange Sunshine automated visual smoke test orchestrator.",
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    parser.add_argument(
        "--tier",
        choices=["compile", "smoke", "server", "full"],
        default="compile",
        help="Which test tier to run (default: compile)",
    )
    parser.add_argument(
        "--log",
        default=str(ROOT / "build" / "smoke" / "client_smoke.log"),
        help="Path to the client log file (for smoke tier)",
    )
    parser.add_argument(
        "--no-color",
        action="store_true",
        help="Disable ANSI colour output",
    )
    args = parser.parse_args()

    color = _use_color(args.no_color)
    log_path = pathlib.Path(args.log)
    results = Results()

    print(f"=== Orange Sunshine Visual Smoke Tests (tier: {args.tier}) ===")
    print(f"Root: {ROOT}")
    print()

    if args.tier == "compile":
        run_compile(results, color)
    elif args.tier == "smoke":
        run_compile(results, color)
        run_smoke(results, log_path, color)
    elif args.tier == "server":
        run_compile(results, color)
        run_server(results, color)
    elif args.tier == "full":
        run_compile(results, color)
        run_smoke(results, log_path, color)
        run_server(results, color)
        run_full_capture(results, color)

    results.summary(color)
    return 0 if results.all_passed() else 1


if __name__ == "__main__":
    sys.exit(main())
