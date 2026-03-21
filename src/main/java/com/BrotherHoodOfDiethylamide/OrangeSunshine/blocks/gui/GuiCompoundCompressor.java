package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.gui;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container.ContainerCompoundCompressor;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity.TileCompoundCompressor;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiCompoundCompressor extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(OrangeSunshine.MODID, "textures/gui/compound_compressor_gui.png");
    private final TileCompoundCompressor tile;

    public GuiCompoundCompressor(InventoryPlayer playerInv, TileCompoundCompressor tile) {
        super(new ContainerCompoundCompressor(playerInv, tile));
        this.tile = tile;
        this.xSize = 176;
        this.ySize = 186;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = "Compound Compressor";
        fontRenderer.drawString(title, xSize / 2 - fontRenderer.getStringWidth(title) / 2, 6, 0x404040);
        fontRenderer.drawString("Inventory", 8, ySize - 96 + 2, 0x404040);
    }
}
