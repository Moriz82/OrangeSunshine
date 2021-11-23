package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by lukas on 13.03.14.
 */
@Cancelable
public class GLClearEvent extends Event
{
    public final int initialMask;
    public int currentMask;

    public GLClearEvent(int mask)
    {
        this.initialMask = mask;
        this.currentMask = mask;
    }
}
