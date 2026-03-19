package moriz.orangesunshine.block.entity;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.MashTubWallBlock;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.block.PlacedDrinksBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface PSBlockEntities {
    BlockEntityType<DryingTableBlockEntity> DRYING_TABLE = create("drying_table", FabricBlockEntityTypeBuilder.create(DryingTableBlockEntity::new, PSBlocks.DRYING_TABLE, PSBlocks.IRON_DRYING_TABLE).build());
    BlockEntityType<MashTubBlockEntity> MASH_TUB = create("wooden_vat", FabricBlockEntityTypeBuilder.create(MashTubBlockEntity::new, PSBlocks.MASH_TUB).build());
    BlockEntityType<MashTubWallBlock.MasterPosition> MASH_TUB_EDGE = create("wooden_vat_edge", FabricBlockEntityTypeBuilder.create(MashTubWallBlock.MasterPosition::new, PSBlocks.MASH_TUB_EDGE).build());
    BlockEntityType<RiftJarBlockEntity> RIFT_JAR = create("rift_jar", FabricBlockEntityTypeBuilder.create(RiftJarBlockEntity::new, PSBlocks.RIFT_JAR).build());
    BlockEntityType<PeyoteBlockEntity> PEYOTE = create("peyote", FabricBlockEntityTypeBuilder.create(PeyoteBlockEntity::new, PSBlocks.PEYOTE).build());
    BlockEntityType<DistilleryBlockEntity> DISTILLERY = create("distillery", FabricBlockEntityTypeBuilder.create(DistilleryBlockEntity::new, PSBlocks.DISTILLERY).build());
    BlockEntityType<BottleRackBlockEntity> BOTTLE_RACK = create("bottle_rack", FabricBlockEntityTypeBuilder.create(BottleRackBlockEntity::new, PSBlocks.BOTTLE_RACK).build());
    BlockEntityType<FlaskBlockEntity> FLASK = create("flask", FabricBlockEntityTypeBuilder.create(FlaskBlockEntity::new, PSBlocks.FLASK).build());
    BlockEntityType<BarrelBlockEntity> BARREL = create("barrel", FabricBlockEntityTypeBuilder.create(BarrelBlockEntity::new,
            PSBlocks.OAK_BARREL, PSBlocks.SPRUCE_BARREL,
            PSBlocks.BIRCH_BARREL, PSBlocks.JUNGLE_BARREL,
            PSBlocks.ACACIA_BARREL, PSBlocks.DARK_OAK_BARREL
    ).build());
    BlockEntityType<PlacedDrinksBlock.Data> PLACED_DRINK = create("placed_drink", FabricBlockEntityTypeBuilder.create(PlacedDrinksBlock.Data::new, PSBlocks.PLACED_DRINK).build());

    BlockEntityType<MortarPestleBlockEntity> MORTAR_PESTLE_BLOCK_ENTITY = create("mortar_pestle",
            FabricBlockEntityTypeBuilder.create(MortarPestleBlockEntity::new, PSBlocks.MORTAR_PESTLE).build());

    BlockEntityType<MixingTableBlockEntity> MIXING_TABLE_BLOCK_ENTITY = create("mixing_table",
            FabricBlockEntityTypeBuilder.create(MixingTableBlockEntity::new, PSBlocks.MIXING_TABLE).build());

    static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityType<T> type) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, OrangeSunshine.id(id), type);
    }

    static void bootstrap() { }
}
