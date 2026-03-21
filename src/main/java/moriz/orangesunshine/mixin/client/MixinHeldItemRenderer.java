package moriz.orangesunshine.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moriz.orangesunshine.client.render.DrugRenderer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
abstract class MixinHeldItemRenderer {

    /**
     * Apply drug-induced hand tremor / translation before the held-item arms
     * are rendered.  Injecting at HEAD so the distortion is baked into the
     * PoseStack that all subsequent arm transforms build on.
     */
    @Inject(
        method = "renderHandsWithItems",
        at = @At("HEAD"),
        require = 0
    )
    private void onRenderHandsWithItems(
            float partialTick,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            LocalPlayer player,
            int combinedLight,
            CallbackInfo ci) {
        DrugRenderer.INSTANCE.distortHand(poseStack, partialTick);
    }
}
