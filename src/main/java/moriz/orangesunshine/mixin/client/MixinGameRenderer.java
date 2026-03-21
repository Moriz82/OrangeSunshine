package moriz.orangesunshine.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moriz.orangesunshine.client.render.DrugRenderer;
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
}
