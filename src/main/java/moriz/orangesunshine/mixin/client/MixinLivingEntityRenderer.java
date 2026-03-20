package moriz.orangesunshine.mixin.client;

import moriz.orangesunshine.client.render.DrugRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
abstract class MixinLivingEntityRenderer {
    /**
     * After the model has been animated, apply drug-induced limb and head poses
     * to the player's model so they look appropriately impaired.
     */
    @Inject(method = "render",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
                shift = At.Shift.AFTER),
            require = 0)
    private <T extends LivingEntity> void onRenderAfterSetupAnim(
            T entity, float entityYaw, float partialTick,
            PoseStack poseStack, MultiBufferSource buffer, int packedLight,
            CallbackInfo ci) {
        if (entity instanceof Player player) {
            LivingEntityRenderer<?, ?, ?> self = (LivingEntityRenderer<?, ?, ?>) (Object) this;
            if (self.getModel() instanceof HumanoidModel<?> model) {
                DrugRenderer.INSTANCE.poseModel(player, model, partialTick);
            }
        }
    }
}
