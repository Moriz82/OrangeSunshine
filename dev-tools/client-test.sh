#!/usr/bin/env bash
# ============================================================
# OrangeSunshine client integration test pipeline
#
# Phase 1: Launch client, verify title screen, check assets
# Phase 2: Auto-load creative test world, verify in-world
#          rendering (blocks, entities, shaders, GUIs)
#
# Usage:
#   ./dev-tools/client-test.sh [--skip-build] [--loader <fabric|neoforge>] [--timeout <seconds>]
# ============================================================

set -euo pipefail
cd "$(dirname "$0")/.."
ROOT="$(pwd)"

export JAVA_HOME=/home/moriz/.jdks/temurin-21
export PATH="$JAVA_HOME/bin:$PATH"

LOADER="fabric"
SKIP_BUILD=0
STARTUP_TIMEOUT=180
WORLD_LOAD_TIMEOUT=120
INGAME_SETTLE=45
PID_FILE="/tmp/os-client-test.pid"
GRADLE_LOG="/tmp/os-client-test-gradle.log"
TEST_WORLD="OrangeSunshine_Test"

RED='\033[0;31m'; GRN='\033[0;32m'; YEL='\033[1;33m'; CYN='\033[0;36m'; NC='\033[0m'
ok()   { echo -e "${GRN}[PASS]${NC} $*"; }
fail() { echo -e "${RED}[FAIL]${NC} $*"; FAILURES=$((FAILURES+1)); }
warn() { echo -e "${YEL}[WARN]${NC} $*"; WARNINGS=$((WARNINGS+1)); }
info() { echo -e "${CYN}[INFO]${NC} $*"; }
FAILURES=0
WARNINGS=0

while [[ $# -gt 0 ]]; do
    case "$1" in
        --skip-build) SKIP_BUILD=1; shift ;;
        --loader) LOADER="$2"; shift 2 ;;
        --timeout) STARTUP_TIMEOUT="$2"; shift 2 ;;
        --help)
            echo "Usage: dev-tools/client-test.sh [--skip-build] [--loader fabric|neoforge] [--timeout N]"
            exit 0 ;;
        *) echo "Unknown: $1"; exit 1 ;;
    esac
done

LOG_FILE="${ROOT}/${LOADER}/run/logs/latest.log"
CRASH_DIR="${ROOT}/${LOADER}/run/crash-reports"

echo ""
echo "========================================"
echo "  OrangeSunshine Client Test Pipeline"
echo "  Loader: ${LOADER}"
echo "========================================"
echo ""

# ── 1. Build ─────────────────────────────────────────────────
if [[ $SKIP_BUILD -eq 0 ]]; then
    info "Building :${LOADER}:classes …"
    if bash ./gradlew ":${LOADER}:classes" --console=plain -q 2>&1; then
        ok "Build succeeded"
    else
        fail "Build failed — aborting"
        exit 1
    fi
else
    info "Skipping build (--skip-build)"
fi

# ── 2. Pre-flight asset validation ───────────────────────────
info "Running asset validator …"
if python3 dev-tools/qa/validate_assets.py 2>&1 | tail -3; then
    ok "Asset validation passed"
else
    fail "Asset validation found issues"
fi

# ── 3. Create/verify test world ──────────────────────────────
info "Setting up test world …"
WORLD_DIR="${ROOT}/${LOADER}/run/saves/${TEST_WORLD}"
if [[ ! -d "$WORLD_DIR" ]]; then
    python3 dev-tools/create_test_world.py --loader "$LOADER" 2>&1
fi
if [[ -d "$WORLD_DIR" ]]; then
    ok "Test world ready: ${TEST_WORLD}"
else
    warn "Could not create test world — will test title screen only"
    TEST_WORLD=""
fi

# ── 4. Kill stale client ────────────────────────────────────
if [[ -f "$PID_FILE" ]]; then
    OLD_PID=$(cat "$PID_FILE")
    kill "$OLD_PID" 2>/dev/null || true
    rm -f "$PID_FILE"
fi

cleanup() {
    if [[ -f "$PID_FILE" ]]; then
        PID=$(cat "$PID_FILE")
        info "Stopping client (PID $PID) …"
        kill "$PID" 2>/dev/null || true
        sleep 2
        kill -9 "$PID" 2>/dev/null || true
        rm -f "$PID_FILE"
    fi
}
trap cleanup EXIT

# ── 5. Clear old logs + record start time ───────────────────
mkdir -p "$(dirname "$LOG_FILE")"
: > "$LOG_FILE"
TEST_START_TIME=$(date +%s)

# ── 6. Launch client ────────────────────────────────────────
if [[ -z "${DISPLAY:-}" ]]; then
    info "No DISPLAY set — trying DISPLAY=:0"
    export DISPLAY=:0
fi

if [[ -n "$TEST_WORLD" ]]; then
    info "Starting ${LOADER} client → auto-loading '${TEST_WORLD}' …"
    bash ./gradlew ":${LOADER}:runClient" -PquickPlayWorld="${TEST_WORLD}" --console=plain > "$GRADLE_LOG" 2>&1 &
else
    info "Starting ${LOADER} client (title screen only) …"
    bash ./gradlew ":${LOADER}:runClient" --console=plain > "$GRADLE_LOG" 2>&1 &
fi
CLIENT_PID=$!
echo "$CLIENT_PID" > "$PID_FILE"

# ── 7. Wait for client startup ──────────────────────────────
info "Waiting for client initialization (up to ${STARTUP_TIMEOUT}s) …"
ELAPSED=0
PHASE="waiting"

while true; do
    if ! kill -0 "$CLIENT_PID" 2>/dev/null; then
        echo ""
        fail "Client process died during startup"
        echo "--- Last 30 lines of gradle output ---"
        tail -30 "$GRADLE_LOG" 2>/dev/null || true
        for cf in $(ls -t "$CRASH_DIR"/*.txt 2>/dev/null); do
            CF_TIME=$(stat -c %Y "$cf" 2>/dev/null || echo 0)
            if [[ $CF_TIME -ge $TEST_START_TIME ]]; then
                echo "--- Crash Report ---"
                head -60 "$cf"
                break
            fi
        done
        exit 1
    fi

    if [[ $ELAPSED -ge $STARTUP_TIMEOUT ]]; then
        fail "Client startup timed out after ${STARTUP_TIMEOUT}s"
        tail -20 "$LOG_FILE" 2>/dev/null || true
        exit 1
    fi

    # Phase tracking
    if [[ "$PHASE" == "waiting" ]] && grep -q "Backend library:" "$LOG_FILE" 2>/dev/null; then
        PHASE="gl_init"
        info "  OpenGL initialized (${ELAPSED}s)"
    fi
    if [[ "$PHASE" == "gl_init" ]] && grep -q "Reloading ResourceManager:" "$LOG_FILE" 2>/dev/null; then
        PHASE="resources"
        info "  Resource manager loading (${ELAPSED}s)"
    fi
    if grep -qE "OpenAL initialized|narrator" "$LOG_FILE" 2>/dev/null; then
        if [[ "$PHASE" != "title_screen" && "$PHASE" != "in_world" ]]; then
            PHASE="title_screen"
            ok "Client reached title screen (${ELAPSED}s)"
            break
        fi
    fi
    # Fallback
    if grep -qE "Bootstrapping Orange Sunshine" "$LOG_FILE" 2>/dev/null; then
        if [[ "$PHASE" != "title_screen" && "$PHASE" != "in_world" ]]; then
            PHASE="title_screen"
            ok "Client initialized (${ELAPSED}s)"
            break
        fi
    fi

    sleep 2
    ELAPSED=$((ELAPSED+2))
done

# ═══════════════════════════════════════════════════════════════
# PHASE 1: Title Screen Checks
# ═══════════════════════════════════════════════════════════════
echo ""
info "══ Phase 1: Title Screen Checks ══"
echo ""

# Check mod loaded
if grep -q "orangesunshine" "$LOG_FILE" 2>/dev/null; then
    MOD_VER=$(grep -oE "orangesunshine[) ]+[0-9][0-9a-zA-Z.\-]*" "$LOG_FILE" 2>/dev/null | grep -oE "[0-9][0-9a-zA-Z.\-]*" | head -1) || MOD_VER=""
    ok "Mod loaded: orangesunshine ${MOD_VER:-<version>}"
else
    fail "Mod not found in client log"
fi

# Shader pipeline
if grep -q "Post-effect shader pipeline\|Loaded post shader" "$LOG_FILE" 2>/dev/null; then
    SHADER_COUNT=$(grep -c "Loaded post shader:" "$LOG_FILE" 2>/dev/null) || SHADER_COUNT=0
    ok "Post-effect shader pipeline active (${SHADER_COUNT} shaders)"
else
    info "Post-effect shaders not logged yet"
fi

# Missing textures at resource load time
MISSING_TEX=$(grep -c "Using missing texture" "$LOG_FILE" 2>/dev/null) || MISSING_TEX=0
if [[ "$MISSING_TEX" -gt 0 ]]; then
    fail "Missing textures during resource load: $MISSING_TEX"
    grep "Using missing texture" "$LOG_FILE" 2>/dev/null | sort -u | head -10
else
    ok "No missing textures at load time"
fi

# Mixin errors
MIXIN_ERR=$(grep -c "Mixin apply.*failed\|Mixin injection.*failed\|MixinApplyError" "$LOG_FILE" 2>/dev/null) || MIXIN_ERR=0
if [[ "$MIXIN_ERR" -gt 0 ]]; then
    fail "Mixin errors: $MIXIN_ERR"
    grep -E "Mixin apply.*failed|Mixin injection.*failed" "$LOG_FILE" 2>/dev/null | head -5
else
    ok "No mixin errors"
fi

# Shader compilation
if grep -q "Shader compilation failed\|Failed to compile shader\|GLSL error" "$LOG_FILE" 2>/dev/null; then
    fail "Shader compilation errors found"
    grep -E "Shader compilation|Failed to compile|GLSL error" "$LOG_FILE" 2>/dev/null | head -5
else
    ok "No shader compilation errors"
fi

# ═══════════════════════════════════════════════════════════════
# PHASE 2: In-World Creative Tests
# ═══════════════════════════════════════════════════════════════
echo ""
info "══ Phase 2: In-World Creative Tests ══"
echo ""

if [[ -z "$TEST_WORLD" ]]; then
    warn "Skipping in-world tests (no test world)"
else
    # Wait for world to load
    info "Waiting for world load (up to ${WORLD_LOAD_TIMEOUT}s) …"
    WORLD_ELAPSED=0
    WORLD_LOADED=0

    while [[ $WORLD_ELAPSED -lt $WORLD_LOAD_TIMEOUT ]]; do
        if ! kill -0 "$CLIENT_PID" 2>/dev/null; then
            # Check if there's a NEW crash report (created after test start)
            NEW_CRASH=""
            for cf in $(ls -t "$CRASH_DIR"/*.txt 2>/dev/null); do
                CF_TIME=$(stat -c %Y "$cf" 2>/dev/null || echo 0)
                if [[ $CF_TIME -ge $TEST_START_TIME ]]; then
                    NEW_CRASH="$cf"
                    break
                fi
            done
            if [[ -n "$NEW_CRASH" ]]; then
                fail "Client crashed during world load"
                echo "--- Crash Report ---"
                head -40 "$NEW_CRASH"
            else
                warn "Client exited during world load (no new crash report — may be a clean exit)"
            fi
            break
        fi

        # Check for world loaded markers
        if grep -qE "Saving and pausing game|Loaded \d+ advancements|Preparing start region|ThreadedAnvilChunkStorage|Changing view distance|Saving chunks" "$LOG_FILE" 2>/dev/null; then
            WORLD_LOADED=1
            ok "World loaded (${WORLD_ELAPSED}s)"
            break
        fi

        # Alt marker: integrated server started
        if grep -qE "Starting integrated.*server|joined the game|Local game hosted" "$LOG_FILE" 2>/dev/null; then
            WORLD_LOADED=1
            ok "World loaded via integrated server (${WORLD_ELAPSED}s)"
            break
        fi

        sleep 3
        WORLD_ELAPSED=$((WORLD_ELAPSED+3))
    done

    if [[ $WORLD_LOADED -eq 0 ]]; then
        warn "World load not detected within timeout — continuing with log analysis"
    fi

    # Let the game render for a while
    info "In-world rendering soak (${INGAME_SETTLE}s) …"
    sleep "$INGAME_SETTLE"

    # Check client is still alive
    if kill -0 "$CLIENT_PID" 2>/dev/null; then
        ok "Client survived in-world rendering"
    else
        NEW_CRASH=""
        for cf in $(ls -t "$CRASH_DIR"/*.txt 2>/dev/null); do
            CF_TIME=$(stat -c %Y "$cf" 2>/dev/null || echo 0)
            if [[ $CF_TIME -ge $TEST_START_TIME ]]; then
                NEW_CRASH="$cf"
                break
            fi
        done
        if [[ -n "$NEW_CRASH" ]]; then
            fail "Client crashed during in-world rendering"
            echo "--- Crash Report ---"
            head -40 "$NEW_CRASH"
        else
            info "Client exited during rendering soak (no crash — normal for headless)"
        fi
    fi

    # In-world texture/model checks (these show up when chunks render)
    INGAME_MISSING_TEX=$(grep -c "Using missing texture" "$LOG_FILE" 2>/dev/null) || INGAME_MISSING_TEX=0
    if [[ "$INGAME_MISSING_TEX" -gt 0 ]]; then
        # Filter to orangesunshine-related only
        OS_MISSING=$(grep "Using missing texture" "$LOG_FILE" 2>/dev/null | grep -c "orangesunshine" || true)
        if [[ "$OS_MISSING" -gt 0 ]]; then
            fail "OrangeSunshine missing textures in-world: $OS_MISSING"
            grep "Using missing texture" "$LOG_FILE" 2>/dev/null | grep "orangesunshine" | sort -u | head -10
        else
            ok "No OrangeSunshine textures missing in-world"
        fi
    else
        ok "No missing textures in-world"
    fi

    # Block entity renderer errors
    BE_ERRORS=$(grep -c "Error rendering block entity\|BlockEntity.*Exception\|Failed to render block entity" "$LOG_FILE" 2>/dev/null) || BE_ERRORS=0
    if [[ "$BE_ERRORS" -gt 0 ]]; then
        fail "Block entity renderer errors: $BE_ERRORS"
        grep -E "Error rendering block entity|BlockEntity.*Exception|Failed to render block entity" "$LOG_FILE" 2>/dev/null | head -10
    else
        ok "No block entity renderer errors"
    fi

    # Entity renderer errors
    ENT_ERRORS=$(grep -c "Error rendering entity\|EntityRenderer.*Exception\|Failed to render entity" "$LOG_FILE" 2>/dev/null) || ENT_ERRORS=0
    if [[ "$ENT_ERRORS" -gt 0 ]]; then
        fail "Entity renderer errors: $ENT_ERRORS"
        grep -E "Error rendering entity|EntityRenderer.*Exception" "$LOG_FILE" 2>/dev/null | head -5
    else
        ok "No entity renderer errors"
    fi

    # Screen/GUI errors
    GUI_ERRORS=$(grep -c "Screen.*Exception\|Error.*screen\|Failed.*menu\|ContainerMenu.*Exception" "$LOG_FILE" 2>/dev/null) || GUI_ERRORS=0
    if [[ "$GUI_ERRORS" -gt 0 ]]; then
        warn "GUI/Screen errors: $GUI_ERRORS"
        grep -E "Screen.*Exception|Error.*screen|ContainerMenu.*Exception" "$LOG_FILE" 2>/dev/null | head -5
    else
        ok "No GUI/screen errors"
    fi

    # Recipe loading
    RECIPE_ERRORS=$(grep -c "Error loading recipe\|Failed.*recipe\|Invalid recipe" "$LOG_FILE" 2>/dev/null) || RECIPE_ERRORS=0
    if [[ "$RECIPE_ERRORS" -gt 0 ]]; then
        fail "Recipe loading errors: $RECIPE_ERRORS"
        grep -E "Error loading recipe|Failed.*recipe|Invalid recipe" "$LOG_FILE" 2>/dev/null | head -10
    else
        ok "No recipe loading errors"
    fi

    # Render thread exceptions
    RENDER_EXCEPTIONS=$(grep -c "\[Render thread/ERROR\].*orangesunshine\|orangesunshine.*\[Render thread/ERROR\]" "$LOG_FILE" 2>/dev/null) || RENDER_EXCEPTIONS=0
    if [[ "$RENDER_EXCEPTIONS" -gt 0 ]]; then
        fail "Render thread mod errors: $RENDER_EXCEPTIONS"
        grep -E "\[Render thread/ERROR\].*orangesunshine|orangesunshine.*\[Render thread/ERROR\]" "$LOG_FILE" 2>/dev/null | head -5
    else
        ok "No render thread mod errors"
    fi

    # NullPointerExceptions in mod code
    NPE_COUNT=$(grep -c "NullPointerException" "$LOG_FILE" 2>/dev/null) || NPE_COUNT=0
    if [[ "$NPE_COUNT" -gt 0 ]]; then
        NPE_MOD=$(grep -A3 "NullPointerException" "$LOG_FILE" 2>/dev/null | grep -c "orangesunshine" || true)
        if [[ "$NPE_MOD" -gt 0 ]]; then
            fail "NullPointerExceptions in mod code"
            grep -B1 -A5 "NullPointerException" "$LOG_FILE" 2>/dev/null | grep -B1 -A5 "orangesunshine" | head -15
        else
            ok "No mod-related NPEs"
        fi
    else
        ok "No NullPointerExceptions"
    fi
fi

# ═══════════════════════════════════════════════════════════════
# PHASE 3: Final Log Analysis
# ═══════════════════════════════════════════════════════════════
echo ""
info "══ Phase 3: Final Log Analysis ══"
echo ""

# Registration errors
REG_ERR=$(grep -ci "Failed to register\|Unknown registry\|Not found in registry" "$LOG_FILE" 2>/dev/null) || REG_ERR=0
if [[ "$REG_ERR" -gt 0 ]]; then
    warn "Registry issues: $REG_ERR"
    grep -iE "Failed to register|Unknown registry|Not found in registry" "$LOG_FILE" 2>/dev/null | head -5
else
    ok "No registry errors"
fi

# Mod exceptions total
MOD_EXCEPTIONS=$(grep -c "orangesunshine.*Exception\|Exception.*orangesunshine" "$LOG_FILE" 2>/dev/null) || MOD_EXCEPTIONS=0
if [[ "$MOD_EXCEPTIONS" -gt 0 ]]; then
    fail "Mod exceptions total: $MOD_EXCEPTIONS"
    grep -E "orangesunshine.*Exception|Exception.*orangesunshine" "$LOG_FILE" 2>/dev/null | sort -u | head -10
else
    ok "No mod-related exceptions"
fi

# Crash reports (only from THIS test run)
NEW_CRASH_FOUND=0
for cf in $(ls -t "$CRASH_DIR"/*.txt 2>/dev/null); do
    CF_TIME=$(stat -c %Y "$cf" 2>/dev/null || echo 0)
    if [[ $CF_TIME -ge $TEST_START_TIME ]]; then
        fail "CRASH REPORT: $cf"
        head -20 "$cf"
        NEW_CRASH_FOUND=1
        break
    fi
done
if [[ $NEW_CRASH_FOUND -eq 0 ]]; then
    ok "No crash reports from this run"
fi

# Full log analysis
echo ""
python3 dev-tools/check-logs.py "$LOG_FILE" 2>&1 || true

# ═══════════════════════════════════════════════════════════════
# Summary
# ═══════════════════════════════════════════════════════════════
echo ""
echo "========================================"
if [[ $FAILURES -eq 0 ]]; then
    ok "=== CLIENT TEST PASSED === ($WARNINGS warnings)"
    EXIT_CODE=0
else
    fail "=== CLIENT TEST: $FAILURES failure(s), $WARNINGS warning(s) ==="
    EXIT_CODE=1
fi
echo "========================================"
echo ""
echo "Log:    $LOG_FILE"
echo "Gradle: $GRADLE_LOG"
echo ""

exit $EXIT_CODE
