package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {
    public static final List<Block> ALL_BLOCKS = new ArrayList<>();

    public static final Block PSYCH_ORE = register(new PsychOre());
    public static final Block COCA_BLOCK = register(new CocaBlock());
    public static final Block WEED_BLOCK = register(new WeedBlock());
    public static final Block TOBACCO_BLOCK = register(new TobaccoBlock());
    public static final Block PEYOTE_BLOCK = register(new PeyoteBlock());
    public static final Block SAN_PEDRO_BLOCK = register(new SanPedroBlock());
    public static final Block AYAHUASCA_BLOCK = register(new AyahuascaBlock());
    public static final Block RED_MUSHROOM_BLOCK = register(new RedMushroomBlock());
    public static final Block BROWN_MUSHROOM_BLOCK = register(new BrownMushroomBlock());
    public static final Block CUT_POPPY_BLOCK = register(new CutPoppyBlock());
    public static final Block COKE_CAKE_BLOCK = register(new CokeCakeBlock());
    public static final Block DRYING_TABLE = register(new DryingTableBlock());
    public static final Block FRIDGE = register(new FridgeBlock());
    public static final Block COMPOUND_COMPRESSOR = register(new CompoundCompressorBlock());
    public static final Block COMPOUND_EXTRACTOR = register(new CompoundExtractorBlock());

    private static Block register(Block block) {
        ALL_BLOCKS.add(block);
        return block;
    }

    private static Block simpleBlock(String name, Material material) {
        Block block = new Block(material)
                .setRegistryName(OrangeSunshine.MODID, name)
                .setUnlocalizedName(OrangeSunshine.MODID + "." + name)
                .setCreativeTab(OrangeSunshine.CreativeTab);
        return register(block);
    }
}
