package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugEffects;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvDepthBuffer;

public class WrapperRecursion extends ShaderWrapper<ShaderRecursion>
{
    public WrapperRecursion(String utils)
    {
        super(new ShaderRecursion(OrangeSunshine.logger), getRL("shaderBasic.vert"), getRL("shaderRecursion.frag"), utils);
    }

    @Override
    public void setShaderValues(float partialTicks, int ticks, IvDepthBuffer depthBuffer)
    {
        DrugEffects _de = Drug.getDrugEffects();
        shaderInstance.recursionStrength = Math.min(_de.RECURSION.getValue() * 0.8f, 1.0f);
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
