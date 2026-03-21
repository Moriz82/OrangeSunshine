# dev-tools

Automated development and QA tooling for Orange Sunshine.

## QA workflows

Use `dev-tools/run-qa.sh` for a practical runtime + visual validation pass.

```bash
# Runtime checks for all loaders
bash dev-tools/run-qa.sh

# Runtime checks only for fabric
bash dev-tools/run-qa.sh --loader fabric

# Runtime + singleplayer visual pass on fabric, with client auto-launch
bash dev-tools/run-qa.sh --loader fabric --visual singleplayer --launch-client

# Runtime + both visual checklists on fabric
bash dev-tools/run-qa.sh --loader fabric --visual both --launch-client

# Runtime checks only, skipping compile if you already built
bash dev-tools/run-qa.sh --loader neoforge --skip-build
```

Artifacts are written under:
- `dev-tools/qa/artifacts/<timestamp>/<loader>/runtime/...`
- `dev-tools/qa/artifacts/<timestamp>/<loader>/visual/<scenario>/...`

To skip runtime checks and only run visual verification:

```bash
bash dev-tools/qa/run-qa-visual.sh --loader fabric --scenario dev-tools/qa/scenarios/visual/singleplayer.json
```

## smoke-test.sh

Main runtime smoke-test pipeline. Builds the mod, starts a loader dev server/client, runs RCON checks, optional JSON scenario suites, and analyses logs.

```bash
# Full run (build + server + RCON + scenario + log check)
bash dev-tools/smoke-test.sh --loader fabric

# Optional scenario dir and report output
bash dev-tools/smoke-test.sh --loader forge --scenario-dir dev-tools/qa/scenarios/runtime --artifact-prefix /tmp/qa-report

# Client mode — launch dev client on DISPLAY=:0 and monitor its log
bash dev-tools/smoke-test.sh --loader fabric --client

# Skip compile step
bash dev-tools/smoke-test.sh --loader neoforge --skip-build
```

## rcon.py

Minimal RCON client (Python stdlib, no dependencies).

```bash
python3 dev-tools/rcon.py list
python3 dev-tools/rcon.py --host 127.0.0.1 --port 25575 --password devtest give @a orangesunshine:flask 1
```

## check-logs.py

Scans a Minecraft log file for hard errors.

```bash
python3 dev-tools/check-logs.py fabric/run/logs/latest.log
```

## run-qa-visual.sh

Interactive visual pass runner for checklist-style screenshots.

```bash
# Visual-only singleplayer checklist
bash dev-tools/qa/run-qa-visual.sh --loader fabric --scenario dev-tools/qa/scenarios/visual/singleplayer.json

# Visual-only local server checklist
# Start or keep a local server running first (forge/neoforge/fabric), then run:
bash dev-tools/qa/run-qa-visual.sh --loader fabric --scenario dev-tools/qa/scenarios/visual/local_server.json --launch-client

# Choose screenshot backend
bash dev-tools/qa/run-qa-visual.sh --snapshot-tool grim --snapshot-delay 1
```

## run_scenario.py

Python scenario executor for smoke-test runtime checks.

- Loads JSON steps from `dev-tools/qa/scenarios/runtime/*.json`
- Runs each command over RCON and validates output via regex checks
- Returns non-zero on first failing step
- Can write JSON report via `--report`

## QA scenarios

- `dev-tools/qa/scenarios/runtime/core.json`
- `dev-tools/qa/scenarios/visual/singleplayer.json`
- `dev-tools/qa/scenarios/visual/local_server.json`
