#!/usr/bin/env python3
"""
Parse a Minecraft server/client log and report mod-relevant issues.
Exits 0 if clean, 1 if errors found.
"""

import re
import sys
from pathlib import Path

MOD_ID = "OrangeSunshine"

# Patterns that always count as failures
HARD_FAIL = [
    re.compile(r"Caused by:"),
    re.compile(r"\[Server thread/ERROR\]"),
    re.compile(r"\[main/ERROR\]"),
    re.compile(r"\[Render thread/ERROR\]"),
    re.compile(r"java\.lang\.\w+Exception"),
    re.compile(r"java\.lang\.NullPointerException"),
    re.compile(r"Exception in thread"),
    re.compile(r"Game crashed"),
    re.compile(r"The game crashed"),
    re.compile(r"A fatal error has been detected"),
]

# Patterns that count as warnings
SOFT_WARN = [
    re.compile(r"\[Server thread/WARN\]"),
    re.compile(r"\[main/WARN\]"),
    re.compile(r"Failed to load"),
    re.compile(r"Could not load"),
    re.compile(r"Missing required"),
]

# Expected good lines (server started, mod loaded, etc.)
GOOD_SIGNALS = [
    re.compile(r"Bootstrapping Orange Sunshine"),
    re.compile(r"Done \(\d+\.\d+s\)!"),
    re.compile(r"Loading \d+ mods"),
    re.compile(r"orangesunshine \d"),
]


def analyse(log_path: Path):
    if not log_path.exists():
        print(f"[!] Log not found: {log_path}", file=sys.stderr)
        return 2

    text = log_path.read_text(errors="replace")
    lines = text.splitlines()

    errors = []
    warnings = []
    good = []

    for i, line in enumerate(lines):
        for pat in HARD_FAIL:
            if pat.search(line):
                # include a few lines of context
                ctx = lines[max(0, i - 1) : min(len(lines), i + 5)]
                errors.append("\n    ".join(ctx))
                break
        for pat in SOFT_WARN:
            if pat.search(line):
                warnings.append(line.strip())
                break
        for pat in GOOD_SIGNALS:
            if pat.search(line):
                good.append(line.strip())

    print("=== OrangeSunshine Log Analysis ===")
    print(f"Log: {log_path}")
    print()

    if good:
        print("✓ Good signals:")
        for g in good:
            print(f"  {g}")
        print()

    if warnings:
        print(f"⚠ Warnings ({len(warnings)}):")
        for w in warnings[:20]:
            print(f"  {w}")
        if len(warnings) > 20:
            print(f"  ... and {len(warnings) - 20} more")
        print()

    if errors:
        print(f"✗ ERRORS ({len(errors)}):")
        for e in errors:
            print(f"  {e}")
            print()
        return 1

    print("✓ No hard errors detected.")
    return 0


if __name__ == "__main__":
    path = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("fabric/run/logs/latest.log")
    sys.exit(analyse(path))
