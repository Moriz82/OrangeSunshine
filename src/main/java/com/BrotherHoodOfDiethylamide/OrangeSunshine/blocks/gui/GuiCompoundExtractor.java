package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.gui;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container.ContainerCompoundExtractor;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity.TileCompoundExtractor;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiCompoundExtractor extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(OrangeSunshine.MODID, "textures/gui/compound_extractor_gui.png");
    private final TileCompoundExtractor tile;

    public GuiCompoundExtractor(InventoryPlayer playerInv, TileCompoundExtractor tile) {
        super(new ContainerCompoundExtractor(playerInv, tile));
        this.tile = tile;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        // Fire indicator
        if (tile.getField(0) > 0) {
            int burn = tile.getField(0);
            int maxBurn = tile.getField(1);
            if (maxBurn > 0) {
                int fireHeight = (int) (13.0 * burn / maxBurn);
                drawTexturedModalRect(x + 56, y + 36 + 13 - fireHeight, 176, 13 - fireHeight, 14, fireHeight);
            }
        }

        // Cook progress arrow
        int cookTime = tile.getField(2);
        int totalCookTime = tile.getField(3);
        if (totalCookTime > 0) {
            int arrowWidth = (int) (24.0 * cookTime / totalCookTime);
            drawTexturedModalRect(x + 79, y + 34, 176, 14, arrowWidth, 17);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = "Compound Extractor";
        fontRenderer.drawString(title, xSize / 2 - fontRenderer.getStringWidth(title) / 2, 6, 0x404040);
        fontRenderer.drawString("Inventory", 8, ySize - 96 + 2, 0x404040);
    }
}
