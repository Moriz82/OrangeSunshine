# OrangeSunshine 1.16.5→1.21.11 Port

## Paths
- Source (1.16.5): /home/moriz/Projects/OrangeSunshine-worktrees/1.16.5/src/main/java/com/BrotherHoodOfDiethylamide/OrangeSunshine/
- Target (1.21.11): /home/moriz/Projects/OrangeSunshine-worktrees/1.21.11/src/main/java/moriz/orangesunshine/
- Loaders: common/ fabric/ forge/ neoforge/ (Architectury multiloader)
- Build: ./gradlew build | smoke test: bash dev-tools/smoke-test.sh

## Namespace
OLD: com.BrotherHoodOfDiethylamide.OrangeSunshine
NEW: moriz.orangesunshine

## Key API Mappings (1.16 Forge → 1.21.11)
- Registration: @ObjectHolder → DeferredRegister / PSBlocks.bootstrap(), PSItems.bootstrap()
- Block props: Properties.of() + .setId(blockKey("name")) REQUIRED (NPE without it)
- Item props: new Item.Properties().setId(itemKey("name")) REQUIRED
- IForgeRegistry → BuiltInRegistries (must register during NeoForge RegisterEvent)
- IItemHandler capability → no capability system on common; use Fabric API on fabric side
- Forge events → NeoForge events in neoforge/ package; Fabric callbacks in fabric/ package
- Container/ContainerType → AbstractContainerMenu / MenuType
- ScreenHandlerFactory with extra data → vanilla MenuProvider (no extra data on NeoForge)
- TileEntity → BlockEntity; extend BaseEntityBlock + implement tick()
- World → Level; ServerWorld → ServerLevel
- PlayerEntity → Player; ServerPlayerEntity → ServerPlayer
- Hand → InteractionHand; ActionResult → InteractionResult
- SimpleChannel network → Architectury network API
- BiomeModifier → NeoForge BiomeModifier or Fabric BiomeModifications

## Rules for This Port
- Use Explore agents for reading/analyzing 1.16.5 files — never load large file sets into main context
- Use Haiku for: namespace renames, mechanical 1-to-1 API swaps, boilerplate
- Use Sonnet for: complex API analysis, fluid system, screen handlers, entity system
- Use Opus only for: architecture decisions, multi-system debugging
- After every subsystem: run bash dev-tools/smoke-test.sh --skip-build to verify
- Loader-specific code: Fabric-only → fabric/src/; NeoForge-only → neoforge/src/; Forge-only → forge/src/; shared → src/main/java/moriz/orangesunshine/
- Do NOT read 1.16.5 files into main context; always spawn Explore agents
- Do NOT summarize what you just did — just do it
