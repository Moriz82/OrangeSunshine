package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class FurnaceBlocks {

    public static CompoundExtractor compoundExtractorIdle = new CompoundExtractor("compoundextractoridle", false);
    public static CompoundExtractor compoundExtractorActive = new CompoundExtractor("compoundextractoractive", true);

    public static void register(IForgeRegistry<Block> registry) {
        registry.registerAll(
                compoundExtractorIdle,
                compoundExtractorActive.setLightLevel(0.875F)
        );
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.registerAll(
                compoundExtractorIdle.createItemBlock(),
                compoundExtractorActive.createItemBlock()
        );
    }

    public static void registerModels() {
        compoundExtractorIdle.registerItemModel(Item.getItemFromBlock(compoundExtractorIdle));
        compoundExtractorActive.registerItemModel(Item.getItemFromBlock(compoundExtractorActive));
    }
}