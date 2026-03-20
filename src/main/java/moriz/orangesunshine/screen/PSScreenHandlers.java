package moriz.orangesunshine.screen;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.entity.BarrelBlockEntity;
import moriz.orangesunshine.block.entity.DistilleryBlockEntity;
import moriz.orangesunshine.block.entity.FlaskBlockEntity;
import moriz.orangesunshine.block.entity.MashTubBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * @author Sollace
 * @since 12 Jan 2023
 */
public interface PSScreenHandlers {
    MenuType<DryingTableScreenHandler> DRYING_TABLE = register("drying_table",
            new MenuType<>((syncId, inventory) -> new DryingTableScreenHandler(syncId, inventory, new BlockPosData(BlockPos.ZERO)), FeatureFlagSet.of()));

    MenuType<FluidContraptionScreenHandler<BarrelBlockEntity>> BARREL = register("barrel",
            new MenuType<>((syncId, inventory) -> new FluidContraptionScreenHandler<>(PSScreenHandlers.BARREL, syncId, inventory, new BlockSideData(BlockPos.ZERO, Direction.NORTH)), FeatureFlagSet.of()));
    MenuType<FluidContraptionScreenHandler<DistilleryBlockEntity>> DISTILLERY = register("distillery",
            new MenuType<>((syncId, inventory) -> new FluidContraptionScreenHandler<>(PSScreenHandlers.DISTILLERY, syncId, inventory, new BlockSideData(BlockPos.ZERO, Direction.NORTH)), FeatureFlagSet.of()));
    MenuType<FluidContraptionScreenHandler<FlaskBlockEntity>> FLASK = register("flask",
            new MenuType<>((syncId, inventory) -> new FluidContraptionScreenHandler<>(PSScreenHandlers.FLASK, syncId, inventory, new BlockSideData(BlockPos.ZERO, Direction.NORTH)), FeatureFlagSet.of()));
    MenuType<FluidContraptionScreenHandler<MashTubBlockEntity>> MASH_TUB = register("mash_tub",
            new MenuType<>((syncId, inventory) -> new FluidContraptionScreenHandler<>(PSScreenHandlers.MASH_TUB, syncId, inventory, new BlockSideData(BlockPos.ZERO, Direction.NORTH)), FeatureFlagSet.of()));
    MenuType<MortarPestleScreenHandler> MORTAR_PESTLE =
            Registry.register(BuiltInRegistries.MENU, OrangeSunshine.id("mortar_pestle"),
                    new MenuType<>((syncId, inventory) -> new MortarPestleScreenHandler(syncId, inventory, new BlockPosData(BlockPos.ZERO)), FeatureFlagSet.of()));

    MenuType<MixingTableScreenHandler> MIXING_TABLE =
            Registry.register(BuiltInRegistries.MENU, OrangeSunshine.id("mixing_table"),
                    new MenuType<>((syncId, inventory) -> new MixingTableScreenHandler(syncId, inventory, new BlockPosData(BlockPos.ZERO)), FeatureFlagSet.of()));

    static <T extends AbstractContainerMenu> MenuType<T> register(String name, MenuType<T> type) {
        return Registry.register(BuiltInRegistries.MENU, OrangeSunshine.id(name), type);
    }

    static void bootstrap() { }

    record BlockPosData(BlockPos pos) {
        public static final StreamCodec<RegistryFriendlyByteBuf, BlockPosData> STREAM_CODEC = StreamCodec.of(BlockPosData::write, BlockPosData::read);

        private static BlockPosData read(RegistryFriendlyByteBuf buffer) {
            return new BlockPosData(buffer.readBlockPos());
        }

        private static void write(RegistryFriendlyByteBuf buffer, BlockPosData data) {
            buffer.writeBlockPos(data.pos);
        }
    }

    record BlockSideData(BlockPos pos, Direction direction) {
        public static final StreamCodec<RegistryFriendlyByteBuf, BlockSideData> STREAM_CODEC = StreamCodec.of(BlockSideData::write, BlockSideData::read);

        private static BlockSideData read(RegistryFriendlyByteBuf buffer) {
            return new BlockSideData(buffer.readBlockPos(), buffer.readEnum(Direction.class));
        }

        private static void write(RegistryFriendlyByteBuf buffer, BlockSideData data) {
            buffer.writeBlockPos(data.pos);
            buffer.writeEnum(data.direction);
        }
    }
}
