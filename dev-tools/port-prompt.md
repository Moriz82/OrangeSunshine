Port OrangeSunshine from 1.16.5 to 1.21.11. Read CLAUDE.md first (it has all context).

Source: /home/moriz/Projects/OrangeSunshine-worktrees/1.16.5/src/main/java/com/BrotherHoodOfDiethylamide/OrangeSunshine/
Target: /home/moriz/Projects/OrangeSunshine-worktrees/1.21.11/src/main/java/moriz/orangesunshine/

Current state: Architectury scaffold exists (fabric/forge/neoforge subprojects). Some files ported from 1.20.x. Task: replace/augment with 1.16.5 content where it's more complete.

Port by subsystem in this order (each = 1 agent task):
1. [Haiku] blocks/ — port all Block classes. Compare 1.16.5 blocks/ → target block/. Rename namespace, fix props.
2. [Haiku] items/ — port all Item classes. Same pattern.
3. [Sonnet] blocks/tileentity → block/entity — port BlockEntity classes.
4. [Sonnet] blocks/screen + blocks/container → screen/ — port screen handlers (use vanilla MenuProvider not ExtendedScreenHandlerFactory).
5. [Sonnet] drugs/ → chemistry/ — port chemistry/drug item system.
6. [Sonnet] fluid system — port fluid registration, placed fluid blocks, fluid containers.
7. [Sonnet] entity/ — port entity classes and registration.
8. [Sonnet] network/ — port network packets via Architectury network API.
9. [Haiku] sounds/ + worldgen/ — port sound events and world gen features.
10. [Sonnet] events/ → loader-specific — split Forge event handlers into fabric/ and neoforge/ packages.
11. [Sonnet] mixin/client → mixin/client — update mixin targets to Mojang 1.21.11 class names.
12. [Sonnet] client/ rendering — port renderers to 1.21.11 Mojang render API.

After each step: bash dev-tools/smoke-test.sh --skip-build

Spawn agents for each step. Keep main context clean — use Explore agents to read 1.16.5 source, never dump files into main context.
