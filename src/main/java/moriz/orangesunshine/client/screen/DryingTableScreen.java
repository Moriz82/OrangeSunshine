/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.screen;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.screen.DryingTableScreenHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Updated by Sollace on 3 Jan 2023
 */
public class DryingTableScreen extends AbstractContainerScreen<DryingTableScreenHandler> {
    public static final Identifier TEXTURE = OrangeSunshine.id("textures/gui/drying_table.png");

    public DryingTableScreen(DryingTableScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    public void init() {
        super.init();
        titleLabelX = 26;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderTooltip(context, mouseX, mouseY);

        if (menu.getCarried().isEmpty() && (hoveredSlot == null || !hoveredSlot.hasItem())) {
            int centerX = (width - imageWidth) / 2 + imageWidth;
            int centerY = (height - imageHeight) / 2;

            if (mouseX > centerX - 30 && mouseX < centerX
                    && mouseY > centerY && mouseY < centerY + 30) {
                context.setTooltipForNextFrame(font, Component.translatable("block.orangesunshine.drying_table.daylight", (int)(menu.getHeatRatio() * 100)), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int centerX = (width - imageWidth) / 2;
        int centerY = (height - imageHeight) / 2;

        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, centerX, centerY, 0, 0, imageWidth, imageHeight, 256, 256);

        if (menu.getProgress() > 0) {
            context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, centerX + 88, centerY + 34, 176, 59, 25, 16, 256, 256);
        }

        int progress = (int)(menu.getProgress() * 24);
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, centerX + 88, centerY + 34, 176, 42, progress + 1, 16, 256, 256);

        int heat = (int)(menu.getHeatRatio() * 20);
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, centerX + 148, centerY + 6 + (20 - heat), 176, 21 + (20 - heat), 20, heat, 256, 256);
    }
}
