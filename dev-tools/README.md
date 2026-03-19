# dev-tools

Automated development and smoke-test tooling for Orange Sunshine.

## smoke-test.sh

Main test pipeline. Builds the mod, starts a Fabric dev server, runs RCON commands to verify the mod loaded correctly, and analyses the server log for errors.

```bash
# Full run (build + server + RCON + log check)
bash dev-tools/smoke-test.sh

# Skip build step (use cached jar)
bash dev-tools/smoke-test.sh --skip-build

# Client mode — launch dev client on DISPLAY=:0 and monitor its log
bash dev-tools/smoke-test.sh --client
```

**What it checks:**
- `:fabric:compileJava` succeeds
- Server starts and prints "Done" within 120 s
- RCON responds to `list`
- Key mod items exist in the registry (`give @a orangesunshine:<item>`)
- Log contains no hard errors (Caused by, FATAL, crash reports)

**Requirements:**
- `JAVA_HOME=/home/moriz/.jdks/temurin-21`
- A valid `fabric/run/server.properties` (created on first `runServer`)
- Python 3 (stdlib only)

The script patches `server.properties` before each run:
- `online-mode=false`
- `enable-rcon=true`, `rcon.password=devtest`, `rcon.port=25575`
- `enforce-secure-profile=false`

---

## rcon.py

Minimal RCON client (Python stdlib, no dependencies).

```bash
# Send a single command
python3 dev-tools/rcon.py list
python3 dev-tools/rcon.py give @a orangesunshine:flask 1

# Options
python3 dev-tools/rcon.py --host 127.0.0.1 --port 25575 --password devtest <command>
```

Retries the connection up to 5 times with a 2 s delay (useful while the server is still starting).

---

## check-logs.py

Scans a Minecraft log file for problems and good signals.

```bash
python3 dev-tools/check-logs.py fabric/run/logs/latest.log
```

Exits `0` if the log looks clean, `1` if hard errors were found.

**Hard-fail patterns:** `Caused by`, `\[FATAL\]`, `Exception in`, crash report headers.
**Good signals:** mod loading confirmation, "Done" startup message.
