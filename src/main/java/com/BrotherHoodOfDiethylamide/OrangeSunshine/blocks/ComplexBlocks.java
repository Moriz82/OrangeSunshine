package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Block_init;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class ComplexBlocks {

    public static CompoundExtractor compoundExtractorIdle = new CompoundExtractor("compound_extractor_idle", false);
    public static CompoundExtractor compoundExtractorActive = new CompoundExtractor("compound_extractor_active", true);
    public static PsychOre psychOre = new PsychOre("psych_ore", Material.ROCK);

    public static void register(IForgeRegistry<Block> registry) {
        registry.registerAll(
                compoundExtractorIdle,
                compoundExtractorActive.setLightLevel(0.875F),
                psychOre
        );
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.registerAll(
                compoundExtractorIdle.createItemBlock(),
                compoundExtractorActive.createItemBlock(),
                psychOre.createItemBlock()
        );
    }

    public static void registerModels() {
        compoundExtractorIdle.registerItemModel(Item.getItemFromBlock(compoundExtractorIdle));
        compoundExtractorActive.registerItemModel(Item.getItemFromBlock(compoundExtractorActive));
        psychOre.registerItemModel(Item.getItemFromBlock(psychOre));
    }
}