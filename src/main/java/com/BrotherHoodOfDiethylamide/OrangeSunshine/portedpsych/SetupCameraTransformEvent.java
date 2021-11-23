package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by lukas on 13.03.14.
 */
@Cancelable
public class SetupCameraTransformEvent extends Event
{
    public final float partialTicks;

    public SetupCameraTransformEvent(float partialTicks)
    {
        this.partialTicks = partialTicks;
    }
}
