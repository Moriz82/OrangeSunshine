package moriz.orangesunshine.block.entity;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.MashTubWallBlock;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.block.PlacedDrinksBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.function.BiFunction;

public final class PSBlockEntities {
    public static BlockEntityType<DryingTableBlockEntity> DRYING_TABLE;
    public static BlockEntityType<MashTubBlockEntity> MASH_TUB;
    public static BlockEntityType<MashTubWallBlock.MasterPosition> MASH_TUB_EDGE;
    public static BlockEntityType<RiftJarBlockEntity> RIFT_JAR;
    public static BlockEntityType<PeyoteBlockEntity> PEYOTE;
    public static BlockEntityType<DistilleryBlockEntity> DISTILLERY;
    public static BlockEntityType<BottleRackBlockEntity> BOTTLE_RACK;
    public static BlockEntityType<FlaskBlockEntity> FLASK;
    public static BlockEntityType<BarrelBlockEntity> BARREL;
    public static BlockEntityType<PlacedDrinksBlock.Data> PLACED_DRINK;
    public static BlockEntityType<MortarPestleBlockEntity> MORTAR_PESTLE_BLOCK_ENTITY;
    public static BlockEntityType<MixingTableBlockEntity> MIXING_TABLE_BLOCK_ENTITY;

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> BlockEntityType<T> create(String id,
            BiFunction<BlockPos, BlockState, T> factory, Block... validBlocks) {
        try {
            Class<?> supplierClass = Class.forName(
                    "net.minecraft.world.level.block.entity.BlockEntityType$BlockEntitySupplier");
            Object supplier = Proxy.newProxyInstance(
                    PSBlockEntities.class.getClassLoader(),
                    new Class[]{ supplierClass },
                    (proxy, method, args) -> factory.apply((BlockPos) args[0], (BlockState) args[1]));
            Constructor<?> ctor = null;
            for (Constructor<?> c : BlockEntityType.class.getDeclaredConstructors()) {
                if (c.getParameterCount() == 2 && c.getParameterTypes()[1] == Set.class) {
                    ctor = c;
                    break;
                }
            }
            if (ctor == null) throw new RuntimeException("BlockEntityType constructor (supplier, Set) not found");
            ctor.setAccessible(true);
            BlockEntityType<T> type = (BlockEntityType<T>) ctor.newInstance(supplier, Set.of(validBlocks));
            return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, OrangeSunshine.id(id), type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create BlockEntityType for " + id, e);
        }
    }

    public static void bootstrap() {
        DRYING_TABLE = create("drying_table", DryingTableBlockEntity::new, PSBlocks.DRYING_TABLE, PSBlocks.IRON_DRYING_TABLE);
        MASH_TUB = create("wooden_vat", MashTubBlockEntity::new, PSBlocks.MASH_TUB);
        MASH_TUB_EDGE = create("wooden_vat_edge", MashTubWallBlock.MasterPosition::new, PSBlocks.MASH_TUB_EDGE);
        RIFT_JAR = create("rift_jar", RiftJarBlockEntity::new, PSBlocks.RIFT_JAR);
        PEYOTE = create("peyote", PeyoteBlockEntity::new, PSBlocks.PEYOTE);
        DISTILLERY = create("distillery", DistilleryBlockEntity::new, PSBlocks.DISTILLERY);
        BOTTLE_RACK = create("bottle_rack", BottleRackBlockEntity::new, PSBlocks.BOTTLE_RACK);
        FLASK = create("flask", FlaskBlockEntity::new, PSBlocks.FLASK);
        BARREL = create("barrel", BarrelBlockEntity::new,
                PSBlocks.OAK_BARREL, PSBlocks.SPRUCE_BARREL,
                PSBlocks.BIRCH_BARREL, PSBlocks.JUNGLE_BARREL,
                PSBlocks.ACACIA_BARREL, PSBlocks.DARK_OAK_BARREL);
        PLACED_DRINK = create("placed_drink", PlacedDrinksBlock.Data::new, PSBlocks.PLACED_DRINK);
        MORTAR_PESTLE_BLOCK_ENTITY = create("mortar_pestle", MortarPestleBlockEntity::new, PSBlocks.MORTAR_PESTLE);
        MIXING_TABLE_BLOCK_ENTITY = create("mixing_table", MixingTableBlockEntity::new, PSBlocks.MIXING_TABLE);
    }
}
