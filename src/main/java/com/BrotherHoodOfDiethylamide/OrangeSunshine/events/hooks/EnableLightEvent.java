package com.BrotherHoodOfDiethylamide.OrangeSunshine.events.hooks;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fires when lighting is toggled
 */
@OnlyIn(Dist.CLIENT)
public class EnableLightEvent extends Event {
    public final boolean enabled;

    public EnableLightEvent(boolean enabled) {
        this.enabled = enabled;
    }
}
