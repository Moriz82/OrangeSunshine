#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."
ROOT="$(pwd)"

LOADER="fabric"
SCENARIO="${ROOT}/dev-tools/qa/scenarios/visual/singleplayer.json"
ARTIFACT_DIR="${ROOT}/dev-tools/qa/artifacts/$(date +%Y%m%d-%H%M%S)/visual"
LAUNCH_CLIENT=0
CLIENT_DELAY=25
AUTO_CAPTURE=1
SNAPSHOT_TOOL=""
SNAPSHOT_DELAY=2

usage() {
    cat <<USAGE
Usage: dev-tools/qa/run-qa-visual.sh [options]

Options:
  --loader <fabric|forge|neoforge>
  --scenario <path>
  --artifact-dir <path>
  --launch-client
  --no-capture
  --snapshot-tool <grim|gnome|import|spectacle|flameshot>
  --snapshot-delay <seconds>
  --client-delay <seconds>
  --help
USAGE
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --loader)
            LOADER="$2"
            shift 2
            ;;
        --scenario)
            SCENARIO="$2"
            shift 2
            ;;
        --artifact-dir)
            ARTIFACT_DIR="$2"
            shift 2
            ;;
        --launch-client)
            LAUNCH_CLIENT=1
            shift
            ;;
        --no-capture)
            AUTO_CAPTURE=0
            shift
            ;;
        --snapshot-tool)
            SNAPSHOT_TOOL="$2"
            shift 2
            ;;
        --snapshot-delay)
            SNAPSHOT_DELAY="$2"
            shift 2
            ;;
        --client-delay)
            CLIENT_DELAY="$2"
            shift 2
            ;;
        --help)
            usage
            exit 0
            ;;
        *)
            echo "Unknown arg: $1"
            usage
            exit 1
            ;;
    esac
done

mkdir -p "$ARTIFACT_DIR"
MANIFEST_FILE="${ARTIFACT_DIR}/screenshots.txt"
: > "$MANIFEST_FILE"

if [[ "$LOADER" != "fabric" && "$LOADER" != "forge" && "$LOADER" != "neoforge" ]]; then
    echo "Unsupported loader: $LOADER" >&2
    exit 1
fi
if [[ ! -f "$SCENARIO" ]]; then
    echo "Scenario file not found: $SCENARIO" >&2
    exit 1
fi

echo "Loader: $LOADER"
echo "Scenario: $SCENARIO"
echo "Artifacts: $ARTIFACT_DIR"
echo ""

if [[ "$AUTO_CAPTURE" -eq 1 ]]; then
    echo "Screenshot mode: enabled"
else
    echo "Screenshot mode: disabled (manual)"
fi
if [[ -n "$SNAPSHOT_TOOL" ]]; then
    echo "Snapshot tool: $SNAPSHOT_TOOL"
fi
if [[ "$SNAPSHOT_DELAY" != "0" ]]; then
    echo "Snapshot delay: ${SNAPSHOT_DELAY}s"
fi
echo ""

if [[ "$LAUNCH_CLIENT" -eq 1 ]]; then
    if [[ -z "${DISPLAY:-}" ]]; then
        echo "DISPLAY not set; cannot launch client automatically" >&2
        exit 1
    fi

    LOG_FILE="${ROOT}/${LOADER}/run/logs/latest.log"
    mkdir -p "$(dirname "$LOG_FILE")"
    : > "$LOG_FILE"

    bash ./gradlew ":${LOADER}:runClient" --console=plain > /tmp/os-qa-visual-gradle.log 2>&1 &
    CLIENT_PID=$!
    echo "Started ${LOADER} client (PID ${CLIENT_PID}). Waiting up to ${CLIENT_DELAY}s for startup..."

    sleep 2
    ELAPSED=0
    while ! grep -qE "Initializing game|Backend library|OpenGL|LWJGL|Minecraft|Started" "$LOG_FILE" 2>/dev/null; do
        if ! kill -0 "$CLIENT_PID" 2>/dev/null; then
            echo "Client exited early" >&2
            cat /tmp/os-qa-visual-gradle.log
            exit 1
        fi
        if [[ $ELAPSED -ge $CLIENT_DELAY ]]; then
            echo "Timed out waiting for client startup log" >&2
            exit 1
        fi
        sleep 2
        ELAPSED=$((ELAPSED+2))
    done
fi

cleanup() {
    if [[ -n "${CLIENT_PID:-}" ]] && kill -0 "$CLIENT_PID" 2>/dev/null; then
        echo "Stopping visual client (PID $CLIENT_PID)"
        kill "$CLIENT_PID" 2>/dev/null || true
    fi
}
trap cleanup EXIT

mapfile -t STEPS < <(python3 - "$SCENARIO" <<'PY'
import json
import sys

with open(sys.argv[1], "r", encoding="utf-8") as fp:
    data = json.load(fp)

for step in data.get("steps", []):
    if not isinstance(step, dict):
        continue
    sid = str(step.get("id", "step"))
    screenshot = str(step.get("screenshot", f"{sid}.png"))
    instruction = str(step.get("instruction", "")).replace("\t", "    ")
    print(f"{sid}\t{screenshot}\t{instruction}")
PY
)

if [[ ${#STEPS[@]} -eq 0 ]]; then
    echo "No steps found in scenario: $SCENARIO"
    exit 1
fi

echo "Visual QA sequence"
echo "Open the game and follow each instruction."
echo "Press Enter to capture each screenshot."
echo "Press Ctrl-C at any time to stop and keep artifacts already created."
if [[ "$LAUNCH_CLIENT" -eq 0 ]]; then
    echo "Tip: use --launch-client to auto-open a client for this run."
fi
echo ""

INDEX=1
TOTAL=${#STEPS[@]}

for STEP in "${STEPS[@]}"; do
    IFS=$'\t' read -r STEP_ID STEP_FILE STEP_TEXT <<< "$STEP"
    echo "[$INDEX/$TOTAL] [$STEP_ID] $STEP_TEXT"
    read -r -p "Press Enter when ready to continue: "

    if [[ "$AUTO_CAPTURE" -eq 1 ]]; then
        TARGET="${ARTIFACT_DIR}/${STEP_FILE}"
        mkdir -p "$(dirname "$TARGET")"
        if [[ -n "$SNAPSHOT_TOOL" ]]; then
            export SNAPSHOT_TOOL
        else
            unset SNAPSHOT_TOOL || true
        fi
        export SNAPSHOT_DELAY

        if CAPTURE_PATH=$(bash dev-tools/qa/capture_screenshot.sh "$TARGET"); then
            echo "Saved: $CAPTURE_PATH"
            echo "$CAPTURE_PATH" >> "$MANIFEST_FILE"
        else
            echo "Screenshot failed for $STEP_ID" >&2
        fi
    else
        echo "Skipping screenshot capture for $STEP_ID"
    fi

    INDEX=$((INDEX+1))
    echo ""
done

echo "Screenshot manifest: $MANIFEST_FILE"
echo "Visual sequence complete. Artifacts in $ARTIFACT_DIR"
