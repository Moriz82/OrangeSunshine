package com.BrotherHoodOfDiethylamide.OrangeSunshine.events.hooks;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.mixin.client.MixinLightTexture;
import net.minecraft.client.renderer.LightTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fires when light map is toggled
 * Hooks into {@link LightTexture#turnOnLightLayer()} and {@link LightTexture#turnOffLightLayer()} by {@link MixinLightTexture}
 */
@OnlyIn(Dist.CLIENT)
public class EnableLightMapEvent extends Event {
    public final boolean enabled;

    public EnableLightMapEvent(boolean enabled) {
        this.enabled = enabled;
    }
}
