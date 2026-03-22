package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugEffects;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvDepthBuffer;

public class WrapperKaleidoscope extends ShaderWrapper<ShaderKaleidoscope>
{
    public WrapperKaleidoscope(String utils)
    {
        super(new ShaderKaleidoscope(OrangeSunshine.logger), getRL("shaderBasic.vert"), getRL("shaderkaleidoscope.frag"), utils);
    }

    @Override
    public void setShaderValues(float partialTicks, int ticks, IvDepthBuffer depthBuffer)
    {
        DrugEffects _de = Drug.getDrugEffects();
        shaderInstance.kaleidoscopeStrength = Math.min(_de.KALEIDOSCOPE_INTENSITY.getValue() * 0.8f, 1.0f);
    }

    @Override
    public void update()
    {
    }

    @Override
    public boolean wantsDepthBuffer(float partialTicks)
    {
        return false;
    }
}
