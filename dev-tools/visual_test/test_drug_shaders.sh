#!/bin/bash
# Automated visual test for drug shader effects
# This script launches the game, waits for it to be ready, then checks logs for shader activation

set -e

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
LOG_FILE="$ROOT/fabric/run/logs/latest.log"
TIMEOUT=180  # 3 minutes max

echo "=== OrangeSunshine Visual Shader Test ==="
echo "Root: $ROOT"
echo ""

# Clean old log
rm -f "$LOG_FILE"

# Start the game in background
echo "[INFO] Starting Fabric client..."
cd "$ROOT"
./gradlew :fabric:runClient --no-daemon &
GAME_PID=$!

# Wait for game to start
echo "[INFO] Waiting for game to initialize..."
sleep 30

# Check if game started successfully
check_log() {
    if [ -f "$LOG_FILE" ]; then
        grep -q "$1" "$LOG_FILE" 2>/dev/null
        return $?
    fi
    return 1
}

# Wait for shaders to load
WAIT_START=$(date +%s)
while ! check_log "Post-effect shader pipeline:"; do
    ELAPSED=$(($(date +%s) - WAIT_START))
    if [ $ELAPSED -gt $TIMEOUT ]; then
        echo "[FAIL] Timeout waiting for shader pipeline"
        kill $GAME_PID 2>/dev/null || true
        exit 1
    fi
    sleep 2
done

echo "[PASS] Shader pipeline loaded"

# Check world_waves shader loaded
if check_log "world_waves.json"; then
    echo "[PASS] world_waves shader loaded"
else
    echo "[FAIL] world_waves shader NOT loaded"
    kill $GAME_PID 2>/dev/null || true
    exit 1
fi

# Count shaders
SHADER_COUNT=$(grep -c "Loaded post shader:" "$LOG_FILE" 2>/dev/null || echo "0")
echo "[INFO] Total shaders loaded: $SHADER_COUNT"

# Check for any shader compilation errors
if grep -q "Shader compilation failed\|Failed to compile\|GLSL error" "$LOG_FILE" 2>/dev/null; then
    echo "[FAIL] Shader compilation errors found:"
    grep "Shader compilation\|Failed to compile\|GLSL error" "$LOG_FILE" | head -5
    kill $GAME_PID 2>/dev/null || true
    exit 1
else
    echo "[PASS] No shader compilation errors"
fi

# Wait for player to be in world (check for drug client tick logs)
echo "[INFO] Waiting for player to enter world..."
WAIT_START=$(date +%s)
while ! check_log "drug client tick"; do
    ELAPSED=$(($(date +%s) - WAIT_START))
    if [ $ELAPSED -gt 90 ]; then
        echo "[WARN] No drug client tick found - player may not be in world"
        break
    fi
    sleep 5
done

# Let the game run for a bit more to see if effects activate
echo "[INFO] Monitoring for visual effects..."
sleep 30

# Check for any world_waves activation
if grep -q "world_waves ACTIVE" "$LOG_FILE" 2>/dev/null; then
    echo "[PASS] world_waves shader ACTIVE - visual effects working!"
    grep "world_waves ACTIVE" "$LOG_FILE" | tail -3
else
    echo "[INFO] world_waves not active (requires high drug levels - this is expected for passive test)"
    
    # Check what drug levels were
    DRUG_LEVELS=$(grep "drug client tick" "$LOG_FILE" 2>/dev/null | tail -1)
    if [ -n "$DRUG_LEVELS" ]; then
        echo "[INFO] Last drug levels: $DRUG_LEVELS"
    fi
fi

# Check simple_effects shader (color rotation etc)
if grep -q "simple_effects\|QuickColorRotation\|SlowColorRotation" "$LOG_FILE" 2>/dev/null; then
    echo "[INFO] simple_effects shader data found"
fi

echo ""
echo "=== Test Complete ==="
echo "[INFO] Stopping game..."
kill $GAME_PID 2>/dev/null || true
wait $GAME_PID 2>/dev/null || true

echo "[PASS] Visual shader test completed successfully"
echo ""
echo "Summary:"
echo "  - Shader pipeline: OK"
echo "  - world_waves shader: LOADED"
echo "  - Shader count: $SHADER_COUNT"
echo ""
echo "To fully test visual effects, run the game manually and use:"
echo "  /drug @s set orangesunshine:lsd 1.0"
echo "  /drug @s set orangesunshine:peyote 1.0"
