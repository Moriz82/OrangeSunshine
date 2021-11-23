/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvDepthBuffer;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvOpenGLTexturePingPong;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvShaderInstance2D;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvShaderInstanceMC;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

/**
 * Created by lukas on 26.04.14.
 */
public abstract class ShaderWrapper<ShaderInstance extends IvShaderInstance2D> implements EffectWrapper
{
    public ShaderInstance shaderInstance;

    public ResourceLocation vertexShaderFile;
    public ResourceLocation fragmentShaderFile;

    public String utils;

    public ShaderWrapper(ShaderInstance shaderInstance, ResourceLocation vertexShaderFile, ResourceLocation fragmentShaderFile, String utils)
    {
        this.shaderInstance = shaderInstance;
        this.vertexShaderFile = vertexShaderFile;
        this.fragmentShaderFile = fragmentShaderFile;
        this.utils = utils;
    }

    public static ResourceLocation getRL(String shaderFile)
    {
        return new ResourceLocation(OrangeSunshine.MODID, OrangeSunshine.filePathShaders + shaderFile);
    }

    @Override
    public void alloc()
    {
        if (PSRenderStates.shader2DEnabled)
        {
            IvShaderInstanceMC.trySettingUpShader(shaderInstance, vertexShaderFile, fragmentShaderFile, utils);
        }
    }

    @Override
    public void dealloc()
    {
        shaderInstance.deleteShader();
    }

    @Override
    public void apply(float partialTicks, IvOpenGLTexturePingPong pingPong, IvDepthBuffer depthBuffer)
    {
        if (PSRenderStates.shader2DEnabled)
        {
            Minecraft mc = Minecraft.getMinecraft();
            int ticks = mc.getRenderViewEntity().ticksExisted;
            setShaderValues(partialTicks, ticks, depthBuffer);

            if (shaderInstance.shouldApply(ticks + partialTicks))
            {
                shaderInstance.apply(mc.displayWidth, mc.displayHeight, ticks + partialTicks, pingPong);
            }
        }
    }

    public abstract void setShaderValues(float partialTicks, int ticks, IvDepthBuffer depthBuffer);
}
