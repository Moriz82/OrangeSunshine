package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {

    public static DeferredRegister<TileEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, OrangeSunshine.MOD_ID);

    public static RegistryObject<TileEntityType<DryingTableTile>> DRYING_TABLE_TILE =
            TILE_ENTITIES.register("drying_table_tile", () -> TileEntityType.Builder.of(
                    DryingTableTile::new, ModBlocks.DRYING_TABLE.get()).build(null));

    public static RegistryObject<TileEntityType<FridgeTile>> FRIDGE_TILE =
            TILE_ENTITIES.register("fridge_tile", () -> TileEntityType.Builder.of(
                    FridgeTile::new, ModBlocks.FRIDGE.get()).build(null));

    public static RegistryObject<TileEntityType<CompoundCompressorTile>> COMPOUND_COMPRESSOR_TILE =
            TILE_ENTITIES.register("compound_compressor_tile", () -> TileEntityType.Builder.of(
                    CompoundCompressorTile::new, ModBlocks.COMPOUND_COMPRESSOR.get()).build(null));

    public static RegistryObject<TileEntityType<CompoundExtractorTile>> COMPOUND_EXTRACTOR_TILE =
            TILE_ENTITIES.register("compound_extractor_tile", () -> TileEntityType.Builder.of(
                    CompoundExtractorTile::new, ModBlocks.COMPOUND_EXTRACTOR.get()).build(null));

    public static void register(IEventBus eventBus) {
        TILE_ENTITIES.register(eventBus);
    }
}