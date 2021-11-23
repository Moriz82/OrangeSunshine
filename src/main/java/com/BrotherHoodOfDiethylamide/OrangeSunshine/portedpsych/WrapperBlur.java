/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvDepthBuffer;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * Created by lukas on 26.04.14.
 */
public class WrapperBlur extends ShaderWrapper<ShaderBlur>
{
    private double guiBackgroundBlur;

    public WrapperBlur(String utils)
    {
        super(new ShaderBlur(OrangeSunshine.logger), getRL("shaderBasic.vert"), getRL("shaderBlur.frag"), utils);
    }

    @Override
    public void setShaderValues(float partialTicks, int ticks, IvDepthBuffer depthBuffer)
    {
        DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity());

        if (drugProperties != null)
        {
            shaderInstance.vBlur = drugProperties.getDrugValue("Power");
            shaderInstance.hBlur = 0.0f;
        }
        else
        {
            shaderInstance.vBlur = 0.0f;
            shaderInstance.hBlur = 0.0f;
        }

        shaderInstance.vBlur += 1.1f * guiBackgroundBlur * guiBackgroundBlur * guiBackgroundBlur;
        shaderInstance.hBlur += 1.1f * guiBackgroundBlur * guiBackgroundBlur * guiBackgroundBlur;
    }

    @Override
    public void update()
    {
        if (Minecraft.getMinecraft().isGamePaused())
            guiBackgroundBlur = Math.min(1, guiBackgroundBlur + 0.25f);
        else
            guiBackgroundBlur = Math.max(0, guiBackgroundBlur - 0.25f);
    }

    @Override
    public boolean wantsDepthBuffer(float partialTicks)
    {
        return false;
    }
}
