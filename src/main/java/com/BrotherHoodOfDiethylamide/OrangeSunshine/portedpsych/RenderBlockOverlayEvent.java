package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by lukas on 13.03.14.
 */
@Cancelable
public class RenderBlockOverlayEvent extends Event
{
    public final float partialTicks;

    public RenderBlockOverlayEvent(float partialTicks)
    {
        this.partialTicks = partialTicks;
    }
}
