package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import net.minecraft.util.math.MathHelper;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

/**
 * Created by lukas on 25.02.14.
 */
@ParametersAreNonnullByDefault
public class DrugEffectInterpreter
{
    public static float getSmoothVision(com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties drugProperties)
    {
        float smoothVision = 0.0f;
        for (Drug drug : drugProperties.getAllDrugs())
            smoothVision += drug.headMotionInertness();
        return  1.0f / (1.0f + smoothVision);
    }

    public static float getCameraShiftY(com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties drugProperties, float ticks)
    {
        float amplitude = 0.0f;

        for (Drug drug : drugProperties.getAllDrugs())
            amplitude += (1.0f - amplitude) * drug.viewTrembleStrength();

        if (amplitude > 0.0f)
            return MathHelper.sin(ticks / 3.0f) * MathHelper.sin(ticks / 3.0f) * amplitude * 0.1f;

        return 0.0f;
    }

    public static float getCameraShiftX(com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties drugProperties, float ticks)
    {
        float amplitude = 0.0f;

        for (Drug drug : drugProperties.getAllDrugs())
            amplitude += (1.0f - amplitude) * drug.viewTrembleStrength();

        if (amplitude > 0.0f)
            return (new Random((long) (ticks * 1000.0f)).nextFloat() - 0.5f) * 0.05f * amplitude;

        return 0.0f;
    }

    public static float getHandShiftY(com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties drugProperties, float ticks)
    {
        return getCameraShiftY(drugProperties, ticks) * 0.3f;
    }

    public static float getHandShiftX(com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties drugProperties, float ticks)
    {
        float amplitude = 0.0f;

        for (Drug drug : drugProperties.getAllDrugs())
            amplitude += (1.0f - amplitude) * drug.handTrembleStrength();

        if (amplitude > 0.0f)
            return (new Random((long) (ticks * 1000.0f)).nextFloat() - 0.5f) * 0.015f * amplitude;

        return 0.0f;
    }
}
