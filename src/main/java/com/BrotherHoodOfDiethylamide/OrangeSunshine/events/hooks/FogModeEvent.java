package com.BrotherHoodOfDiethylamide.OrangeSunshine.events.hooks;

import net.minecraftforge.eventbus.api.Event;

public class FogModeEvent extends Event {
    public final int mode;

    public FogModeEvent(int mode) {
        this.mode = mode;
    }
}
