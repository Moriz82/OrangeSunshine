#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."
ROOT="$(pwd)"

TARGET_LOADER="all"
RUNTIME_SCENARIO="${ROOT}/dev-tools/qa/scenarios/runtime"
RUN_RUNTIME=1
VISUAL_MODE="none"
VISUAL_LOADER=""
SKIP_BUILD=0
ARTIFACT_ROOT="${ROOT}/dev-tools/qa/artifacts/$(date +%Y%m%d-%H%M%S)"
LAUNCH_CLIENT=0
CLIENT_DELAY=25

usage() {
    cat <<EOF
Usage: dev-tools/run-qa.sh [options]

Options:
  --loader <fabric|forge|neoforge|all>   target loaders (default: all)
  --runtime-scenario <dir>               run JSON scenarios from this dir (default: qa/scenarios/runtime)
  --skip-build                           skip gradle compile task
  --no-runtime                           skip runtime smoke checks
  --visual <none|singleplayer|local_server|both>  visual checklist mode
  --visual-loader <fabric|forge|neoforge> run visual mode on this loader
  --launch-client                        auto-launch client for visual mode
  --client-delay <seconds>               visual launch timeout delay (default: 25)
  --artifact-dir <path>                  base output directory
  --help
EOF
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --loader)
            TARGET_LOADER="$2"
            shift 2
            ;;
        --runtime-scenario)
            RUNTIME_SCENARIO="$2"
            shift 2
            ;;
        --skip-build)
            SKIP_BUILD=1
            shift
            ;;
        --no-runtime)
            RUN_RUNTIME=0
            shift
            ;;
        --visual)
            VISUAL_MODE="$2"
            shift 2
            ;;
        --visual-loader)
            VISUAL_LOADER="$2"
            shift 2
            ;;
        --launch-client)
            LAUNCH_CLIENT=1
            shift
            ;;
        --client-delay)
            CLIENT_DELAY="$2"
            shift 2
            ;;
        --artifact-dir)
            ARTIFACT_ROOT="$2"
            shift 2
            ;;
        --help)
            usage
            exit 0
            ;;
        *)
            echo "Unknown argument: $1" >&2
            usage
            exit 1
            ;;
    esac
done

if [[ "$TARGET_LOADER" != "all" && "$TARGET_LOADER" != "fabric" && "$TARGET_LOADER" != "forge" && "$TARGET_LOADER" != "neoforge" ]]; then
    echo "Unsupported loader: $TARGET_LOADER" >&2
    exit 1
fi

if [[ "$VISUAL_MODE" != "none" && "$VISUAL_MODE" != "singleplayer" && "$VISUAL_MODE" != "local_server" && "$VISUAL_MODE" != "both" ]]; then
    echo "Unsupported visual mode: $VISUAL_MODE" >&2
    echo "Expected: none, singleplayer, local_server, both" >&2
    exit 1
fi

if [[ "$VISUAL_LOADER" != "" && "$VISUAL_LOADER" != "fabric" && "$VISUAL_LOADER" != "forge" && "$VISUAL_LOADER" != "neoforge" ]]; then
    echo "Unsupported visual loader: $VISUAL_LOADER" >&2
    exit 1
fi

if [[ "$RUN_RUNTIME" -eq 1 && ! -d "$RUNTIME_SCENARIO" ]]; then
    echo "Runtime scenario directory not found: $RUNTIME_SCENARIO" >&2
    exit 1
fi

if [[ "$VISUAL_MODE" != "none" && ! -f "${ROOT}/dev-tools/qa/run-qa-visual.sh" ]]; then
    echo "Visual helper missing: dev-tools/qa/run-qa-visual.sh" >&2
    exit 1
fi

if [[ "$TARGET_LOADER" == "all" ]]; then
    LOADERS=(fabric forge neoforge)
else
    LOADERS=("$TARGET_LOADER")
fi

run_runtime() {
    local loader="$1"
    local artifact_dir="${ARTIFACT_ROOT}/${loader}/runtime"
    mkdir -p "$artifact_dir"

    echo "Running runtime checks for ${loader}"
    local args=(--loader "$loader" --scenario-dir "$RUNTIME_SCENARIO" --artifact-prefix "$artifact_dir")
    if [[ $SKIP_BUILD -eq 1 ]]; then
        args+=(--skip-build)
    fi

    bash dev-tools/smoke-test.sh "${args[@]}"
}

run_visual() {
    local mode="$1"
    local loader="$2"
    local scenario="$3"

    local artifact_dir="${ARTIFACT_ROOT}/${loader}/visual/${mode}"
    mkdir -p "$artifact_dir"

    local args=(--loader "$loader" --scenario "$scenario" --artifact-dir "$artifact_dir")
    if [[ $LAUNCH_CLIENT -eq 1 ]]; then
        args+=(--launch-client --client-delay "$CLIENT_DELAY")
    fi
    bash dev-tools/qa/run-qa-visual.sh "${args[@]}"
}

echo "QA run started"
echo "Loaders: ${LOADERS[*]}"
echo "Artifact root: $ARTIFACT_ROOT"
echo ""

if [[ "$RUN_RUNTIME" -eq 1 ]]; then
    for loader in "${LOADERS[@]}"; do
        run_runtime "$loader"
        echo ""
    done
fi

if [[ "$VISUAL_MODE" != "none" ]]; then
    if [[ "$TARGET_LOADER" == "all" ]]; then
        if [[ -n "$VISUAL_LOADER" ]]; then
            VISUAL_TARGET="$VISUAL_LOADER"
        else
            echo "Loader 'all' selected with visual mode; defaulting to fabric for visual checks"
            VISUAL_TARGET="fabric"
        fi
    else
        VISUAL_TARGET="${LOADERS[0]}"
    fi

    if [[ "$VISUAL_MODE" == "singleplayer" || "$VISUAL_MODE" == "both" ]]; then
        run_visual singleplayer "$VISUAL_TARGET" "${ROOT}/dev-tools/qa/scenarios/visual/singleplayer.json"
        echo ""
    fi

    if [[ "$VISUAL_MODE" == "local_server" || "$VISUAL_MODE" == "both" ]]; then
        run_visual local_server "$VISUAL_TARGET" "${ROOT}/dev-tools/qa/scenarios/visual/local_server.json"
    fi
fi

echo "QA run complete: $ARTIFACT_ROOT"
