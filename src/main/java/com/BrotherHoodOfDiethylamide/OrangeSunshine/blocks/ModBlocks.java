package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.potion.Effects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final RegistryObject<TallCropsBlock> COCA_BLOCK = register("coca", () -> new TallCropsBlock(AbstractBlock.Properties.of(Material.PLANT).instabreak().noCollission().randomTicks().sound(SoundType.CROP)));
    public static final RegistryObject<TallCropsBlock> WEED_BLOCK = register("weed", () -> new WeedBlock(AbstractBlock.Properties.of(Material.PLANT).instabreak().noCollission().randomTicks().sound(SoundType.CROP)));
    public static final RegistryObject<TallCropsBlock> TOBACCO_BLOCK = register("tobacco_plant", () -> new TobaccoBlock(AbstractBlock.Properties.of(Material.PLANT).instabreak().noCollission().randomTicks().sound(SoundType.CROP)));
    public static final RegistryObject<CactusBlock> PEYOTE_BLOCK = register("peyote_plant", () -> new PeyoteBlock(AbstractBlock.Properties.of(Material.CACTUS).randomTicks().strength(0.4F).sound(SoundType.WOOL)));
    public static final RegistryObject<CactusBlock> SAN_PEDRO_BLOCK = register("san_pedro_plant", () -> new SanPedroBlock(AbstractBlock.Properties.of(Material.CACTUS).randomTicks().strength(0.4F).sound(SoundType.WOOL)));
    public static final RegistryObject<CokeCakeBlock> COKE_CAKE_BLOCK = register("coke_cake", () -> new CokeCakeBlock(AbstractBlock.Properties.of(Material.CAKE).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<CutPoppyBlock> CUT_POPPY_BLOCK = register("cut_poppy", () -> new CutPoppyBlock(AbstractBlock.Properties.of(Material.PLANT).instabreak().noCollission().randomTicks().sound(SoundType.GRASS)));
    public static final RegistryObject<Block> PSYCH_ORE = register("psych_ore", () -> new OreBlock(AbstractBlock.Properties.of(Material.STONE).strength(5f,5f).harvestTool(ToolType.PICKAXE).harvestLevel(3).sound(SoundType.STONE).requiresCorrectToolForDrops().randomTicks()));
    public static final RegistryObject<Block> DRYING_TABLE = register("drying_table", () -> new DryingTableBlock(AbstractBlock.Properties.of(Material.STONE)));
    public static final RegistryObject<Block> FRIDGE = register("fridge", () -> new FridgeBlock(AbstractBlock.Properties.of(Material.STONE)));
    public static final RegistryObject<Block> COMPOUND_COMPRESSOR = register("compound_compressor", () -> new CompoundCompressorBlock(AbstractBlock.Properties.of(Material.STONE)));
    public static final RegistryObject<Block> COMPOUND_EXTRACTOR = register("compound_extractor", () -> new CompoundExtractorBlock(AbstractBlock.Properties.of(Material.STONE)));
    public static final RegistryObject<Block> BROWN_MUSHROOM_BLOCK = register("brown_shrooms", () -> new MushroomBlock( AbstractBlock.Properties.copy(Blocks.BROWN_MUSHROOM).instabreak().noCollission().randomTicks().sound(SoundType.GRASS)));
    public static final RegistryObject<Block> RED_MUSHROOM_BLOCK = register("red_shrooms", () -> new MushroomBlock( AbstractBlock.Properties.copy(Blocks.RED_MUSHROOM).instabreak().noCollission().randomTicks().sound(SoundType.GRASS)));
    public static final RegistryObject<TallCropsBlock> AYAHUASCA_BLOCK = register("ayahuasca", () -> new AyahuascaBlock(AbstractBlock.Properties.of(Material.PLANT).instabreak().noCollission().randomTicks().sound(SoundType.CROP)));

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> supplier) {
        return OrangeSunshine.BLOCKS.register(name, supplier);
    }

    @OnlyIn(Dist.CLIENT)
    public static void initRenderTypes() {
        RenderTypeLookup.setRenderLayer(COCA_BLOCK.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(WEED_BLOCK.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(TOBACCO_BLOCK.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(PEYOTE_BLOCK.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(SAN_PEDRO_BLOCK.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(CUT_POPPY_BLOCK.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BROWN_MUSHROOM_BLOCK.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(RED_MUSHROOM_BLOCK.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(AYAHUASCA_BLOCK.get(), RenderType.cutout());
    }

    public static void init() {
    }
}
