#!/usr/bin/env bash
# ============================================================================
# OrangeSunshine Visual Effect Test
# ============================================================================
# Verifies that drug visual effects actually render on screen.
#
# What it does:
#   1. Builds the mod (unless --no-build)
#   2. Starts ffmpeg screen recording of the Minecraft window region
#   3. Launches Minecraft in visual-test mode (VisualTest.java)
#   4. Minecraft signals when to take screenshots via flag files
#   5. Takes baseline screenshot (no drugs) and peak screenshot (all drugs)
#   6. Stops recording when Minecraft exits
#   7. Runs Python analysis on the two screenshots
#   8. Reports PASS/FAIL and saves artifacts to tests/visual/results/
#
# Usage:
#   ./tests/visual/run_visual_test.sh [--no-build] [--no-record] [--keep-world]
#
# Requirements:
#   - DISPLAY=:0 (XWayland or X11)
#   - ffmpeg with x11grab support
#   - grim (Wayland screenshot) OR ImageMagick import (X11 screenshot)
#   - python3 + Pillow
# ============================================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"
JAVA_HOME="${JAVA_HOME:-/home/moriz/.local/jdks/jdk8u482-b08}"
export JAVA_HOME

TIMESTAMP="$(date +%Y%m%d_%H%M%S)"
RESULTS_DIR="$SCRIPT_DIR/results/${TIMESTAMP}"
mkdir -p "$RESULTS_DIR"

GAME_DIR="$REPO_DIR/run"
DONE_FILE="$GAME_DIR/visual_test_done"
MC_SCREENSHOTS="$GAME_DIR/screenshots"
MC_LOG="$GAME_DIR/logs/latest.log"

BASELINE_PNG="$RESULTS_DIR/baseline.png"
PEAK_PNG="$RESULTS_DIR/peak.png"
RECORDING="$RESULTS_DIR/recording.mp4"
GRADLE_LOG="$RESULTS_DIR/gradle.log"

BUILD=true
RECORD=true
KEEP_WORLD=false

for arg in "$@"; do
    case "$arg" in
        --no-build)   BUILD=false ;;
        --no-record)  RECORD=false ;;
        --keep-world) KEEP_WORLD=true ;;
    esac
done

# ── Cleanup ──────────────────────────────────────────────────────────────────
FFMPEG_PID=""
GRADLE_PID=""

cleanup() {
    echo ""
    echo "[visual-test] Cleaning up..."
    [ -n "$FFMPEG_PID" ] && kill "$FFMPEG_PID" 2>/dev/null || true
    [ -n "$GRADLE_PID" ] && kill "$GRADLE_PID" 2>/dev/null || true
    pkill -f "__visualtest__" 2>/dev/null || true
    pkill -f "gradlew runClient" 2>/dev/null || true
    pkill -f "jdk8u482.*MainClient" 2>/dev/null || true
    rm -f "$DONE_FILE"
    if [ "$KEEP_WORLD" = "false" ]; then
        rm -rf "$GAME_DIR/saves/__visualtest__"
    fi
}
trap cleanup EXIT

# ── Build ────────────────────────────────────────────────────────────────────
cd "$REPO_DIR"

if [ "$BUILD" = "true" ]; then
    echo "[visual-test] Building mod..."
    "$REPO_DIR/gradlew" build 2>&1 | tee "$RESULTS_DIR/build.log" | tail -5
    echo "[visual-test] Build complete"
fi

# ── Clean previous state ─────────────────────────────────────────────────────
rm -f "$DONE_FILE"
rm -f "$MC_SCREENSHOTS/visual_test_baseline.png" "$MC_SCREENSHOTS/visual_test_peak.png" 2>/dev/null || true
rm -f "$GAME_DIR/saves/__visualtest__" 2>/dev/null || true

# ── Start screen recording ───────────────────────────────────────────────────
if [ "$RECORD" = "true" ]; then
    echo "[visual-test] Starting screen recording -> $RECORDING"
    DISPLAY="${DISPLAY:-:0}" ffmpeg -y \
        -f x11grab \
        -video_size 1920x1080 \
        -framerate 30 \
        -i "${DISPLAY:-:0}+0,0" \
        -c:v libx264 -preset ultrafast -crf 28 \
        "$RECORDING" \
        2>"$RESULTS_DIR/ffmpeg.log" &
    FFMPEG_PID=$!
    echo "[visual-test] Recording PID: $FFMPEG_PID"
    sleep 1   # give ffmpeg a moment to start
fi

# ── Launch Minecraft ─────────────────────────────────────────────────────────
echo "[visual-test] Launching Minecraft with visual test mode..."
DISPLAY="${DISPLAY:-:0}" \
    "$REPO_DIR/gradlew" runClient -PvisualTests \
    2>&1 | tee "$GRADLE_LOG" &
GRADLE_PID=$!
echo "[visual-test] Gradle PID: $GRADLE_PID"

# ── Wait for Minecraft to finish and collect screenshots ─────────────────────
# Screenshots are taken internally by VisualTest.java from the live framebuffer.
# They are saved to <gameDir>/screenshots/visual_test_baseline.png and _peak.png.
# We poll for the done signal, then copy the screenshots to results.

echo "[visual-test] Waiting for Minecraft to finish..."

TIMEOUT=600    # 10 minutes max
ELAPSED=0
BASELINE_DONE=false
PEAK_DONE=false

while [ $ELAPSED -lt $TIMEOUT ]; do
    sleep 0.5
    ELAPSED=$((ELAPSED + 1))

    # Check if Gradle died unexpectedly
    if ! kill -0 "$GRADLE_PID" 2>/dev/null; then
        echo "[visual-test] Minecraft process exited"
        break
    fi

    # Check for screenshots appearing
    if [ -f "$MC_SCREENSHOTS/visual_test_baseline.png" ] && [ "$BASELINE_DONE" = "false" ]; then
        echo "[visual-test] Baseline screenshot detected"
        BASELINE_DONE=true
    fi
    if [ -f "$MC_SCREENSHOTS/visual_test_peak.png" ] && [ "$PEAK_DONE" = "false" ]; then
        echo "[visual-test] Peak screenshot detected"
        PEAK_DONE=true
    fi

    # Check done flag
    if [ -f "$DONE_FILE" ]; then
        echo "[visual-test] Minecraft signalled done"
        rm -f "$DONE_FILE"
        sleep 3  # let Minecraft exit cleanly
        break
    fi
done

# Copy screenshots to results dir
if [ -f "$MC_SCREENSHOTS/visual_test_baseline.png" ]; then
    cp "$MC_SCREENSHOTS/visual_test_baseline.png" "$BASELINE_PNG"
    echo "[visual-test] Baseline screenshot -> $BASELINE_PNG"
    BASELINE_DONE=true
fi
if [ -f "$MC_SCREENSHOTS/visual_test_peak.png" ]; then
    cp "$MC_SCREENSHOTS/visual_test_peak.png" "$PEAK_PNG"
    echo "[visual-test] Peak screenshot -> $PEAK_PNG"
    PEAK_DONE=true
fi

# Wait for Minecraft to finish
echo "[visual-test] Waiting for Minecraft to exit..."
wait "$GRADLE_PID" 2>/dev/null || true

# Stop recording
if [ -n "$FFMPEG_PID" ]; then
    echo "[visual-test] Stopping recording..."
    kill -INT "$FFMPEG_PID" 2>/dev/null || true
    wait "$FFMPEG_PID" 2>/dev/null || true
    FFMPEG_PID=""
    if [ -f "$RECORDING" ]; then
        SIZE="$(du -sh "$RECORDING" 2>/dev/null | cut -f1)"
        echo "[visual-test] Recording saved: $RECORDING ($SIZE)"
    fi
fi

# ── Analysis ─────────────────────────────────────────────────────────────────
echo ""
echo "[visual-test] ========================================"
echo "[visual-test]  Screenshot Analysis"
echo "[visual-test] ========================================"

ANALYSIS_EXIT=2

if [ "$BASELINE_DONE" = "false" ] || [ ! -f "$BASELINE_PNG" ]; then
    echo "[visual-test] ERROR: Baseline screenshot was not captured"
    ANALYSIS_EXIT=1
elif [ "$PEAK_DONE" = "false" ] || [ ! -f "$PEAK_PNG" ]; then
    echo "[visual-test] ERROR: Peak screenshot was not captured"
    ANALYSIS_EXIT=1
else
    python3 "$SCRIPT_DIR/analyze.py" \
        "$BASELINE_PNG" "$PEAK_PNG" \
        --output-dir "$RESULTS_DIR"
    ANALYSIS_EXIT=$?
fi

# ── Final report ─────────────────────────────────────────────────────────────
echo ""
echo "[visual-test] ========================================"
echo "[visual-test]  Results saved to: $RESULTS_DIR"
echo "[visual-test] ========================================"
ls -lh "$RESULTS_DIR" 2>/dev/null || true
echo ""

if [ $ANALYSIS_EXIT -eq 0 ]; then
    echo "[visual-test] ✓ VISUAL TEST PASSED — drug effects are rendering"
elif [ $ANALYSIS_EXIT -eq 1 ]; then
    echo "[visual-test] ✗ VISUAL TEST FAILED — no significant visual change detected"
else
    echo "[visual-test] ✗ VISUAL TEST ERROR — screenshots missing or analysis failed"
fi

exit $ANALYSIS_EXIT
