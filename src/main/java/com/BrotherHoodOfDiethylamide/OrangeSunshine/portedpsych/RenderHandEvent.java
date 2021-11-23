package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by lukas on 13.03.14.
 */
@Cancelable
public class RenderHandEvent extends Event
{
    public final float partialTicks;

    public RenderHandEvent(float partialTicks)
    {
        this.partialTicks = partialTicks;
    }

    public static class Pre extends RenderHandEvent
    {
        public Pre(float partialTicks)
        {
            super(partialTicks);
        }
    }

    public static class Post extends RenderHandEvent
    {
        public Post(float partialTicks)
        {
            super(partialTicks);
        }

        @Override
        public boolean isCancelable()
        {
            return false;
        }
    }
}