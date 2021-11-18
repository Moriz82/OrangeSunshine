package com.BrotherHoodOfDiethylamide.OrangeSunshine.events.hooks;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

/**
 * Hooks into {@link WorldRenderer#renderLevel}
 * <br>
 * Fires after camera's coordinates has been set
 */
@OnlyIn(Dist.CLIENT)
public class SetCameraEvent extends Event {
    public final MatrixStack matrixStack;
    public final float partialTicks;
    public final long finishTimeNano;
    public final boolean drawBlockOutline;
    public final ActiveRenderInfo activeRenderInfo;
    public final GameRenderer gameRenderer;
    public final LightTexture lightMap;
    public final Matrix4f projection;

    public SetCameraEvent(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn) {
        this.matrixStack = matrixStackIn;
        this.partialTicks = partialTicks;
        this.finishTimeNano = finishTimeNano;
        this.drawBlockOutline = drawBlockOutline;
        this.activeRenderInfo = activeRenderInfoIn;
        this.gameRenderer = gameRendererIn;
        this.lightMap = lightmapIn;
        this.projection = projectionIn;
    }
}
