package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by lukas on 13.03.14.
 */
public class GLSwitchEvent extends Event
{
    public final int cap;
    public final boolean enable;

    public GLSwitchEvent(int cap, boolean enable)
    {
        this.cap = cap;
        this.enable = enable;
    }
}
