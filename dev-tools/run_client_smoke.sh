#!/usr/bin/env bash
# Runs Fabric :fabric:runClient under a timeout, captures logs, and verifies the mod client bootstraps.
# Usage: from repo root — ./dev-tools/run_client_smoke.sh
# Requires: Java 21+, optional xvfb-run for headless Linux CI.

set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
export JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-21-openjdk}"

LOG_DIR="$ROOT/build/smoke"
mkdir -p "$LOG_DIR"
LOG="$LOG_DIR/client_smoke.log"
TIMEOUT_SEC="${ORANGE_SMOKE_TIMEOUT:-90}"

RUN=(timeout "$TIMEOUT_SEC")
if command -v xvfb-run >/dev/null 2>&1; then
  RUN=(xvfb-run -a "${RUN[@]}")
fi

set +e
"${RUN[@]}" ./gradlew :fabric:runClient --no-daemon --console=plain >"$LOG" 2>&1
EXIT=$?
set -e

echo "---- tail $LOG ----"
tail -80 "$LOG" || true

if grep -q "CLIENT_SMOKE_OK" "$LOG"; then
  echo "smoke: CLIENT_SMOKE_OK found — client initializer ran."
else
  echo "smoke: FAILED — log missing CLIENT_SMOKE_OK (client did not finish bootstrap in time or crashed early)."
  exit 1
fi

if grep -Eqi "Minecraft has crashed|Fatal error in Fabric Loader|Reported exception thrown|Uncaught exception in thread \"main\"" "$LOG"; then
  echo "smoke: FAILED — crash string in log."
  exit 1
fi

# Post-chain / UBO regressions (1.21.x Blaze3D)
if grep -Fq "Buffer needs USAGE_COPY_DST" "$LOG"; then
  echo "smoke: FAILED — GpuBuffer USAGE_COPY_DST regression (LoadedShader UBO upload)."
  exit 1
fi

if grep -Fq "Game crashed!" "$LOG" || grep -Fq "#@!@# Game crashed!" "$LOG"; then
  echo "smoke: FAILED — Game crashed marker in log."
  exit 1
fi

# Shader pipeline must finish resource reload (catches JSON/compile failures)
if ! grep -Fq "Post-effect shader pipeline:" "$LOG"; then
  echo "smoke: FAILED — log missing Post-effect shader pipeline line (ShaderLoader did not finish)."
  exit 1
fi

# 124 = timeout from GNU timeout — acceptable if bootstrap marker present (game keeps running)
if [[ "$EXIT" -ne 0 && "$EXIT" -ne 124 ]]; then
  echo "smoke: FAILED — gradle/run exited with $EXIT"
  exit 1
fi

echo "smoke: OK"
exit 0
