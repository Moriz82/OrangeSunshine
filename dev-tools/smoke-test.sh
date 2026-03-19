#!/usr/bin/env bash
# ============================================================
# OrangeSunshine dev smoke-test pipeline
#
# Usage:
#   ./dev-tools/smoke-test.sh [--skip-build] [--client]
#
# Modes:
#   (default)     Run :fabric:runServer, wait for startup,
#                 run RCON test commands, analyse logs.
#   --client      Run :fabric:runClient on DISPLAY=:0 instead,
#                 then analyse the client log.
#   --skip-build  Skip the build step (use cached jar).
# ============================================================

set -euo pipefail
cd "$(dirname "$0")/.."
ROOT="$(pwd)"

export JAVA_HOME=/home/moriz/.jdks/temurin-21
export PATH="$JAVA_HOME/bin:$PATH"

RCON_PORT=25575
RCON_PASS="devtest"
SERVER_LOG="fabric/run/logs/latest.log"
SERVER_PID_FILE="/tmp/os-smoke-server.pid"
STARTUP_TIMEOUT=120   # seconds to wait for server "Done"
MODE="server"
SKIP_BUILD=0

for arg in "$@"; do
    case "$arg" in
        --client)     MODE="client" ;;
        --skip-build) SKIP_BUILD=1 ;;
    esac
done

# ── colours ──────────────────────────────────────────────────
RED='\033[0;31m'; GRN='\033[0;32m'; YEL='\033[1;33m'; NC='\033[0m'
ok()   { echo -e "${GRN}[PASS]${NC} $*"; }
fail() { echo -e "${RED}[FAIL]${NC} $*"; FAILURES=$((FAILURES+1)); }
info() { echo -e "${YEL}[INFO]${NC} $*"; }
FAILURES=0

# ── 1. Build ─────────────────────────────────────────────────
if [[ $SKIP_BUILD -eq 0 ]]; then
    info "Building :fabric:compileJava …"
    if bash ./gradlew :fabric:compileJava --console=plain -q; then
        ok "Build succeeded"
    else
        fail "Build failed — aborting"
        exit 1
    fi
fi

# ── 2. Prepare dev server.properties ─────────────────────────
if [[ "$MODE" == "server" ]]; then
    SP="fabric/run/server.properties"
    # patch settings needed for dev testing
    sed -i \
        -e "s/^online-mode=.*/online-mode=false/" \
        -e "s/^enable-rcon=.*/enable-rcon=true/" \
        -e "s/^rcon\.password=.*/rcon.password=${RCON_PASS}/" \
        -e "s/^rcon\.port=.*/rcon.port=${RCON_PORT}/" \
        -e "s/^enforce-secure-profile=.*/enforce-secure-profile=false/" \
        "$SP"
    ok "server.properties patched for dev"
    # truncate stale log so we don't false-positive on old "Done" line
    mkdir -p fabric/run/logs
    > "$SERVER_LOG"
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
    info "Starting Fabric dev server …"
    bash ./gradlew :fabric:runServer --console=plain > /tmp/os-smoke-gradle.log 2>&1 &
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
    info "Starting Fabric dev client on DISPLAY=:0 …"
    DISPLAY=:0 bash ./gradlew :fabric:runClient --console=plain > /tmp/os-smoke-gradle.log 2>&1 &
    GRADLE_PID=$!
    echo "$GRADLE_PID" > "$SERVER_PID_FILE"

    info "Waiting for client window (up to ${STARTUP_TIMEOUT}s) …"
    CLIENT_LOG="fabric/run/logs/latest.log"
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

# ── 5. RCON test suite (server mode only) ────────────────────
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
    LOG="fabric/run/logs/latest.log"
fi

if python3 dev-tools/check-logs.py "$LOG"; then
    ok "Log analysis clean"
else
    fail "Log analysis found errors"
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
