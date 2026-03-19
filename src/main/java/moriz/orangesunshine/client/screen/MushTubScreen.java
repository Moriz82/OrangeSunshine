package moriz.orangesunshine.client.screen;

import moriz.orangesunshine.block.entity.MashTubBlockEntity;
import moriz.orangesunshine.screen.FluidContraptionScreenHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;

/**
 * Created by lukas on 13.11.14.
 * Updated by Sollace on 4 Jan 2023
 */
public class MushTubScreen extends FluidProcessingContraptionScreen<MashTubBlockEntity> {
    public MushTubScreen(FluidContraptionScreenHandler<MashTubBlockEntity> handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawAdditionalInfo(GuiGraphics context, int baseX, int baseY) {
        float progress = handler.getBlockEntity().getProgress();
        if (progress > 0 && progress < 1) {
            context.blit(RenderPipelines.GUI_TEXTURED, background, baseX + 140, baseY + 14, 233, 22, 23, 22, 256, 256);
            int barHeight = (int)(22 * (1 - progress));
            context.blit(RenderPipelines.GUI_TEXTURED, background, baseX + 140, baseY + 14 + barHeight, 233, barHeight, 23, 23 - barHeight, 256, 256);
        }
    }
}
