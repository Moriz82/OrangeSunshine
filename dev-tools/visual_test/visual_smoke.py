#!/usr/bin/env python3
"""
Smoke-check for drug visual debugging:
  1. Runs :fabric:compileJava
  2. Optionally scans a game log for OrangeSunshine drug debug lines

Enable "Log drug visuals (debug)" in Mod Menu → Orange Sunshine, consume something,
then point this script at your latest log (defaults to ./client.log in the repo root).

Optional: record the game window with ffmpeg while testing, then review frames:
  ffmpeg -f x11grab -video_size 1920x1080 -i :0.0+0,0 -t 20 -c:v libx264 out.mp4
"""
from __future__ import annotations

import pathlib
import subprocess
import sys


def main() -> None:
    root = pathlib.Path(__file__).resolve().parents[2]
    subprocess.run(
        ["./gradlew", ":fabric:compileJava", "--no-daemon"],
        cwd=root,
        check=True,
    )
    print("compile: OK\n")
    log_path = pathlib.Path(sys.argv[1]) if len(sys.argv) > 1 else root / "client.log"
    if not log_path.is_file():
        print(f"No log file at {log_path}")
        print("After playing with visual debug logging on, pass the path to latest.log, e.g.:")
        print("  python3 dev-tools/visual_test/visual_smoke.py ~/.minecraft/logs/latest.log")
        return
    text = log_path.read_text(errors="ignore")
    needles = ("[OrangeSunshine] drug client tick", "[OrangeSunshine] S2C drug")
    hits = [line for line in text.splitlines() if any(n in line for n in needles)]
    print(f"OrangeSunshine drug debug lines in {log_path}: {len(hits)}")
    for line in hits[-50:]:
        print(line)


if __name__ == "__main__":
    main()
