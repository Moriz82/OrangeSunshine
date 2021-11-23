package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by lukas on 17.11.14.
 */
public class RenderSkyEvent extends Event
{
    public final float partialTicks;

    public RenderSkyEvent(float partialTicks)
    {
        this.partialTicks = partialTicks;
    }

    public static class Pre extends RenderSkyEvent
    {
        public Pre(float partialTicks)
        {
            super(partialTicks);
        }
    }
}
