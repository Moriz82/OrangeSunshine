/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvDepthBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

/**
 * Created by lukas on 26.04.14.
 */
public class WrapperDoubleVision extends ShaderWrapper<ShaderDoubleVision>
{
    public WrapperDoubleVision(String utils)
    {
        super(new ShaderDoubleVision(OrangeSunshine.logger), getRL("shaderBasic.vert"), getRL("shaderDoubleVision.frag"), utils);
    }

    @Override
    public void setShaderValues(float partialTicks, int ticks, IvDepthBuffer depthBuffer)
    {
        DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity());

        shaderInstance.doubleVision = 0.0f;

        if (drugProperties != null)
        {
            for (Drug drug : drugProperties.getAllDrugs())
                shaderInstance.doubleVision += (1.0f - shaderInstance.doubleVision) * drug.doubleVision();

            shaderInstance.doubleVisionDistance = MathHelper.sin((ticks + partialTicks) / 20.0f) * 0.05f * shaderInstance.doubleVision;
        }
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
