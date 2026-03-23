package moriz.orangesunshine.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moriz.orangesunshine.client.render.DrugRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
abstract class MixinGameRenderer {

    /**
     * Inject into the private bobHurt camera-shake method so that drug-induced
     * screen distortion (camera drift, wobble, translation) is applied together
     * with the vanilla hurt-bob transform.  Using TAIL so vanilla transform
     * runs first and drug effects are layered on top.
     */
    @Inject(method = "bobHurt", at = @At("TAIL"), require = 0)
    private void onBobHurt(PoseStack poseStack, float partialTick, CallbackInfo ci) {
        DrugRenderer.INSTANCE.distortScreen(poseStack, partialTick);
    }

    /**
     * Run custom post chains once per frame, in the same phase as vanilla {@code postEffectId}
     * processing: after the world (and outline) is drawn to {@code minecraft:main}, but
     * <strong>before</strong> depth is cleared for the GUI pass. Running at {@code TAIL} or from
     * {@link net.minecraft.client.gui.Gui} caused double application and/or GPU desync (black screen).
     */
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/CommandEncoder;clearDepthTexture(Lcom/mojang/blaze3d/textures/GpuTexture;D)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0))
    private void orangesunshine$runPostEffectsBeforeGuiDepthClear(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        DrugRenderer.INSTANCE.getPostEffects().render(deltaTracker.getGameTimeDeltaTicks());
    }
}
