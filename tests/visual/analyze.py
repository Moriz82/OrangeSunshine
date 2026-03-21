#!/usr/bin/env python3
"""
Visual regression analysis for OrangeSunshine drug effects.

Compares a baseline screenshot (no drugs) against a peak screenshot (all drugs
active) and reports whether a significant visual change occurred.

Usage:
    analyze.py <baseline.png> <peak.png> [--output-dir <dir>]

Exit codes:
    0 = PASS (significant visual difference detected — drug effects visible)
    1 = FAIL (no significant difference — effects not rendering)
    2 = ERROR (bad arguments / missing files)
"""

import sys
import os
import argparse
import json
import math
from pathlib import Path

try:
    from PIL import Image, ImageChops, ImageDraw, ImageFont
    import colorsys
except ImportError:
    print("ERROR: Pillow not installed. Run: pip install Pillow")
    sys.exit(2)


# ─── Thresholds ───────────────────────────────────────────────────────────────
# These are deliberately conservative — the drug effects produce *very*
# dramatic changes (rainbow overlay, bloom, waves). If even the GL fallback
# overlay is working, the difference will be well above these thresholds.
#
# Note: hue_variance_increase is measured at ~250 ticks into the drug trip, while
# most hallucinogens (LSD, DMT) have 2400+ tick attack times. The dominant
# effect at this point is SATURATION/bloom (immediate). Color rotation via
# getRotatedColor() is also a uniform hue shift which changes mean hue but not
# necessarily hue variance. 0.0005 confirms hue IS changing without requiring
# the GL-overlay-level rainbow effect.

MIN_MEAN_PIXEL_DIFF  = 8.0    # mean absolute pixel diff per channel (0-255)
MIN_HISTOGRAM_SHIFT  = 0.06   # fraction of pixels that shifted bucket in any channel
MIN_BRIGHT_DIFF      = 5.0    # mean brightness difference (0-255)
MIN_HUE_VARIANCE_INCREASE = 0.0005  # peak hue variance should be higher than baseline
# ──────────────────────────────────────────────────────────────────────────────


def load_rgb(path):
    img = Image.open(path).convert("RGB")
    return img


def compute_stats(img):
    """Return a dict of image statistics."""
    import struct

    raw = img.tobytes()
    pixels = list(zip(raw[0::3], raw[1::3], raw[2::3]))
    n = len(pixels)

    r_vals = [p[0] for p in pixels]
    g_vals = [p[1] for p in pixels]
    b_vals = [p[2] for p in pixels]

    mean_r = sum(r_vals) / n
    mean_g = sum(g_vals) / n
    mean_b = sum(b_vals) / n
    mean_brightness = (mean_r + mean_g + mean_b) / 3.0

    # Hue variance (ignores very dark/desaturated pixels)
    hues = []
    for r, g, b in pixels:
        h, s, v = colorsys.rgb_to_hsv(r / 255.0, g / 255.0, b / 255.0)
        if s > 0.15 and v > 0.15:  # skip near-black and near-grey
            hues.append(h)

    hue_variance = 0.0
    if hues:
        mean_h = sum(hues) / len(hues)
        hue_variance = sum((h - mean_h) ** 2 for h in hues) / len(hues)

    # Histogram (16 buckets per channel)
    hist_r = [0] * 16
    hist_g = [0] * 16
    hist_b = [0] * 16
    for r, g, b in pixels:
        hist_r[r >> 4] += 1
        hist_g[g >> 4] += 1
        hist_b[b >> 4] += 1

    return {
        "mean_r": mean_r, "mean_g": mean_g, "mean_b": mean_b,
        "mean_brightness": mean_brightness,
        "hue_variance": hue_variance,
        "hue_count": len(hues),
        "hist_r": hist_r, "hist_g": hist_g, "hist_b": hist_b,
        "n_pixels": n,
    }


def histogram_shift(h1, h2, n):
    """Fraction of pixels that moved to a different bucket."""
    total_shift = sum(abs(h1[i] - h2[i]) for i in range(len(h1)))
    return total_shift / (2 * n)   # divide by 2 because each moved pixel is counted twice


def mean_pixel_diff(img1, img2):
    """Mean absolute per-channel pixel difference."""
    diff = ImageChops.difference(img1, img2)
    raw = diff.tobytes()
    n = len(raw) // 3
    total = sum(raw)
    return total / (3.0 * n)


def make_comparison_image(baseline, peak, diff_path):
    """Save a side-by-side comparison: baseline | peak | diff×4."""
    w, h = baseline.size
    out = Image.new("RGB", (w * 3, h))
    out.paste(baseline, (0, 0))
    out.paste(peak, (w, 0))

    # Amplified diff
    diff_raw = ImageChops.difference(baseline, peak)
    raw = diff_raw.tobytes()
    amp = bytes(min(255, b * 4) for b in raw)
    diff_img = Image.frombytes("RGB", baseline.size, amp)
    out.paste(diff_img, (w * 2, 0))

    # Labels
    try:
        draw = ImageDraw.Draw(out)
        draw.text((10, 10), "BASELINE (no drugs)", fill=(255, 255, 100))
        draw.text((w + 10, 10), "PEAK (all drugs)", fill=(255, 255, 100))
        draw.text((w * 2 + 10, 10), "DIFF ×4", fill=(255, 255, 100))
    except Exception:
        pass

    out.save(diff_path)
    return diff_path


def run_analysis(baseline_path, peak_path, output_dir):
    baseline = load_rgb(baseline_path)
    peak = load_rgb(peak_path)

    # Resize peak to baseline size if needed (shouldn't happen but be safe)
    if baseline.size != peak.size:
        peak = peak.resize(baseline.size, Image.LANCZOS)

    stats_b = compute_stats(baseline)
    stats_p = compute_stats(peak)

    mpd = mean_pixel_diff(baseline, peak)
    hist_shift_r = histogram_shift(stats_b["hist_r"], stats_p["hist_r"], stats_b["n_pixels"])
    hist_shift_g = histogram_shift(stats_b["hist_g"], stats_p["hist_g"], stats_b["n_pixels"])
    hist_shift_b = histogram_shift(stats_b["hist_b"], stats_p["hist_b"], stats_b["n_pixels"])
    max_hist_shift = max(hist_shift_r, hist_shift_g, hist_shift_b)
    bright_diff = abs(stats_p["mean_brightness"] - stats_b["mean_brightness"])
    hue_var_increase = stats_p["hue_variance"] - stats_b["hue_variance"]

    # ── Check each criterion ──────────────────────────────────────────────────
    checks = {
        "mean_pixel_diff":      (mpd,              MIN_MEAN_PIXEL_DIFF,           "≥"),
        "histogram_shift":      (max_hist_shift,   MIN_HISTOGRAM_SHIFT,           "≥"),
        "brightness_diff":      (bright_diff,      MIN_BRIGHT_DIFF,               "≥"),
        "hue_variance_increase":(hue_var_increase, MIN_HUE_VARIANCE_INCREASE,     "≥"),
    }

    results = {}
    all_passed = True
    for name, (value, threshold, op) in checks.items():
        passed = value >= threshold if op == "≥" else value <= threshold
        if not passed:
            all_passed = False
        results[name] = {"value": round(value, 4), "threshold": threshold, "passed": passed}

    # ── Comparison image ──────────────────────────────────────────────────────
    comparison_path = None
    if output_dir:
        os.makedirs(output_dir, exist_ok=True)
        comparison_path = os.path.join(output_dir, "comparison.png")
        make_comparison_image(baseline, peak, comparison_path)

    # ── Summary ───────────────────────────────────────────────────────────────
    report = {
        "overall": "PASS" if all_passed else "FAIL",
        "checks": results,
        "stats": {
            "baseline": {k: round(v, 4) if isinstance(v, float) else v
                         for k, v in stats_b.items() if k not in ("hist_r","hist_g","hist_b")},
            "peak":     {k: round(v, 4) if isinstance(v, float) else v
                         for k, v in stats_p.items() if k not in ("hist_r","hist_g","hist_b")},
        },
        "comparison_image": comparison_path,
    }

    if output_dir:
        report_path = os.path.join(output_dir, "report.json")
        with open(report_path, "w") as f:
            json.dump(report, f, indent=2)

    # ── Print human-readable output ───────────────────────────────────────────
    bar = "=" * 60
    print(bar)
    print(f"  OrangeSunshine Visual Test Report")
    print(bar)
    print(f"  Baseline : {baseline_path}")
    print(f"  Peak     : {peak_path}")
    print()

    for name, r in results.items():
        icon = "✓ PASS" if r["passed"] else "✗ FAIL"
        print(f"  {icon}  {name}")
        print(f"         value={r['value']:.4f}  threshold={r['threshold']}")

    print()
    print(f"  Baseline brightness : {stats_b['mean_brightness']:.1f}")
    print(f"  Peak     brightness : {stats_p['mean_brightness']:.1f}")
    print(f"  Baseline hue var    : {stats_b['hue_variance']:.5f}")
    print(f"  Peak     hue var    : {stats_p['hue_variance']:.5f}")
    if comparison_path:
        print(f"  Comparison image    : {comparison_path}")
    print()

    overall = "PASS" if all_passed else "FAIL"
    print(f"  {bar[:40]}")
    print(f"  OVERALL: {overall}")
    print(f"  {bar[:40]}")
    print()

    return all_passed


def main():
    parser = argparse.ArgumentParser(description="OrangeSunshine visual test analyzer")
    parser.add_argument("baseline", help="Baseline screenshot (no drugs)")
    parser.add_argument("peak", help="Peak screenshot (all drugs active)")
    parser.add_argument("--output-dir", "-o", default=None, help="Directory for report.json and comparison.png")
    args = parser.parse_args()

    if not os.path.exists(args.baseline):
        print(f"ERROR: baseline not found: {args.baseline}", file=sys.stderr)
        sys.exit(2)
    if not os.path.exists(args.peak):
        print(f"ERROR: peak not found: {args.peak}", file=sys.stderr)
        sys.exit(2)

    passed = run_analysis(args.baseline, args.peak, args.output_dir)
    sys.exit(0 if passed else 1)


if __name__ == "__main__":
    main()
