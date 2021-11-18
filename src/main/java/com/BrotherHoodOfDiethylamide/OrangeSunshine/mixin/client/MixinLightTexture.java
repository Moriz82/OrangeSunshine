package com.BrotherHoodOfDiethylamide.OrangeSunshine.mixin.client;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.events.hooks.EnableLightMapEvent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks onto when the light map toggles
 */
@Mixin(LightTexture.class)
public class MixinLightTexture {
    @Inject(at = @At("HEAD"), method = "turnOnLightLayer")
    private void onEnable(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new EnableLightMapEvent(true));
    }

    @Inject(at = @At("HEAD"), method = "turnOffLightLayer")
    private void onDisable(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new EnableLightMapEvent(false));
    }
}
