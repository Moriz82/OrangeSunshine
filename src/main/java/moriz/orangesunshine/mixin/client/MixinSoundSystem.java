package moriz.orangesunshine.mixin.client;

import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
abstract class MixinSoundSystem {
    /** Scale the game's sound volume up or down based on active drug modifiers. */
    @Inject(method = "getVolume", at = @At("RETURN"), cancellable = true, require = 0)
    private void onGetVolume(@Nullable SoundSource category, CallbackInfoReturnable<Float> info) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        DrugProperties.of((Entity) mc.player).ifPresent(properties ->
            info.setReturnValue(
                Mth.clamp(info.getReturnValueF() * properties.getModifier(Drug.SOUND_VOLUME), 0, 1)
            )
        );
    }
}
