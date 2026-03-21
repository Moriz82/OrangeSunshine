#!/usr/bin/env bash
# ============================================================
# OrangeSunshine dev smoke-test pipeline
#
# Usage:
#   ./dev-tools/smoke-test.sh [--skip-build] [--client] [--loader <fabric|forge|neoforge>] [--scenario-dir <path>]
#
# Modes:
#   (default)     Run <loader>:runServer, wait for startup,
#                 run RCON test commands and optional scenario checks,
#                 then analyse logs.
#   --client      Run <loader>:runClient on DISPLAY=:0 instead,
#                 then analyse the client log.
#   --skip-build  Skip the build step (use cached classes/jars).
# ============================================================

set -euo pipefail
cd "$(dirname "$0")/.."
ROOT="$(pwd)"

export JAVA_HOME=/home/moriz/.jdks/temurin-21
export PATH="$JAVA_HOME/bin:$PATH"

RCON_PORT=25575
RCON_PASS="devtest"
STARTUP_TIMEOUT=120   # seconds to wait for server "Done"
MODE="server"
LOADER="fabric"
SKIP_BUILD=0
SCENARIO_DIR=""
ARTIFACT_PREFIX=""

usage() {
    cat <<EOF
Usage: dev-tools/smoke-test.sh [options]

Options:
  --client                 run client mode for selected loader
  --skip-build             skip compile step
  --loader <fabric|forge|neoforge>  select loader module (default: fabric)
  --scenario-dir <path>    run scenario JSON files from directory
  --artifact-prefix <path>  prefix used for logs and scenario JSON reports
  --help                   show this help
EOF
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --client)
            MODE="client"
            shift
            ;;
        --skip-build)
            SKIP_BUILD=1
            shift
            ;;
        --loader)
            if [[ $# -lt 2 ]]; then
                echo "--loader requires one argument"
                usage
                exit 1
            fi
            LOADER="$2"
            shift 2
            ;;
        --scenario-dir)
            if [[ $# -lt 2 ]]; then
                echo "--scenario-dir requires one argument"
                usage
                exit 1
            fi
            SCENARIO_DIR="$2"
            shift 2
            ;;
        --artifact-prefix)
            if [[ $# -lt 2 ]]; then
                echo "--artifact-prefix requires one argument"
                usage
                exit 1
            fi
            ARTIFACT_PREFIX="$2"
            shift 2
            ;;
        --help)
            usage
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            usage
            exit 1
            ;;
    esac
done

case "$LOADER" in
    fabric|forge|neoforge)
        ;;
    *)
        echo "Unsupported loader '$LOADER'. Use fabric, forge, or neoforge"
        exit 1
        ;;
esac

PROJECT="$LOADER"
TASK_COMPILE=":${PROJECT}:compileJava"
TASK_SERVER=":${PROJECT}:runServer"
TASK_CLIENT=":${PROJECT}:runClient"
SERVER_LOG="${PROJECT}/run/logs/latest.log"
SERVER_PID_FILE="/tmp/os-smoke-${PROJECT}.pid"
SERVER_PROPERTIES_FILE="${PROJECT}/run/server.properties"
CLIENT_LOG="${SERVER_LOG}"

mkdir -p "$(dirname "$SERVER_LOG")"
mkdir -p "$(dirname "$SERVER_PROPERTIES_FILE")"

# ── colours ──────────────────────────────────────────────────
RED='\033[0;31m'; GRN='\033[0;32m'; YEL='\033[1;33m'; NC='\033[0m'
ok()   { echo -e "${GRN}[PASS]${NC} $*"; }
fail() { echo -e "${RED}[FAIL]${NC} $*"; FAILURES=$((FAILURES+1)); }
info() { echo -e "${YEL}[INFO]${NC} $*"; }
FAILURES=0

upsert_property() {
    local file="$1"
    local key="$2"
    local value="$3"

    local tmp_file
    tmp_file="$(mktemp)"

    if [[ ! -f "$file" ]]; then
        : > "$file"
    fi

    awk -v key="$key" -v value="$value" '
    {
        if (index($0, key "=") == 1) {
            print key "=" value
            seen = 1
        } else {
            print $0
        }
    }
    END {
        if (!seen) {
            print key "=" value
        }
    }
    ' "$file" > "$tmp_file"
    mv "$tmp_file" "$file"
}

run_scenario_file() {
    local scenario_file="$1"
    local report_file=""

    if [[ -n "$ARTIFACT_PREFIX" ]]; then
        local base="$(basename "$scenario_file")"
        report_file="${ARTIFACT_PREFIX}/${base%.json}.result.json"
    fi

    if python3 dev-tools/qa/run_scenario.py \
        --loader "$LOADER" \
        --scenario "$scenario_file" \
        --host 127.0.0.1 \
        --port "$RCON_PORT" \
        --password "$RCON_PASS" \
        ${report_file:+--report "$report_file"}; then
        ok "Scenario pass: $(basename "$scenario_file")"
    else
        fail "Scenario failed: $(basename "$scenario_file")"
    fi
}

# ── 1. Build ─────────────────────────────────────────────────
if [[ $SKIP_BUILD -eq 0 ]]; then
    info "Building ${TASK_COMPILE} …"
    if bash ./gradlew "$TASK_COMPILE" --console=plain -q; then
        ok "Build succeeded"
    else
        fail "Build failed — aborting"
        exit 1
    fi
fi

# ── 2. Prepare dev server.properties ─────────────────────────
if [[ "$MODE" == "server" ]]; then
    # patch settings needed for dev testing
    upsert_property "$SERVER_PROPERTIES_FILE" "online-mode" "false"
    upsert_property "$SERVER_PROPERTIES_FILE" "enable-rcon" "true"
    upsert_property "$SERVER_PROPERTIES_FILE" "rcon.password" "$RCON_PASS"
    upsert_property "$SERVER_PROPERTIES_FILE" "rcon.port" "$RCON_PORT"
    upsert_property "$SERVER_PROPERTIES_FILE" "enforce-secure-profile" "false"

    ok "server.properties patched for dev"
    # truncate stale log so we don't false-positive on old "Done" line
    : > "$SERVER_LOG"
fi

# ── 3. Kill any existing server ───────────────────────────────
if [[ -f "$SERVER_PID_FILE" ]]; then
    OLD_PID=$(cat "$SERVER_PID_FILE")
    kill "$OLD_PID" 2>/dev/null || true
    rm -f "$SERVER_PID_FILE"
fi

cleanup() {
    if [[ -f "$SERVER_PID_FILE" ]]; then
        PID=$(cat "$SERVER_PID_FILE")
        info "Stopping server (PID $PID) …"
        kill "$PID" 2>/dev/null || true
        wait "$PID" 2>/dev/null || true
        rm -f "$SERVER_PID_FILE"
    fi
}
trap cleanup EXIT

# ── 4. Start server / client ──────────────────────────────────
if [[ "$MODE" == "server" ]]; then
    info "Starting ${PROJECT} dev server …"
    bash ./gradlew "$TASK_SERVER" --console=plain > /tmp/os-smoke-gradle.log 2>&1 &
    GRADLE_PID=$!
    echo "$GRADLE_PID" > "$SERVER_PID_FILE"

    # wait for "Done" in log
    info "Waiting for server startup (up to ${STARTUP_TIMEOUT}s) …"
    ELAPSED=0
    while ! grep -q "Done (" "$SERVER_LOG" 2>/dev/null; do
        if ! kill -0 "$GRADLE_PID" 2>/dev/null; then
            fail "Server process died before fully starting"
            echo "--- last gradle output ---"
            tail -30 /tmp/os-smoke-gradle.log
            exit 1
        fi
        if [[ $ELAPSED -ge $STARTUP_TIMEOUT ]]; then
            fail "Server startup timed out after ${STARTUP_TIMEOUT}s"
            exit 1
        fi
        sleep 2
        ELAPSED=$((ELAPSED+2))
    done
    ok "Server started in ${ELAPSED}s"
    info "Waiting 3s for RCON port to open …"
    sleep 3

else
    # client mode — just launch and monitor log
    info "Starting ${PROJECT} dev client on DISPLAY=:0 …"
    DISPLAY=:0 bash ./gradlew "$TASK_CLIENT" --console=plain > /tmp/os-smoke-gradle.log 2>&1 &
    GRADLE_PID=$!
    echo "$GRADLE_PID" > "$SERVER_PID_FILE"

    info "Waiting for client window (up to ${STARTUP_TIMEOUT}s) …"
    ELAPSED=0
    while ! grep -qE "Backend library|OpenGL|LWJGL|Initializing game" "$CLIENT_LOG" 2>/dev/null; do
        if ! kill -0 "$GRADLE_PID" 2>/dev/null; then
            fail "Client process died"
            exit 1
        fi
        if [[ $ELAPSED -ge $STARTUP_TIMEOUT ]]; then
            fail "Client startup timed out"
            exit 1
        fi
        sleep 2
        ELAPSED=$((ELAPSED+2))
    done
    ok "Client window opened — monitoring log (60s) …"
    sleep 60
    info "Client check window complete"
fi

# ── 5. RCON test suite + scenario checks (server mode only) ────
if [[ "$MODE" == "server" ]]; then
    RCON="python3 ${ROOT}/dev-tools/rcon.py --password ${RCON_PASS} --port ${RCON_PORT}"

    run_rcon() {
        local label="$1"; shift
        local result
        result=$($RCON "$@" 2>&1) && true
        echo "  cmd: $*"
        echo "  out: $result"
        echo "$result"
    }

    info "--- RCON Test Suite ---"

    # 5a. Server sanity
    OUT=$(run_rcon "server-info" "list")
    if echo "$OUT" | grep -q "players online"; then
        ok "Server responding to RCON"
    else
        fail "RCON list failed: $OUT"
    fi

    # 5b. Check mod items exist via /recipe (recipes load at startup)
    OUT=$(run_rcon "recipes" "recipe give @a orangesunshine:joint")
    echo "  recipe test: $OUT"

    # 5c. Verify mod-registered items by trying /data get entity @e[type=item,limit=1]
    #     Instead use item give to confirm registry (will just fail with "no targets" if no player)
    for ITEM in cannabis_leaf dried_cannabis_leaf flask wine_grapes; do
        OUT=$(run_rcon "item:$ITEM" "give @a orangesunshine:$ITEM 1")
        if echo "$OUT" | grep -qiE "no entity|no targets|no player|was found"; then
            ok "Item registered: orangesunshine:$ITEM (no player online, item exists)"
        elif echo "$OUT" | grep -qi "given\|gave"; then
            ok "Item registered and given: orangesunshine:$ITEM"
        elif echo "$OUT" | grep -qi "unknown\|invalid\|no such\|unrecognized"; then
            fail "Item not found in registry: orangesunshine:$ITEM — $OUT"
        else
            info "Item test for $ITEM: $OUT"
        fi
    done

    # 5e. Scenario checks via external scenario files
    if [[ -n "$SCENARIO_DIR" ]]; then
        info "Running scenario suite: $SCENARIO_DIR"
        if [[ ! -d "$SCENARIO_DIR" ]]; then
            fail "Scenario directory not found: $SCENARIO_DIR"
        else
            while IFS= read -r -d '' SCENARIO_FILE; do
                run_scenario_file "$SCENARIO_FILE"
            done < <(find "$SCENARIO_DIR" -type f -name "*.json" -print0 | sort -z)
        fi
    fi

    # 5d. Trigger server stop cleanly
    info "Sending stop command …"
    $RCON "stop" 2>&1 || true
    sleep 5
fi

# ── 6. Log analysis ───────────────────────────────────────────
info "--- Log Analysis ---"
if [[ "$MODE" == "server" ]]; then
    LOG="$SERVER_LOG"
else
    LOG="$CLIENT_LOG"
fi

if python3 dev-tools/check-logs.py "$LOG"; then
    ok "Log analysis clean"
else
    fail "Log analysis found errors"
fi

if [[ -n "$ARTIFACT_PREFIX" ]]; then
    mkdir -p "$ARTIFACT_PREFIX"
    cp "$LOG" "$ARTIFACT_PREFIX/latest.log" 2>/dev/null || true
fi

# ── 7. Final result ───────────────────────────────────────────
echo ""
if [[ $FAILURES -eq 0 ]]; then
    ok "=== All smoke tests passed ==="
    exit 0
else
    fail "=== $FAILURES test(s) failed ==="
    exit 1
fi
