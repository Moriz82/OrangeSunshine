package com.BrotherHoodOfDiethylamide.OrangeSunshine.testing;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.MachineRecipeManager;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.PlayerDrugs;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.*;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.BongItem;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.RigItem;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

/**
 * Comprehensive automated test runner.
 * Enabled when -Dorangesunshine.runTests=true.
 *
 * Tests:
 *  1. Registry + system init checks
 *  2. Drug renderTick visual effects (all 14 drugs)
 *  3. Drug effectTick gameplay effects (all 14 drugs)
 *  4. All 14 drugs active simultaneously (ADSR integration)
 *  5. Crafting recipes loaded (JSON recipes)
 *  6. Machine recipes loaded (drying table / fridge / compressor / extractor)
 *  7. Bong and Rig substance maps populated
 *  8. DrugProperties / portedpsych bridge
 */
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = OrangeSunshine.MODID)
public class AutoTest {

    private static final boolean ENABLED = Boolean.getBoolean("orangesunshine.runTests");

    private enum State {
        INIT, WAIT_WORLD,
        GIVE_ALL_DRUGS, WAIT_ALL_EFFECTS,
        RUN_ALL_CHECKS,
        GIVE_DRUG_VIA_SERVER, WAIT_SERVER_SYNC, CHECK_SERVER_SYNC_FINISH,
        DONE
    }

    private static State state = State.INIT;
    private static int waitTicks = 0;
    private static int passed = 0;
    private static int failed = 0;

    // -------------------------------------------------------------------------
    // Tick handler
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (!ENABLED || event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getMinecraft();

        switch (state) {

            case INIT: {
                if (mc.world == null && mc.currentScreen != null) {
                    OrangeSunshine.logger.info("[TEST] Creating test world...");
                    WorldSettings settings = new WorldSettings(
                            0L, GameType.CREATIVE, false, false, WorldType.FLAT);
                    settings.enableCommands();
                    mc.launchIntegratedServer("__autotest__", "AutoTest", settings);
                    state = State.WAIT_WORLD;
                    waitTicks = 0;
                }
                break;
            }

            case WAIT_WORLD: {
                waitTicks++;
                if (mc.player != null && mc.world != null && !mc.isGamePaused()) {
                    OrangeSunshine.logger.info("[TEST] World loaded after " + waitTicks + " ticks");
                    state = State.GIVE_ALL_DRUGS;
                    waitTicks = 0;
                }
                if (waitTicks > 1200) crash("World load timed out");
                break;
            }

            case GIVE_ALL_DRUGS: {
                EntityPlayer player = mc.player;
                // Give one instance of every drug with delay=0, potency=1.0, short duration
                for (Map.Entry<String, Drug> entry : DrugRegistry.DRUGS.entrySet()) {
                    Drug.addDrug(player, new DrugInstance(entry.getValue(), 0, 1.0f, 400));
                }
                OrangeSunshine.logger.info("[TEST] Gave all 14 drugs to player");
                state = State.WAIT_ALL_EFFECTS;
                waitTicks = 0;
                break;
            }

            case WAIT_ALL_EFFECTS: {
                waitTicks++;
                if (waitTicks >= 20) { // 1 second — ADSR attack started
                    state = State.RUN_ALL_CHECKS;
                }
                break;
            }

            case RUN_ALL_CHECKS: {
                runAllChecks(mc.player, mc);
                state = State.GIVE_DRUG_VIA_SERVER;
                waitTicks = 0;
                break;
            }

            case GIVE_DRUG_VIA_SERVER: {
                // Test real server→client sync path: add a drug to the SERVER player,
                // which triggers immediate sync to the client (with our fix).
                // First clear the server player's drugs so the client starts clean.
                EntityPlayerMP serverPlayer = FMLCommonHandler.instance()
                    .getMinecraftServerInstance()
                    .getPlayerList().getPlayerByUUID(mc.player.getUniqueID());
                if (serverPlayer != null) {
                    Drug.clearDrugs(serverPlayer);
                    PlayerDrugs.sync(serverPlayer);  // sync empty list to client
                    Drug testDrug = DrugRegistry.DRUGS.get("lsd_bottle");
                    Drug.addDrug(serverPlayer, new DrugInstance(testDrug, 0, 1.0f, 400));
                    // Drug.addDrug now immediately syncs serverPlayer → client
                    OrangeSunshine.logger.info("[TEST] Added lsd_bottle via server player, awaiting sync...");
                } else {
                    OrangeSunshine.logger.warn("[TEST] Could not get server player for sync test");
                }
                state = State.WAIT_SERVER_SYNC;
                waitTicks = 0;
                break;
            }

            case WAIT_SERVER_SYNC: {
                waitTicks++;
                if (waitTicks >= 5) {
                    state = State.CHECK_SERVER_SYNC_FINISH;
                }
                break;
            }

            case CHECK_SERVER_SYNC_FINISH: {
                checkServerSyncAndFinish(mc.player);
                state = State.DONE;
                break;
            }

            case DONE:
                break;
        }
    }

    // -------------------------------------------------------------------------
    // Master check runner
    // -------------------------------------------------------------------------

    private static void runAllChecks(EntityPlayer player, Minecraft mc) {
        OrangeSunshine.logger.info("[TEST] ========== Running comprehensive checks ==========");

        checkRegistry();
        checkDrugRenderTick();
        checkDrugEffectTick(player);
        checkActiveAndBridge(player);
        checkCraftingRecipes(mc);
        checkMachineRecipes();
        checkBongAndRig();

        OrangeSunshine.logger.info("[TEST] ========================================");
        OrangeSunshine.logger.info("[TEST] (continuing to server-sync test...)");
    }

    private static void checkServerSyncAndFinish(EntityPlayer clientPlayer) {
        OrangeSunshine.logger.info("[TEST] --- Server→Client sync path ---");

        // After Drug.addDrug(serverPlayer,...) the sync packet should have arrived by now (5 ticks).
        java.util.List<DrugInstance> sources = Drug.getDrugSources(clientPlayer);
        check("Server sync: client received drug sources (size > 0)", !sources.isEmpty());
        Drug lsd = DrugRegistry.DRUGS.get("lsd_bottle");
        boolean found = false;
        for (DrugInstance d : sources) {
            if (d.getDrug() == lsd) { found = true; break; }
        }
        check("Server sync: lsd_bottle arrived on client", found);

        OrangeSunshine.logger.info("[TEST] ========================================");
        OrangeSunshine.logger.info("[TEST] Final results: " + passed + " passed, " + failed + " failed");

        if (failed == 0) {
            OrangeSunshine.logger.info("[TEST] ALL TESTS PASSED");
        } else {
            OrangeSunshine.logger.error("[TEST] TESTS FAILED: " + failed + " failures");
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try { deleteDir(new java.io.File("run/__autotest__")); }
            catch (Exception ignored) {}
        }));

        final int exitCode = failed == 0 ? 0 : 1;
        new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            FMLCommonHandler.instance().exitJava(exitCode, false);
        }).start();
    }

    // -------------------------------------------------------------------------
    // 1. Registry checks
    // -------------------------------------------------------------------------

    private static void checkRegistry() {
        OrangeSunshine.logger.info("[TEST] --- Registry ---");
        check("DrugRegistry has 14 drugs", DrugRegistry.DRUGS.size() == 14);
        String[] expectedDrugs = {
            "red_shrooms", "brown_shrooms", "cocaine", "weed", "morphine",
            "lsd_bottle", "lsd_blotter", "orangesunshine_bottle", "orangesunshine_blotter",
            "dmt", "dmt_5_meo", "peyote", "nic", "mdma"
        };
        for (String name : expectedDrugs) {
            check("DrugRegistry has '" + name + "'", DrugRegistry.DRUGS.containsKey(name));
        }
    }

    // -------------------------------------------------------------------------
    // 2. Drug renderTick — visual effects correct for each drug
    // -------------------------------------------------------------------------

    private static void checkDrugRenderTick() {
        OrangeSunshine.logger.info("[TEST] --- Drug visual effects (renderTick at peak) ---");

        checkDrugRender("weed",
            de -> de.SATURATION.getValue() > 0 && de.BLOOM_RADIUS.getValue() > 0 && de.CAMERA_INERTIA.getValue() > 0,
            "SATURATION + BLOOM_RADIUS + CAMERA_INERTIA");

        checkDrugRender("cocaine",
            de -> de.SATURATION.getValue() < 0 && de.CAMERA_TREMBLE.getValue() > 0 && de.BUMPY.getValue() > 0,
            "SATURATION<0 + CAMERA_TREMBLE + BUMPY");

        checkDrugRender("morphine",
            de -> de.SATURATION.getValue() > 0,
            "SATURATION (at effect=1.0, threshold 0.5 so f=0.5)");

        checkDrugRender("lsd_bottle",
            de -> de.BIG_WAVES.getValue() > 0 && de.SMALL_WAVES.getValue() > 0
               && de.WORLD_DEFORMATION.getValue() > 0 && de.KALEIDOSCOPE_INTENSITY.getValue() > 0
               && de.RECURSION.getValue() > 0,
            "BIG_WAVES + SMALL_WAVES + WORLD_DEFORMATION + KALEIDOSCOPE + RECURSION");

        checkDrugRender("lsd_blotter",
            de -> de.BIG_WAVES.getValue() > 0 && de.SMALL_WAVES.getValue() > 0,
            "BIG_WAVES + SMALL_WAVES");

        checkDrugRender("orangesunshine_bottle",
            de -> de.WORLD_DEFORMATION.getValue() > 0 && de.KALEIDOSCOPE_INTENSITY.getValue() > 0,
            "WORLD_DEFORMATION + KALEIDOSCOPE");

        checkDrugRender("orangesunshine_blotter",
            de -> de.WORLD_DEFORMATION.getValue() > 0,
            "WORLD_DEFORMATION");

        checkDrugRender("dmt",
            de -> de.BIG_WAVES.getValue() > 0 && de.RECURSION.getValue() > 0
               && de.KALEIDOSCOPE_INTENSITY.getValue() > 0,
            "BIG_WAVES + RECURSION + KALEIDOSCOPE");

        checkDrugRender("dmt_5_meo",
            de -> de.BIG_WAVES.getValue() > 0 && de.RECURSION.getValue() > 0,
            "BIG_WAVES + RECURSION");

        checkDrugRender("red_shrooms",
            de -> de.BIG_WAVES.getValue() > 0 && de.SATURATION.getValue() > 0 && de.KALEIDOSCOPE_INTENSITY.getValue() > 0,
            "BIG_WAVES + SATURATION + KALEIDOSCOPE");

        checkDrugRender("brown_shrooms",
            de -> de.BIG_WAVES.getValue() > 0 && de.SATURATION.getValue() > 0,
            "BIG_WAVES + SATURATION");

        checkDrugRender("peyote",
            de -> de.BIG_WAVES.getValue() > 0 && de.WORLD_DEFORMATION.getValue() > 0
               && de.KALEIDOSCOPE_INTENSITY.getValue() > 0,
            "BIG_WAVES + WORLD_DEFORMATION + KALEIDOSCOPE");

        checkDrugRender("nic",
            de -> de.SMALL_WAVES.getValue() > 0 && de.BRIGHTNESS.getValue() > 0,
            "SMALL_WAVES + BRIGHTNESS");

        checkDrugRender("mdma",
            de -> de.CAMERA_TREMBLE.getValue() > 0 && de.SATURATION.getValue() > 0
               && de.HUE_AMPLITUDE.getValue() > 0,
            "CAMERA_TREMBLE + SATURATION + HUE_AMPLITUDE");
    }

    private interface DrugEffectsCheck {
        boolean test(DrugEffects de);
    }

    private static void checkDrugRender(String drugName, DrugEffectsCheck check, String expectedChannels) {
        Drug drug = DrugRegistry.DRUGS.get(drugName);
        if (drug == null) {
            check("renderTick[" + drugName + "]: drug exists", false);
            return;
        }
        DrugEffects effects = new DrugEffects();
        drug.renderTick(effects, 1.0f);
        boolean ok = check.test(effects);
        if (!ok) {
            OrangeSunshine.logger.error("[TEST] FAIL: renderTick[" + drugName + "] - expected " + expectedChannels);
            failed++;
        } else {
            OrangeSunshine.logger.info("[TEST] PASS: renderTick[" + drugName + "] (" + expectedChannels + ")");
            passed++;
        }
    }

    // -------------------------------------------------------------------------
    // 3. Drug effectTick — gameplay effects correct
    // -------------------------------------------------------------------------

    private static void checkDrugEffectTick(EntityPlayer player) {
        OrangeSunshine.logger.info("[TEST] --- Drug gameplay effects (effectTick at peak) ---");

        // Weed: slows movement and digging, increases hunger
        {
            Drug drug = DrugRegistry.DRUGS.get("weed");
            DrugEffects effects = new DrugEffects();
            drug.effectTick(player, effects, 1.0f);
            check("effectTick[weed] MOVEMENT_SPEED < 0", effects.MOVEMENT_SPEED.getValue() < 0);
            check("effectTick[weed] DIG_SPEED < 0", effects.DIG_SPEED.getValue() < 0);
            check("effectTick[weed] HUNGER_RATE > 0", effects.HUNGER_RATE.getValue() > 0);
        }

        // Cocaine: boosts movement speed
        {
            Drug drug = DrugRegistry.DRUGS.get("cocaine");
            DrugEffects effects = new DrugEffects();
            drug.effectTick(player, effects, 1.0f);
            check("effectTick[cocaine] MOVEMENT_SPEED > 0", effects.MOVEMENT_SPEED.getValue() > 0);
        }

        // Morphine: sets DROWN_RATE at full effect
        {
            Drug drug = DrugRegistry.DRUGS.get("morphine");
            DrugEffects effects = new DrugEffects();
            drug.effectTick(player, effects, 1.0f);
            check("effectTick[morphine] DROWN_RATE > 0", effects.DROWN_RATE.getValue() > 0);
        }

        // Nic: no gameplay DrugEffects (all render-only)
        {
            Drug drug = DrugRegistry.DRUGS.get("nic");
            DrugEffects effects = new DrugEffects();
            drug.effectTick(player, effects, 1.0f);
            check("effectTick[nic] no MOVEMENT_SPEED change (nic is visual-only)",
                effects.MOVEMENT_SPEED.getValue() == 0f);
        }
    }

    // -------------------------------------------------------------------------
    // 4. Active drugs + portedpsych bridge
    // -------------------------------------------------------------------------

    private static void checkActiveAndBridge(EntityPlayer player) {
        OrangeSunshine.logger.info("[TEST] --- Active drugs + portedpsych bridge ---");

        Map<Drug, Float> actives = Drug.getActiveDrugs(player);
        check("All 14 drugs active after giving them", actives.size() == 14);

        // Every registered drug should be active
        for (Map.Entry<String, Drug> entry : DrugRegistry.DRUGS.entrySet()) {
            float level = actives.getOrDefault(entry.getValue(), -1f);
            check("Drug '" + entry.getKey() + "' active (level > 0)", level > 0f);
            if (level > 0) {
                OrangeSunshine.logger.info("[TEST]   " + entry.getKey() + " level: " + level);
            }
        }

        // portedpsych bridge
        DrugProperties dp = DrugProperties.getDrugProperties(player);
        check("DrugProperties initialized for client player", dp != null);
        if (dp != null) {
            check("DrugEffectsBridge registered as 'bridge'", DrugProperties.drugs.containsKey("bridge"));
            check("DrugRenderer created", dp.renderer != null);
        }
    }

    // -------------------------------------------------------------------------
    // 5. Crafting recipes — verify JSON recipes were loaded
    // -------------------------------------------------------------------------

    private static void checkCraftingRecipes(Minecraft mc) {
        OrangeSunshine.logger.info("[TEST] --- Crafting recipes ---");

        // Items that should have crafting recipes loaded from JSON
        Item[][] recipePairs = {
            {ModItems.WEED_JOINT},
            {ModItems.CIGARETTE},
            {ModItems.CIGAR},
            {ModItems.ROLLING_PAPER},
            {ModItems.BLOTTER},
            {ModItems.LSD_BLOTTER},
            {ModItems.ORANGESUNSHINE_BLOTTER},
            {ModItems.LYSERGIC_ACID},
            {ModItems.CAKE_BAR},
            {ModItems.EMPTY_SYRINGE},
            {ModItems.PSYCH_SWORD},
            {ModItems.PSYCH_PIC},
            {ModItems.PSYCH_AXE},
            {ModItems.PSYCH_SHOVEL},
            {ModItems.PSYCH_HOE},
            {ModItems.PSYCH_HELMET},
            {ModItems.PSYCH_CHEST},
            {ModItems.PSYCH_LEGGINGS},
            {ModItems.PSYCH_BOOTS},
        };

        String[] recipeNames = {
            "weed_joint", "cigarette", "cigar", "rolling_paper", "blotter",
            "lsd_blotter", "orangesunshine_blotter", "lysergic_acid", "cake_bar",
            "syringe (empty)",
            "psych_sword", "psych_pickaxe", "psych_axe", "psych_shovel", "psych_hoe",
            "psych_helmet", "psych_chestplate", "psych_leggings", "psych_boots"
        };

        // Collect all recipes into a list for size reporting
        java.util.List<IRecipe> allRecipes = new java.util.ArrayList<>();
        for (IRecipe r : CraftingManager.REGISTRY) allRecipes.add(r);
        OrangeSunshine.logger.info("[TEST]   Total crafting recipes loaded: " + allRecipes.size());

        for (int i = 0; i < recipePairs.length; i++) {
            Item target = recipePairs[i][0];
            String name = recipeNames[i];
            boolean found = false;
            for (IRecipe recipe : allRecipes) {
                ItemStack out = recipe.getRecipeOutput();
                if (!out.isEmpty() && out.getItem() == target) {
                    found = true;
                    break;
                }
            }
            check("Crafting recipe for '" + name + "'", found);
        }
    }

    // -------------------------------------------------------------------------
    // 6. Machine recipes
    // -------------------------------------------------------------------------

    private static void checkMachineRecipes() {
        OrangeSunshine.logger.info("[TEST] --- Machine recipes ---");

        // Drying table
        String dt = MachineRecipeManager.DRYING_TABLE;
        checkMachineRecipe(dt, "weed_leaf → dried_weed_leaf",
            ModItems.WEED_LEAF, ModItems.DRIED_WEED_LEAF);
        checkMachineRecipe(dt, "weed_bud → dried_weed_bud",
            ModItems.WEED_BUD, ModItems.DRIED_WEED_BUD);
        checkMachineRecipe(dt, "tobacco → dried_tobacco",
            ModItems.TOBACCO, ModItems.DRIED_TOBACCO);
        checkMachineRecipe(dt, "red_shrooms → dried_red_shrooms",
            ModItems.RED_SHROOMS, ModItems.DRIED_RED_MUSHROOM);
        checkMachineRecipe(dt, "brown_shrooms → dried_brown_shrooms",
            ModItems.BROWN_SHROOMS, ModItems.DRIED_BROWN_MUSHROOM);

        // Fridge
        String fr = MachineRecipeManager.FRIDGE;
        checkMachineRecipe(fr, "coca_mulch → cocaine_rock",
            ModItems.COCA_MULCH, ModItems.COCAINE_ROCK);
        checkMachineRecipe(fr, "opium_bottle_0 → opium_bottle_1",
            ModItems.OPIUM_BOTTLE_0, ModItems.OPIUM_BOTTLE_1);
        checkMachineRecipe(fr, "opium_bottle_3 → morphine_bottle",
            ModItems.OPIUM_BOTTLE_3, ModItems.MORPHINE_BOTTLE);
        checkMachineRecipe(fr, "bark_solution_1 → bark_solution_2",
            ModItems.BARK_SOLUTION_1, ModItems.BARK_SOLUTION_2);
        checkMachineRecipe(fr, "bark_solution_4 → bark_solution_5",
            ModItems.BARK_SOLUTION_4, ModItems.BARK_SOLUTION_5);

        // Compound compressor
        String cc = MachineRecipeManager.COMPOUND_COMPRESSOR;
        checkMachineRecipe(cc, "cocaine_rock → cocaine_powder",
            ModItems.COCAINE_ROCK, ModItems.COCAINE_POWDER);
        checkMachineRecipe(cc, "dried_weed_bud x3 → weed_extract",
            new ItemStack(ModItems.DRIED_WEED_BUD, 3), ModItems.WEED_EXTRACT);
        checkMachineRecipe(cc, "ergorot x4 → ergotamine",
            new ItemStack(ModItems.ERGOROT_INFECTED_WHEAT, 4), ModItems.ERGOTAMINE);
        checkMachineRecipe(cc, "dried_tobacco x4 → nic",
            new ItemStack(ModItems.DRIED_TOBACCO, 4), ModItems.NIC);
        checkMachineRecipe(cc, "peyote x3 → mescaline",
            new ItemStack(ModItems.PEYOTE, 3), ModItems.MESCALINE);

        // Compound extractor
        String ce = MachineRecipeManager.COMPOUND_EXTRACTOR;
        checkMachineRecipe(ce, "bark_solution_5 → dmt",
            ModItems.BARK_SOLUTION_5, ModItems.DMT_ITEM);
        checkMachineRecipe(ce, "coca_leaf x4 → coca_mulch",
            new ItemStack(ModItems.COCA_LEAF, 4), ModItems.COCA_MULCH);
        checkMachineRecipe(ce, "dmt → ayahuasca",
            ModItems.DMT_ITEM, ModItems.AYAHUASCA);
        checkMachineRecipe(ce, "morphine_bottle → morphine_syringe",
            ModItems.MORPHINE_BOTTLE, ModItems.MORPHINE_SYRINGE);
    }

    private static void checkMachineRecipe(String machine, String label, Item input, Item expectedOutput) {
        checkMachineRecipe(machine, label, new ItemStack(input), expectedOutput);
    }

    private static void checkMachineRecipe(String machine, String label, ItemStack input, Item expectedOutput) {
        com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.MachineRecipe recipe =
            MachineRecipeManager.findRecipe(machine, input);
        boolean ok = recipe != null && recipe.getOutput().getItem() == expectedOutput;
        check("Machine[" + machine + "] " + label, ok);
    }

    // -------------------------------------------------------------------------
    // 7. Bong and Rig substance maps
    // -------------------------------------------------------------------------

    private static void checkBongAndRig() {
        OrangeSunshine.logger.info("[TEST] --- Bong/Rig substance maps ---");

        check("BongItem.BONGABLES populated", !BongItem.BONGABLES.isEmpty());
        check("BongItem has dried_weed_bud", BongItem.BONGABLES.containsKey(ModItems.DRIED_WEED_BUD));
        check("BongItem has dried_weed_leaf", BongItem.BONGABLES.containsKey(ModItems.DRIED_WEED_LEAF));
        check("BongItem has weed_extract", BongItem.BONGABLES.containsKey(ModItems.WEED_EXTRACT));
        check("BongItem has dried_tobacco", BongItem.BONGABLES.containsKey(ModItems.DRIED_TOBACCO));
        check("BongItem has dmt", BongItem.BONGABLES.containsKey(ModItems.DMT_ITEM));
        check("BongItem has dmt_5_meo", BongItem.BONGABLES.containsKey(ModItems.DMT_5_MEO));

        check("RigItem.RIGABLES populated", !RigItem.RIGABLES.isEmpty());
        check("RigItem has weed_extract", RigItem.RIGABLES.containsKey(ModItems.WEED_EXTRACT));
        check("RigItem has dmt", RigItem.RIGABLES.containsKey(ModItems.DMT_ITEM));
        check("RigItem has dmt_5_meo", RigItem.RIGABLES.containsKey(ModItems.DMT_5_MEO));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static void check(String name, boolean condition) {
        if (condition) {
            OrangeSunshine.logger.info("[TEST] PASS: " + name);
            passed++;
        } else {
            OrangeSunshine.logger.error("[TEST] FAIL: " + name);
            failed++;
        }
    }

    private static void crash(String reason) {
        OrangeSunshine.logger.error("[TEST] CRASH: " + reason);
        FMLCommonHandler.instance().exitJava(1, false);
    }

    private static void deleteDir(java.io.File dir) {
        if (!dir.exists()) return;
        java.io.File[] files = dir.listFiles();
        if (files != null) for (java.io.File f : files) {
            if (f.isDirectory()) deleteDir(f);
            else f.delete();
        }
        dir.delete();
    }
}
