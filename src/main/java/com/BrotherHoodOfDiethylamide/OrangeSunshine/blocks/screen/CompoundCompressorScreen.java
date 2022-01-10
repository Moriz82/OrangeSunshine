package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.screen;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container.CompoundCompressorContainer;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity.CompoundCompressorTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CompoundCompressorScreen extends ContainerScreen<CompoundCompressorContainer> {
    private final ResourceLocation GUI = new ResourceLocation(OrangeSunshine.MOD_ID,
            "textures/gui/compound_compressor_gui.png");

    public CompoundCompressorScreen(CompoundCompressorContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(GUI);
        int i = this.getGuiLeft();
        int j = this.getGuiTop();
        this.blit(matrixStack, i, j, 0, 0, this.getXSize(), this.getYSize());

        if (this.getMenu().isWorking() > 0.0)
            this.blit(matrixStack, i+90, j+35, 177, 42, (int) (23*this.getMenu().isWorking()), 15);
    }
}