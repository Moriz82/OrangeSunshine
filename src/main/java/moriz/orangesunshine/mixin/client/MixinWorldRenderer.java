package moriz.orangesunshine.mixin.client;

import moriz.orangesunshine.client.render.DrugRenderer;
import moriz.orangesunshine.entity.drug.DrugProperties;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
abstract class MixinWorldRenderer {

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;execute(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder$Inspector;)V"), require = 0)
    private void onRenderLevel(GraphicsResourceAllocator allocator, DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Matrix4f projectionMatrix2, GpuBufferSlice gpuBufferSlice, Vector4f vector4f, boolean bl, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            DrugProperties properties = DrugProperties.of(mc.player);
            if (properties != null && properties.getHallucinations().getHallucinationStrength(deltaTracker.getGameTimeDeltaPartialTick(false)) > 0) {
                // This might be too late as the frame graph is about to execute, but let's try.
                // In a real port, we should add a pass to the FrameGraph.
                // For now, let's just call it and see if it crashes.
                PoseStack matrices = new PoseStack();
                matrices.pushPose();
                matrices.mulPose(modelViewMatrix);
                DrugRenderer.INSTANCE.renderAllHallucinations(matrices, mc.renderBuffers().bufferSource(), camera, deltaTracker.getGameTimeDeltaPartialTick(false), properties);
                matrices.popPose();
            }
        }
    }
}
