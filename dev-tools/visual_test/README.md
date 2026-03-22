# Orange Sunshine — Automated Visual & Drug-State Testing

## Overview

This test suite validates that the Orange Sunshine Fabric mod compiles cleanly, that the
client bootstraps without crashing (bootstrap markers written to the log), and that drug
debug log lines appear when a player consumes a drug with visual debug logging enabled.
It does **not** perform pixel-level rendering comparisons or fully automated in-world
gameplay — those require a connected player and are treated as stretch goals documented
below. The suite is structured in four tiers, from a fast CI-safe compile check up to
a full screenshot-capture run that needs `xvfb` and `ffmpeg`.

## Prerequisites

| Requirement | Required? | Notes |
|---|---|---|
| Java 21+ | Required | Must be on `PATH` or set via `JAVA_HOME` |
| Python 3.9+ | Required | Standard library only; no pip install needed for tiers 1–3 |
| xvfb-run | Optional | Headless Linux CI (tier 2); auto-detected by `run_client_smoke.sh` |
| ffmpeg | Optional | Screen-recording for tier 4 frame capture |
| Pillow (`pip install Pillow`) | Optional | Image comparison in `screenshot_gate.py --compare` (tier 4) |

## Quick Start

### Tier 1: Compile only (fastest, CI-safe, no display needed)

```bash
python3 dev-tools/visual_test/run_visual_tests.py --tier compile
# or via Gradle:
./gradlew :fabric:visualSmokeCompile
```

Runs `:fabric:compileJava`. Exit 0 = compile passed; exit 1 = compile failed.

### Tier 2: Compile + client bootstrap (needs xvfb or a live display)

```bash
python3 dev-tools/visual_test/run_visual_tests.py --tier smoke
# or via Gradle:
./gradlew :fabric:visualSmokeTest
```

Compiles, then runs `dev-tools/run_client_smoke.sh`, which launches
`:fabric:runClient` under a timeout (default 90 s). Verifies that
`CLIENT_SMOKE_OK` appears in the log. Optional bootstrap markers are
reported as informational notices, not failures.

### Tier 3: Full server drug registry (needs a running Minecraft server)

```bash
python3 dev-tools/visual_test/run_visual_tests.py --tier server
```

Compiles, then runs `dev-tools/smoke-test.sh --skip-build --loader fabric
--scenario-dir dev-tools/qa/scenarios/runtime`. Starts a Fabric dev server,
executes RCON checks (item registry, recipe registry), runs JSON scenario
files, and analyses the server log.

### Tier 4: Full suite with screenshot capture (needs xvfb + ffmpeg)

```bash
python3 dev-tools/visual_test/run_visual_tests.py --tier full
```

Runs tier 3, then calls `dev-tools/visual_test/screenshot_gate.py capture` if
that script exists. Screenshot capture is a future deliverable (Unit 5); when
the script is absent the step is skipped with an informational notice.

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `ORANGE_SMOKE_TIMEOUT` | `90` | Seconds before `run_client_smoke.sh` kills the client process |
| `DISPLAY` | auto (xvfb) | X display for `smoke-test.sh --client`; `run_client_smoke.sh` uses `xvfb-run -a` to auto-assign |
| `JAVA_HOME` | `/usr/lib/jvm/java-21-openjdk` | JDK path; overridden in `smoke-test.sh` to `~/.jdks/temurin-21` |
| `NO_COLOR` | (unset) | Set to any non-empty value to disable ANSI colour output |

The orchestrator also accepts `--no-color` and `--log PATH` flags directly
(these are CLI flags, not environment variables):

```bash
python3 dev-tools/visual_test/run_visual_tests.py --tier smoke --log /tmp/myrun.log --no-color
```

## Expected Artifacts

| Path | Produced by | Contents |
|---|---|---|
| `build/smoke/client_smoke.log` | `run_client_smoke.sh` | Full stdout + stderr of the client Gradle run |
| `build/smoke/frames/` | `screenshot_gate.py` (tier 4) | Captured screenshots |
| `dev-tools/qa/artifacts/<timestamp>/` | `smoke-test.sh --artifact-prefix` | Scenario JSON reports, copy of `latest.log` |

## Log Markers

`run_visual_tests.py` (tier 2) and `run_client_smoke.sh` scan
`build/smoke/client_smoke.log` for the following strings.

| Marker | When logged | Meaning |
|---|---|---|
| `CLIENT_SMOKE_OK` | Client init | Full bootstrap succeeded — **required for tier 2 to pass** |
| `S2C_RECEIVERS_REGISTERED` | Client init | Networking S2C receivers registered |
| `RENDERERS_OK` | Client init | Block/entity renderers registered |
| `PARTICLES_OK` | Client init | Particle factories registered |
| `SHADERS_OK` | Client init | Shader system initialized |
| `[OrangeSunshine] drug client tick` | In-world (every ~2 s) | Drug renderer updating (requires player in-world with debug logging on) |
| `[OrangeSunshine] S2C drug` | In-world | Drug sync packet received from server |

`S2C_RECEIVERS_REGISTERED`, `RENDERERS_OK`, `PARTICLES_OK`, and `SHADERS_OK`
are **optional** in automated runs — they are reported as informational notices
and do not cause a tier-2 failure if absent. This is expected during an
in-progress port.

## Interpreting Failures

### Tier 1 — compile failure

The build output is printed to the terminal by Gradle. Common causes:

- Missing import after a Mojmap migration step — check the class named in the
  compiler error.
- `@ModifyVariable` or mixin with a wrong descriptor — confirm the target
  method signature against Yarn / Mojmap.
- `setId()` missing on a `Block.Properties` or `Item.Properties` — required
  since 1.21.

### Tier 2 — `CLIENT_SMOKE_OK` not found

1. Check the tail of `build/smoke/client_smoke.log` for a Java exception or
   `Fabric Loader` error.
2. If the log ends abruptly, the client may have crashed before reaching the
   client initializer. Look for `Caused by:` lines near the top of any
   exception stack.
3. If the process was killed by the timeout (`ORANGE_SMOKE_TIMEOUT`), increase
   it: `ORANGE_SMOKE_TIMEOUT=180 bash dev-tools/run_client_smoke.sh`.
4. If the log contains `Minecraft has crashed` or
   `Fatal error in Fabric Loader`, the mixin or registration system has a hard
   error — fix those before re-running.

### Tier 2 — crash string in log

`run_client_smoke.sh` treats any of the following as an automatic failure:

- `Minecraft has crashed`
- `Fatal error in Fabric Loader`
- `Reported exception thrown`
- `Uncaught exception in thread "main"`

Find the first occurrence in the log and read the stack trace below it.

### Tier 3 — RCON / scenario failure

- Confirm the server fully started (`Done (` line in
  `fabric/run/logs/latest.log`).
- Verify RCON is enabled: `smoke-test.sh` patches `server.properties`
  automatically, but if `--skip-build` is used without a prior full run the
  file may not exist yet.
- Use `python3 dev-tools/rcon.py --password devtest list` to manually test
  connectivity.
- For a failing scenario step, read the `[FAIL]` output line printed by
  `run_scenario.py` — the `detail:` sub-line explains which `must_match` /
  `must_not_match` / `acceptable` pattern was violated.

### Drug debug lines show 0

`[OrangeSunshine] drug client tick` and `[OrangeSunshine] S2C drug` require:

1. A player connected in-world (automated client runs reach the main menu, not
   an active world).
2. "Log drug visuals (debug)" toggled on in Mod Menu → Orange Sunshine.

Zero hits is **normal and expected** for automated CI runs.

## CI Integration

The recommended CI setup runs tier 1 unconditionally and tier 2 with
`xvfb-run`. Tier 3 requires a running Minecraft server and is better suited
to a nightly or manual workflow.

```yaml
name: Visual Smoke
on: [push, pull_request]
jobs:
  visual-smoke:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Setup Python
        uses: actions/setup-python@v5
        with:
          python-version: '3.11'
      - name: Install xvfb (for Tier 2)
        run: sudo apt-get install -y xvfb
      - name: Tier 1 — Compile
        run: python3 dev-tools/visual_test/run_visual_tests.py --tier compile
      - name: Tier 2 — Client bootstrap
        run: python3 dev-tools/visual_test/run_visual_tests.py --tier smoke
        env:
          DISPLAY: ':99'
          ORANGE_SMOKE_TIMEOUT: '120'
```

Gradle equivalents for the two CI steps:

```bash
./gradlew :fabric:visualSmokeCompile   # tier 1
./gradlew :fabric:visualSmokeTest      # tier 2 (needs xvfb)
```

Both tasks are registered in `fabric/build.gradle` under the `verification` group.

## Stretch Goal: Drug Client Tick Assertion

Asserting that `[OrangeSunshine] drug client tick` and `[OrangeSunshine] S2C drug`
appear in a log requires:

1. A player connected in an active world.
2. Visual debug logging enabled via Mod Menu → Orange Sunshine → "Log drug
   visuals (debug)".
3. The player consuming a drug item so the effect is active.

**Manual procedure:**

```bash
# 1. Start the client with visual debug logging on (Mod Menu setting).
# 2. Load or create a singleplayer world.
# 3. Give yourself a drug: /give @s orangesunshine:joint
# 4. Consume it.
# 5. Wait ~5 seconds, then copy latest.log and run:
python3 dev-tools/visual_test/visual_smoke.py ~/.minecraft/logs/latest.log
```

**Path toward full automation:** Minecraft 1.21 supports
`--quickPlaySingleplayer <WorldName>` as a launch argument, which loads a
named singleplayer world directly. Combined with `xvfb-run`, a pre-created
test world, and a command block or data pack that gives and applies a drug on
world load, a headless end-to-end assertion becomes feasible without user
interaction. This is not yet implemented; the groundwork (`run_client_smoke.sh`
and `visual_smoke.py`) is in place.

## See Also

- `dev-tools/README.md` — general QA infrastructure overview (smoke-test
  pipeline, RCON client, scenario format, `run-qa.sh` / `run-qa-visual.sh`)
- `dev-tools/smoke-test.sh` — full server smoke pipeline with RCON checks and
  scenario runner
- `dev-tools/qa/scenarios/runtime/` — RCON scenario JSON files consumed by
  tier 3
- `dev-tools/visual_test/visual_smoke.py` — standalone drug-debug log scanner
  (wraps the compile step and grep logic used by the orchestrator)
- `dev-tools/visual_test/run_visual_tests.py` — orchestrator for all four tiers
