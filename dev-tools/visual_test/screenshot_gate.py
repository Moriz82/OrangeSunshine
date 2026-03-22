#!/usr/bin/env python3
"""
Screenshot / image gate for Orange Sunshine visual smoke tests.

Commands:
    capture  -- grab one frame from a X11 display via ffmpeg
    compare  -- compare mean brightness of two images (requires Pillow)
    check    -- capture a frame and print brightness summary

Usage:
    python3 dev-tools/visual_test/screenshot_gate.py capture [--out PATH] [--display :0]
    python3 dev-tools/visual_test/screenshot_gate.py compare BEFORE AFTER [--threshold 15] [--region top-right|full]
    python3 dev-tools/visual_test/screenshot_gate.py check [--display :0] [--out-dir build/smoke/frames]
"""
from __future__ import annotations

import argparse
import shutil
import subprocess
import sys
import time
from pathlib import Path

# ── Optional dependencies ─────────────────────────────────────────────────────

try:
    from PIL import Image
    import numpy as np  # numpy is always available if PIL is

    HAS_PILLOW = True
except ImportError:
    HAS_PILLOW = False

# ── Paths ─────────────────────────────────────────────────────────────────────

ROOT = Path(__file__).resolve().parents[2]
DEFAULT_FRAMES_DIR = ROOT / "build" / "smoke" / "frames"

# ── Helpers ───────────────────────────────────────────────────────────────────


_FFMPEG_FOUND: bool | None = None


def _check_ffmpeg() -> bool:
    """Return True if ffmpeg is on PATH (cached after first call)."""
    global _FFMPEG_FOUND
    if _FFMPEG_FOUND is None:
        _FFMPEG_FOUND = shutil.which("ffmpeg") is not None
    return _FFMPEG_FOUND


def _capture_frame(display: str, out_path: Path) -> bool:
    """
    Capture a single frame from *display* via ffmpeg.
    Returns True on success, False on failure (including missing ffmpeg).
    Caller is responsible for treating missing-ffmpeg as a non-fatal skip.
    """
    if not _check_ffmpeg():
        print("[screenshot_gate] ffmpeg not found — skipping frame capture.")
        print("[screenshot_gate] Install ffmpeg to enable screenshot capture.")
        return False

    out_path.parent.mkdir(parents=True, exist_ok=True)

    result = subprocess.run(
        [
            "ffmpeg",
            "-f", "x11grab",
            "-video_size", "1280x720",
            "-i", f"{display}.0",
            "-vframes", "1",
            "-q:v", "2",
            "-y",
            str(out_path),
        ],
        capture_output=True,
    )

    if result.returncode != 0:
        stderr = result.stderr.decode(errors="replace")
        print(f"[screenshot_gate] ffmpeg failed (exit {result.returncode}).")
        for line in stderr.splitlines()[-5:]:
            print(f"  {line}")
        return False

    print(f"[screenshot_gate] Frame captured: {out_path}")
    return True


def _mean_brightness(img_path: Path, region: str) -> tuple[float, tuple[int, int]]:
    """
    Compute mean brightness (0–100 scale) of *img_path*.
    *region* is 'top-right' or 'full'.
    Returns (brightness, (width, height)) so callers avoid re-opening the file.
    Guard with HAS_PILLOW before calling — requires Pillow and numpy.
    """
    img = Image.open(img_path).convert("L")
    size = img.size
    w, h = size
    if region == "top-right":
        img = img.crop((w // 2, 0, w, h // 2))
    arr = np.array(img, dtype=float)
    return float(arr.mean() / 255 * 100), size


# ── Sub-commands ──────────────────────────────────────────────────────────────


def cmd_capture(args: argparse.Namespace) -> int:
    if args.out:
        out_path = Path(args.out)
    else:
        timestamp = int(time.time())
        out_path = DEFAULT_FRAMES_DIR / f"frame_{timestamp}.jpg"

    ffmpeg_present = _check_ffmpeg()
    ok = _capture_frame(args.display, out_path)
    return 0 if ok or not ffmpeg_present else 1


def cmd_compare(args: argparse.Namespace) -> int:
    if not HAS_PILLOW:
        print("[screenshot_gate] Pillow not installed — skipping image comparison.")
        print("[screenshot_gate] Install with: pip install Pillow")
        return 0

    before_path = Path(args.before)
    after_path = Path(args.after)

    for p in (before_path, after_path):
        if not p.is_file():
            print(f"[screenshot_gate] File not found: {p}")
            return 1

    before_brightness, _ = _mean_brightness(before_path, args.region)
    after_brightness, _ = _mean_brightness(after_path, args.region)
    delta = after_brightness - before_brightness

    print(f"[screenshot_gate] Before brightness ({args.region}): {before_brightness:.2f}")
    print(f"[screenshot_gate] After  brightness ({args.region}): {after_brightness:.2f}")
    print(f"[screenshot_gate] Delta: {delta:+.2f} (threshold: ±{args.threshold})")

    if abs(delta) >= args.threshold:
        print("[screenshot_gate] PASS — visible change detected.")
        return 0
    print("[screenshot_gate] FAIL — no significant brightness change detected.")
    return 1


def cmd_check(args: argparse.Namespace) -> int:
    out_dir = Path(args.out_dir) if args.out_dir else DEFAULT_FRAMES_DIR
    timestamp = int(time.time())
    out_path = out_dir / f"frame_{timestamp}.jpg"

    ok = _capture_frame(args.display, out_path)
    if not ok:
        return 0

    print(f"[screenshot_gate] Frame saved: {out_path}")

    if HAS_PILLOW:
        brightness, (w, h) = _mean_brightness(out_path, "full")
        print(f"[screenshot_gate] Frame size: {w}x{h}")
        print(f"[screenshot_gate] Mean brightness (full): {brightness:.2f}/100")
    else:
        print("[screenshot_gate] Pillow not installed — brightness summary skipped.")
        print("[screenshot_gate] Install with: pip install Pillow")

    return 0


# ── Argument parsing ──────────────────────────────────────────────────────────


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description="Screenshot / image gate for Orange Sunshine visual smoke tests.",
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    sub = parser.add_subparsers(dest="command", metavar="COMMAND")

    # capture
    p_capture = sub.add_parser("capture", help="Grab one frame from the X11 display.")
    p_capture.add_argument(
        "--out",
        metavar="PATH",
        default=None,
        help=f"Output image path (default: {DEFAULT_FRAMES_DIR}/frame_<timestamp>.jpg)",
    )
    p_capture.add_argument(
        "--display",
        metavar="DISPLAY",
        default=":0",
        help="X11 display to grab (default: :0)",
    )

    # compare
    p_compare = sub.add_parser(
        "compare",
        help="Compare mean brightness between two images (requires Pillow).",
    )
    p_compare.add_argument("before", metavar="BEFORE", help="Path to the before image.")
    p_compare.add_argument("after", metavar="AFTER", help="Path to the after image.")
    p_compare.add_argument(
        "--threshold",
        type=float,
        default=15.0,
        metavar="N",
        help="Minimum brightness delta to count as a visible change (default: 15).",
    )
    p_compare.add_argument(
        "--region",
        choices=["top-right", "full"],
        default="top-right",
        help="Image region to measure (default: top-right, where the Minecraft HUD lives).",
    )

    # check
    p_check = sub.add_parser(
        "check",
        help="Capture a frame and print a brightness summary.",
    )
    p_check.add_argument(
        "--display",
        metavar="DISPLAY",
        default=":0",
        help="X11 display to grab (default: :0)",
    )
    p_check.add_argument(
        "--out-dir",
        metavar="DIR",
        default=None,
        help=f"Directory for captured frames (default: {DEFAULT_FRAMES_DIR})",
    )

    return parser


def main() -> None:
    parser = build_parser()
    args = parser.parse_args()

    if args.command is None:
        parser.print_help()
        sys.exit(0)

    if args.command == "capture":
        sys.exit(cmd_capture(args))
    elif args.command == "compare":
        sys.exit(cmd_compare(args))
    elif args.command == "check":
        sys.exit(cmd_check(args))
    else:
        parser.print_help()
        sys.exit(1)


if __name__ == "__main__":
    main()
