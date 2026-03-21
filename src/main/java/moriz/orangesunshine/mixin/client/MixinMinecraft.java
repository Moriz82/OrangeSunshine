package moriz.orangesunshine.mixin.client;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.client.render.DrugRenderer;
import moriz.orangesunshine.entity.drug.DrugProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class MixinMinecraft {
    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo info) {
        Minecraft mc = (Minecraft) (Object) this;
        if (mc.player != null) {
            DrugProperties.of((Entity) mc.player).ifPresent(properties -> {
                DrugRenderer.INSTANCE.update(properties, mc.player);
            });
        }
    }
}
