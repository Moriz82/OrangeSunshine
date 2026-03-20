package moriz.orangesunshine.mixin.client;

import moriz.orangesunshine.client.render.DrugEffectInterpreter;
import moriz.orangesunshine.entity.drug.DrugProperties;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
abstract class MixinCamera {
    /**
     * Force the camera to detach (third-person) when a hallucination has pushed the
     * virtual camera position far enough out-of-body.
     */
    @Inject(method = "isDetached", at = @At("HEAD"), cancellable = true, require = 0)
    private void onIsDetached(CallbackInfoReturnable<Boolean> info) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        DrugProperties.of((Entity) mc.player).ifPresent(properties -> {
            if (DrugEffectInterpreter.getOOB(properties)) {
                info.setReturnValue(true);
            }
        });
    }
}
