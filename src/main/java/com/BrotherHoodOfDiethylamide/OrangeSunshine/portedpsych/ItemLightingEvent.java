package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by lukas on 13.03.14.
 */
public class ItemLightingEvent extends Event
{
    public final boolean enable;

    public ItemLightingEvent(boolean enable)
    {
        this.enable = enable;
    }
}
