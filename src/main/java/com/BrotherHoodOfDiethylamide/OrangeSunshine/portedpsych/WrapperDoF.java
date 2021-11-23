/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvDepthBuffer;
import net.minecraft.client.Minecraft;

/**
 * Created by lukas on 26.04.14.
 */
public class WrapperDoF extends ShaderWrapper<ShaderDoF>
{
    public WrapperDoF(String utils)
    {
        super(new ShaderDoF(OrangeSunshine.logger), getRL("shaderBasic.vert"), getRL("shaderDof.frag"), utils);
    }

    public boolean isActive()
    {
        return (128f < getCurrentZFar());
    }

    protected float getCurrentZFar()
    {
        return (float) (Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16);
    }

    @Override
    public void setShaderValues(float partialTicks, int ticks, IvDepthBuffer depthBuffer)
    {
        if (depthBuffer != null && isActive())
        {
            shaderInstance.depthTextureIndex = depthBuffer.getDepthTextureIndex();

            shaderInstance.zNear = 0.05f;
            shaderInstance.zFar = getCurrentZFar();

            shaderInstance.focalPointNear =  0.2f / shaderInstance.zFar;
            shaderInstance.focalPointFar = 128f / shaderInstance.zFar;
            shaderInstance.focalBlurFar = 0f;
            shaderInstance.focalBlurNear = 0f;
        }
        else
        {
            shaderInstance.focalBlurFar = 0.0f;
            shaderInstance.focalBlurNear = 0.0f;
        }
    }

    @Override
    public void update()
    {

    }

    @Override
    public boolean wantsDepthBuffer(float partialTicks)
    {
        return isActive();
    }
}
