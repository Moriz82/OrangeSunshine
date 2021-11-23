package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by lukas on 13.03.14.
 */
public class GLFogiEvent extends Event
{
    public final int pname;
    public final int param;

    public GLFogiEvent(int pname, int param)
    {
        this.pname = pname;
        this.param = param;
    }
}
