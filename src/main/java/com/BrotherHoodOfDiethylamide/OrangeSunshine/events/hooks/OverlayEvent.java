package com.BrotherHoodOfDiethylamide.OrangeSunshine.events.hooks;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

@OnlyIn(Dist.CLIENT)
public class OverlayEvent extends Event {
    public final boolean enabled;

    public OverlayEvent(boolean enabled) {
        this.enabled = enabled;
    }
}
