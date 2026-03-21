package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.gui;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container.ContainerDryingTable;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity.TileDryingTable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiDryingTable extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(OrangeSunshine.MODID, "textures/gui/drying_table_gui.png");
    private final TileDryingTable tile;

    public GuiDryingTable(InventoryPlayer playerInv, TileDryingTable tile) {
        super(new ContainerDryingTable(playerInv, tile));
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

        // Draw progress arrows for active slots
        for (int i = 0; i < TileDryingTable.INPUT_SLOTS; i++) {
            int process = tile.getProcessTime(i);
            int total = tile.getTotalTime(i);
            if (process > 0 && total > 0) {
                // Small progress indicator per slot
                int row = i / 3;
                int col = i % 3;
                int slotX = x + 8 + col * 18;
                int slotY = y + 18 + row * 18;
                int prog = (int) (16 * process / (float) total);
                drawTexturedModalRect(slotX, slotY + 16, 176, 0, prog, 4);
                break; // Just show first active slot's progress
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = "Drying Table";
        fontRenderer.drawString(title, xSize / 2 - fontRenderer.getStringWidth(title) / 2, 6, 0x404040);
        fontRenderer.drawString("Inventory", 8, ySize - 96 + 2, 0x404040);
    }
}
