package com.BrotherHoodOfDiethylamide.OrangeSunshine.mixin.client;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.RenderUtil;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.ShaderRenderer;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.events.hooks.*;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks onto after camera matrix is set, upon chunk layer rendering, upon entity rendering, and upon tile entity rendering
 */
@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Shadow @Final private RenderTypeBuffers renderBuffers;

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V", shift = At.Shift.AFTER), method = "renderLevel")
    private void updateCameraAndRender(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new SetCameraEvent(matrixStackIn, partialTicks, finishTimeNano, drawBlockOutline, activeRenderInfoIn, gameRendererIn, lightmapIn, projectionIn));
    }

    @Inject(at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/IProfiler;push(Ljava/lang/String;)V", args = {"ldc=filterempty"}), method = "renderChunkLayer")
    private void renderPreChunkLayer(RenderType renderType, MatrixStack matrixStack, double x, double y, double z, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new RenderEvent.RenderTerrainEvent(RenderEvent.Phase.START, renderType));
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/IProfiler;pop()V", shift = At.Shift.AFTER, ordinal = 1), method = "renderChunkLayer")
    private void renderPostChunkLayer(RenderType renderType, MatrixStack matrixStack, double x, double y, double z, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new RenderEvent.RenderTerrainEvent(RenderEvent.Phase.END, renderType));
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/DimensionRenderInfo;constantAmbientLight()Z"), method = "renderLevel")
    private void renderPostTerrain(MatrixStack matrixStack, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci) {
        if (ShaderRenderer.useShader) RenderUtil.flushRenderBuffer();
    }

    @Inject(at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/IProfiler;popPush(Ljava/lang/String;)V", args = {"ldc=entities"}), method = "renderLevel")
    private void renderPreEntity(MatrixStack matrixStack, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new RenderEvent.RenderEntityEvent(RenderEvent.Phase.START, null));
    }

    @Inject(at = @At("HEAD"), method = "renderEntity")
    private void flushEntity(Entity entity, double camX, double camY, double camZ, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, CallbackInfo ci) {
        if (ShaderRenderer.useShader) {
            MinecraftForge.EVENT_BUS.post(new RenderEvent.RenderEntityEvent(RenderEvent.Phase.START, entity));
            RenderUtil.flushRenderBuffer();
        }
    }

    @Inject(at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/IProfiler;popPush(Ljava/lang/String;)V", args = {"ldc=blockentities"}), method = "renderLevel")
    private void renderPreBlockEntity(MatrixStack matrixStack, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new RenderEvent.RenderEntityEvent(RenderEvent.Phase.END, null));
        MinecraftForge.EVENT_BUS.post(new RenderEvent.RenderBlockEntityEvent(RenderEvent.Phase.START));
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderTypeBuffers;outlineBufferSource()Lnet/minecraft/client/renderer/OutlineLayerBuffer;", ordinal = 1), method = "renderLevel")
    private void drawBanner(CallbackInfo ci) {
        if (!RenderUtil.hasOptifine) // Optifine already does this
            renderBuffers.bufferSource().endBatch(Atlases.bannerSheet());
    }

    @Inject(at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/IProfiler;popPush(Ljava/lang/String;)V", args = {"ldc=destroyProgress"}), method = "renderLevel")
    private void renderPostBlockEntity(MatrixStack matrixStack, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new RenderEvent.RenderBlockEntityEvent(RenderEvent.Phase.END));
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;renderHitOutline(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;Lnet/minecraft/entity/Entity;DDDLnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"), method = "renderLevel")
    private void renderPreBlockOutline(MatrixStack matrixStack, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new RenderEvent.RenderBlockOutlineEvent(RenderEvent.Phase.START, renderBuffers.bufferSource()));
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;renderHitOutline(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;Lnet/minecraft/entity/Entity;DDDLnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", shift = At.Shift.AFTER), method = "renderLevel")
    private void renderPostBlockOutline(MatrixStack matrixStack, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new RenderEvent.RenderBlockOutlineEvent(RenderEvent.Phase.END, renderBuffers.bufferSource()));
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;renderParticles(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/ActiveRenderInfo;FLnet/minecraft/client/renderer/culling/ClippingHelper;)V"), method = "renderLevel")
    private void renderPreParticles(MatrixStack matrixStack, float p_228426_2_, long p_228426_3_, boolean p_228426_5_, ActiveRenderInfo p_228426_6_, GameRenderer p_228426_7_, LightTexture p_228426_8_, Matrix4f p_228426_9_, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new RenderEvent.RenderParticlesEvent(RenderEvent.Phase.START));
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;renderParticles(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/ActiveRenderInfo;FLnet/minecraft/client/renderer/culling/ClippingHelper;)V", shift = At.Shift.AFTER), method = "renderLevel")
    private void renderPostParticles(MatrixStack matrixStack, float p_228426_2_, long p_228426_3_, boolean p_228426_5_, ActiveRenderInfo p_228426_6_, GameRenderer p_228426_7_, LightTexture p_228426_8_, Matrix4f p_228426_9_, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new RenderEvent.RenderParticlesEvent(RenderEvent.Phase.END));
    }
}
