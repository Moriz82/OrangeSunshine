package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems;
import net.minecraft.item.ItemStack;

public class RecipeInit {
    public static void registerAll() {
        registerDryingTable();
        registerFridge();
        registerCompoundCompressor();
        registerCompoundExtractor();
    }

    private static void registerDryingTable() {
        String dt = MachineRecipeManager.DRYING_TABLE;
        // Weed: leaf and bud drying
        MachineRecipeManager.register(dt, new ItemStack(ModItems.WEED_LEAF), new ItemStack(ModItems.DRIED_WEED_LEAF), 1000);
        MachineRecipeManager.register(dt, new ItemStack(ModItems.WEED_BUD), new ItemStack(ModItems.DRIED_WEED_BUD), 1000);
        // Tobacco drying
        MachineRecipeManager.register(dt, new ItemStack(ModItems.TOBACCO), new ItemStack(ModItems.DRIED_TOBACCO), 1000);
        // Mushroom drying
        MachineRecipeManager.register(dt, new ItemStack(ModItems.RED_SHROOMS), new ItemStack(ModItems.DRIED_RED_MUSHROOM), 1000);
        MachineRecipeManager.register(dt, new ItemStack(ModItems.BROWN_SHROOMS), new ItemStack(ModItems.DRIED_BROWN_MUSHROOM), 1000);
    }

    private static void registerFridge() {
        String fr = MachineRecipeManager.FRIDGE;
        // Coca Mulch -> Cocaine Rock (cold extraction)
        MachineRecipeManager.register(fr, new ItemStack(ModItems.COCA_MULCH), new ItemStack(ModItems.COCAINE_ROCK), 800);
        // Opium stages - each stage produces next bottle (fill with glass bottle in world)
        MachineRecipeManager.register(fr, new ItemStack(ModItems.OPIUM_BOTTLE_0), new ItemStack(ModItems.OPIUM_BOTTLE_1), 600);
        MachineRecipeManager.register(fr, new ItemStack(ModItems.OPIUM_BOTTLE_1), new ItemStack(ModItems.OPIUM_BOTTLE_2), 600);
        MachineRecipeManager.register(fr, new ItemStack(ModItems.OPIUM_BOTTLE_2), new ItemStack(ModItems.OPIUM_BOTTLE_3), 600);
        MachineRecipeManager.register(fr, new ItemStack(ModItems.OPIUM_BOTTLE_3), new ItemStack(ModItems.MORPHINE_BOTTLE), 800);
        // Bark solution processing
        MachineRecipeManager.register(fr, new ItemStack(ModItems.BARK_SOLUTION_1), new ItemStack(ModItems.BARK_SOLUTION_2), 600);
        MachineRecipeManager.register(fr, new ItemStack(ModItems.BARK_SOLUTION_2), new ItemStack(ModItems.BARK_SOLUTION_3), 600);
        MachineRecipeManager.register(fr, new ItemStack(ModItems.BARK_SOLUTION_3), new ItemStack(ModItems.BARK_SOLUTION_4), 600);
        MachineRecipeManager.register(fr, new ItemStack(ModItems.BARK_SOLUTION_4), new ItemStack(ModItems.BARK_SOLUTION_5), 600);
    }

    private static void registerCompoundCompressor() {
        String cc = MachineRecipeManager.COMPOUND_COMPRESSOR;
        // Cocaine powder from rock
        MachineRecipeManager.register(cc, new ItemStack(ModItems.COCAINE_ROCK), new ItemStack(ModItems.COCAINE_POWDER), 200);
        // Cocaine dust from powder (fine grinding)
        MachineRecipeManager.register(cc, new ItemStack(ModItems.COCAINE_POWDER, 2), new ItemStack(ModItems.COCAINE_DUST, 3), 200);
        // Weed extract from dried bud
        MachineRecipeManager.register(cc, new ItemStack(ModItems.DRIED_WEED_BUD, 3), new ItemStack(ModItems.WEED_EXTRACT), 400);
        // Ergotamine from ergorot wheat
        MachineRecipeManager.register(cc, new ItemStack(ModItems.ERGOROT_INFECTED_WHEAT, 4), new ItemStack(ModItems.ERGOTAMINE), 600);
        // Lysergic Acid from ergotamine
        MachineRecipeManager.register(cc, new ItemStack(ModItems.ERGOTAMINE, 2), new ItemStack(ModItems.LYSERGIC_ACID), 800);
        // Mescaline from peyote
        MachineRecipeManager.register(cc, new ItemStack(ModItems.PEYOTE, 3), new ItemStack(ModItems.MESCALINE), 600);
        // NIC from tobacco
        MachineRecipeManager.register(cc, new ItemStack(ModItems.DRIED_TOBACCO, 4), new ItemStack(ModItems.NIC), 400);
    }

    private static void registerCompoundExtractor() {
        String ce = MachineRecipeManager.COMPOUND_EXTRACTOR;
        // DMT from bark solution 5
        MachineRecipeManager.register(ce, new ItemStack(ModItems.BARK_SOLUTION_5), new ItemStack(ModItems.DMT_ITEM), 600);
        // MDMA synthesis
        MachineRecipeManager.register(ce, new ItemStack(ModItems.AMMONIA), new ItemStack(ModItems.MDMA), 800);
        // Morphine syringe fill
        MachineRecipeManager.register(ce, new ItemStack(ModItems.MORPHINE_BOTTLE), new ItemStack(ModItems.MORPHINE_SYRINGE), 200);
        // Coca leaf processing -> coca mulch
        MachineRecipeManager.register(ce, new ItemStack(ModItems.COCA_LEAF, 4), new ItemStack(ModItems.COCA_MULCH), 400);
        // Ayahuasca from bark solution + combined
        MachineRecipeManager.register(ce, new ItemStack(ModItems.DMT_ITEM), new ItemStack(ModItems.AYAHUASCA), 400);
    }
}
