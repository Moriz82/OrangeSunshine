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
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * @author Sollace
 * @since 12 Jan 2023
 */
public interface PSScreenHandlers {
    MenuType<DryingTableScreenHandler> DRYING_TABLE = register("drying_table", new ExtendedScreenHandlerType<>(DryingTableScreenHandler::new, BlockPosData.STREAM_CODEC));

    MenuType<FluidContraptionScreenHandler<BarrelBlockEntity>> BARREL = register("barrel", new ExtendedScreenHandlerType<>(
            (sync, inventory, data) -> new FluidContraptionScreenHandler<>(PSScreenHandlers.BARREL, sync, inventory, data),
            BlockSideData.STREAM_CODEC
    ));
    MenuType<FluidContraptionScreenHandler<DistilleryBlockEntity>> DISTILLERY = register("distillery", new ExtendedScreenHandlerType<>(
            (sync, inventory, data) -> new FluidContraptionScreenHandler<>(PSScreenHandlers.DISTILLERY, sync, inventory, data),
            BlockSideData.STREAM_CODEC
    ));
    MenuType<FluidContraptionScreenHandler<FlaskBlockEntity>> FLASK = register("flask", new ExtendedScreenHandlerType<>(
            (sync, inventory, data) -> new FluidContraptionScreenHandler<>(PSScreenHandlers.FLASK, sync, inventory, data),
            BlockSideData.STREAM_CODEC
    ));
    MenuType<FluidContraptionScreenHandler<MashTubBlockEntity>> MASH_TUB = register("mash_tub", new ExtendedScreenHandlerType<>(
            (sync, inventory, data) -> new FluidContraptionScreenHandler<>(PSScreenHandlers.MASH_TUB, sync, inventory, data),
            BlockSideData.STREAM_CODEC
    ));
    MenuType<MortarPestleScreenHandler> MORTAR_PESTLE =
            Registry.register(BuiltInRegistries.MENU, OrangeSunshine.id("mortar_pestle"),
                    new ExtendedScreenHandlerType<>(MortarPestleScreenHandler::new, BlockPosData.STREAM_CODEC));

    MenuType<MixingTableScreenHandler> MIXING_TABLE =
            Registry.register(BuiltInRegistries.MENU, OrangeSunshine.id("mixing_table"),
                    new ExtendedScreenHandlerType<>(MixingTableScreenHandler::new, BlockPosData.STREAM_CODEC));

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
