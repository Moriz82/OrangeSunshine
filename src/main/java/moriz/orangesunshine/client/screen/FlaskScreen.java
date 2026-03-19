/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.screen;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.entity.FlaskBlockEntity;
import moriz.orangesunshine.screen.FluidContraptionScreenHandler;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Created by lukas on 26.10.14.
 * Updated by Sollace on 4 Jan 2023
 */
public class FlaskScreen<T extends FlaskBlockEntity> extends AbstractFluidContraptionScreen<FluidContraptionScreenHandler<T>> {
    protected final Identifier background;

    public FlaskScreen(FluidContraptionScreenHandler<T> handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.background = OrangeSunshine.id("textures/gui/" + BlockEntityType.getKey(handler.getBlockEntity().getType()).getPath() + ".png");
    }

    @Override
    protected void renderBg(GuiGraphics context, float partialTicks, int mouseX, int mouseY) {
        int baseX = (width - imageWidth) / 2;
        int baseY = (height - imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, background, baseX + 30, baseY + 20, 0, imageHeight, 110, 50, 256, 256);

        drawTanks(context, baseX, baseY);

        context.blit(RenderPipelines.GUI_TEXTURED, background, baseX, baseY, 0, 0, imageWidth, imageHeight, 256, 256);
        drawAdditionalInfo(context, baseX, baseY);

        float inputProgress = handler.getBlockEntity().inputSlot.getProgress();
        if (inputProgress > 0 && inputProgress < 1) {
            int width = (int)(45 * inputProgress);
            context.blit(RenderPipelines.GUI_TEXTURED, background, baseX + 20 + width, baseY + 40, 176 + width, 0, 45 - width, 16, 256, 256);
        }
        float outputProgress = handler.getBlockEntity().outputSlot.getProgress();
        if (outputProgress > 0 && outputProgress < 1) {
            context.blit(RenderPipelines.GUI_TEXTURED, background, baseX + 68, baseY + 60, 176, 17, (int)(53 * outputProgress), 20, 256, 256);
        }
    }

    protected void drawAdditionalInfo(GuiGraphics context, int baseX, int baseY) {

    }

    protected void drawTanks(GuiGraphics context, int baseX, int baseY) {
        drawTank(context, getTank(), baseX + 48, baseY + 59, 64, 27);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        super.render(context, mouseX, mouseY, tickDelta);
        int baseX = (width - imageWidth) / 2;
        int baseY = (height - imageHeight) / 2;
        drawTankTooltips(context, mouseX, mouseY, baseX, baseY);
    }

    protected void drawTankTooltips(GuiGraphics context, int mouseX, int mouseY, int baseX, int baseY) {
        drawTankTooltip(context, getTank(), baseX + 65, baseY + 33, 40, 30, mouseX, mouseY, getAdditionalTankText());
    }

    protected List<Component> getAdditionalTankText() {
        return List.of();
    }
}
