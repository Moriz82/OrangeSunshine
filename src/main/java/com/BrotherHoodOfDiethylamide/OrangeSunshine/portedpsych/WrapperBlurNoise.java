/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvDepthBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Random;

/**
 * Created by lukas on 26.04.14.
 */
public class WrapperBlurNoise extends ShaderWrapper<ShaderBlurNoise>
{
    public WrapperBlurNoise(String utils)
    {
        super(new ShaderBlurNoise(OrangeSunshine.logger), getRL("shaderBasic.vert"), getRL("shaderBlurNoise.frag"), utils);
    }

    @Override
    public void setShaderValues(float partialTicks, int ticks, IvDepthBuffer depthBuffer)
    {
        DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity());

        if (drugProperties != null)
        {
            shaderInstance.strength = drugProperties.getDrugValue("Power") * 0.6f;
            shaderInstance.seed = new Random((long) ((ticks + partialTicks) * 1000.0)).nextFloat() * 9.0f + 1.0f;
        }
        else
        {
            shaderInstance.strength = 0.0f;
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
