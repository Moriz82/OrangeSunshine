#!/bin/bash
# OrangeSunshine automated test runner.
# Builds the mod, launches Minecraft headlessly, runs in-game checks, exits 0/1.

set -euo pipefail

JAVA_HOME=/home/moriz/.local/jdks/jdk8u482-b08
export JAVA_HOME

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG=/tmp/mc_autotest_$$.log

cleanup() {
    pkill -f "__autotest__" 2>/dev/null || true
    pkill -f "gradlew runClient" 2>/dev/null || true
    # Kill any leftover Minecraft JVMs from this run
    pkill -f "jdk8u482.*MainClient" 2>/dev/null || true
}
trap cleanup EXIT

cd "$SCRIPT_DIR"

echo "==> Building mod..."
"$JAVA_HOME/bin/java" -version 2>&1
"$SCRIPT_DIR/gradlew" build 2>&1 | tail -3

echo "==> Cleaning previous test world..."
rm -rf run/__autotest__

echo "==> Launching Minecraft in test mode..."
# Use the existing display (:0 via XWayland)
DISPLAY="${DISPLAY:-:0}" \
"$SCRIPT_DIR/gradlew" runClient -PrunTests 2>&1 | tee "$LOG" &
GRADLE_PID=$!

echo "==> Waiting for test result (up to 5 minutes)..."
TIMEOUT=300
ELAPSED=0
RESULT=""

while [ $ELAPSED -lt $TIMEOUT ]; do
    sleep 2
    ELAPSED=$((ELAPSED + 2))

    if grep -q "ALL TESTS PASSED" "$LOG" 2>/dev/null; then
        RESULT="PASS"
        break
    fi
    if grep -q "TESTS FAILED" "$LOG" 2>/dev/null; then
        RESULT="FAIL"
        break
    fi
    if grep -q "\[TEST\] CRASH:" "$LOG" 2>/dev/null; then
        RESULT="CRASH"
        break
    fi
    # Check if gradle exited unexpectedly before tests ran
    if ! kill -0 $GRADLE_PID 2>/dev/null; then
        if [ -z "$RESULT" ]; then
            RESULT="CRASH"
        fi
        break
    fi
done

if [ -z "$RESULT" ]; then
    RESULT="TIMEOUT"
fi

echo ""
echo "==> Test log saved to: $LOG"
echo ""
# Print just the TEST lines for a clean summary
grep "\[TEST\]" "$LOG" 2>/dev/null || true
echo ""

case "$RESULT" in
    PASS)
        echo "✓ ALL TESTS PASSED"
        exit 0
        ;;
    FAIL)
        echo "✗ TESTS FAILED"
        exit 1
        ;;
    CRASH)
        echo "✗ TEST CRASHED (check $LOG)"
        exit 1
        ;;
    TIMEOUT)
        echo "✗ TEST TIMED OUT after ${TIMEOUT}s"
        exit 1
        ;;
esac
