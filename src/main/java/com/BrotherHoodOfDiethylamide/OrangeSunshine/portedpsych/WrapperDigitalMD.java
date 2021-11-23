/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvDepthBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * Created by lukas on 26.04.14.
 */
public class WrapperDigitalMD extends ShaderWrapper<ShaderDigital>
{
    public ResourceLocation digitalTextTexture;

    public WrapperDigitalMD(String utils)
    {
        super(new ShaderDigital(OrangeSunshine.logger), getRL("shaderBasic.vert"), getRL("shaderDigital.frag"), utils);

        digitalTextTexture = new ResourceLocation(OrangeSunshine.MODID, OrangeSunshine.filePathTextures + "digitalText.png");
    }

    @Override
    public void setShaderValues(float partialTicks, int ticks, IvDepthBuffer depthBuffer)
    {
        DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity());

        if (drugProperties != null)
        {
            shaderInstance.digital = drugProperties.getDrugValue("Zero");
            shaderInstance.maxDownscale = drugProperties.getDigitalEffectPixelResize();
            shaderInstance.digitalTextTexture = PSRenderStates.getTextureIndex(digitalTextTexture);
        }
        else
        {
            shaderInstance.digital = 0.0f;
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
