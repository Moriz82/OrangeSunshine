package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final RegistryObject<TallCropsBlock> COCA_BLOCK = register("coca", () -> new TallCropsBlock(AbstractBlock.Properties.of(Material.PLANT).instabreak().noCollission().randomTicks().sound(SoundType.CROP)));
    public static final RegistryObject<TallCropsBlock> WEED_BLOCK = register("weed", () -> new WeedBlock(AbstractBlock.Properties.of(Material.PLANT).instabreak().noCollission().randomTicks().sound(SoundType.CROP)));
    public static final RegistryObject<TallCropsBlock> PEYOTE_BLOCK = register("peyote", () -> new PeyoteBlock(AbstractBlock.Properties.of(Material.PLANT).instabreak().noCollission().randomTicks().sound(SoundType.CROP)));
    public static final RegistryObject<CokeCakeBlock> COKE_CAKE_BLOCK = register("coke_cake", () -> new CokeCakeBlock(AbstractBlock.Properties.of(Material.CAKE).strength(0.5F).sound(SoundType.WOOL)));
    public static final RegistryObject<CutPoppyBlock> CUT_POPPY_BLOCK = register("cut_poppy", () -> new CutPoppyBlock(AbstractBlock.Properties.of(Material.PLANT).instabreak().noCollission().randomTicks().sound(SoundType.GRASS)));
    public static final RegistryObject<OreBlock> PSYCH_ORE = register("psych_ore", () -> new OreBlock(AbstractBlock.Properties.of(Material.STONE).strength(5f,5f).harvestTool(ToolType.PICKAXE).harvestLevel(3).sound(SoundType.STONE).requiresCorrectToolForDrops().randomTicks()));
    public static final RegistryObject<Block> DRYING_TABLE = register("drying_table", () -> new DryingTableBlock(AbstractBlock.Properties.of(Material.STONE)));
    public static final RegistryObject<Block> FRIDGE = register("fridge", () -> new FridgeBlock(AbstractBlock.Properties.of(Material.STONE)));

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> supplier) {
        return OrangeSunshine.BLOCKS.register(name, supplier);
    }

    @OnlyIn(Dist.CLIENT)
    public static void initRenderTypes() {
        RenderTypeLookup.setRenderLayer(COCA_BLOCK.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(WEED_BLOCK.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(CUT_POPPY_BLOCK.get(), RenderType.cutout());
    }

    public static void init() {
    }
}
