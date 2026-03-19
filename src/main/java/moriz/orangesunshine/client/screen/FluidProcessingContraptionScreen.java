package moriz.orangesunshine.client.screen;

import moriz.orangesunshine.block.entity.FluidProcessingBlockEntity;
import moriz.orangesunshine.screen.FluidContraptionScreenHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;

import java.util.List;

abstract class FluidProcessingContraptionScreen<T extends FluidProcessingBlockEntity> extends FlaskScreen<T> {

    private final List<Component> processingLabel;

    public FluidProcessingContraptionScreen(FluidContraptionScreenHandler<T> handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        processingLabel = List.of(handler.getBlockEntity().getProcessType().getStatus());
    }

    @Override
    protected final List<Component> getAdditionalTankText() {
        return handler.getBlockEntity().isActive() ? processingLabel : super.getAdditionalTankText();
    }
}
