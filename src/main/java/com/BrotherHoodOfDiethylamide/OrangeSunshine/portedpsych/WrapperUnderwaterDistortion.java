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
public class WrapperUnderwaterDistortion extends ShaderWrapper<ShaderHeatDistortions>
{
    public ResourceLocation heatDistortionNoiseTexture;

    public WrapperUnderwaterDistortion(String utils)
    {
        super(new ShaderHeatDistortions(OrangeSunshine.logger), getRL("shaderBasic.vert"), getRL("shaderHeatDistortion.frag"), utils);

        heatDistortionNoiseTexture = new ResourceLocation(OrangeSunshine.MODID, OrangeSunshine.filePathTextures + "heatDistortionNoise.png");
    }

    @Override
    public void setShaderValues(float partialTicks, int ticks, IvDepthBuffer depthBuffer)
    {
        DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity());

        if (PSRenderStates.doWaterDistortion && drugProperties != null && depthBuffer != null)
        {
            float waterDistortion = drugProperties.renderer.getCurrentWaterDistortion();

            shaderInstance.depthTextureIndex = depthBuffer.getDepthTextureIndex();
            shaderInstance.noiseTextureIndex = PSRenderStates.getTextureIndex(heatDistortionNoiseTexture);

            shaderInstance.strength = waterDistortion;
            shaderInstance.wobbleSpeed = 0.03f;
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
        DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity());

        if (drugProperties != null)
        {
            float waterDistortion = PSRenderStates.doWaterDistortion ? drugProperties.renderer.getCurrentWaterDistortion() : 0.0f;

            return waterDistortion > 0.0f;
        }

        return false;
    }
}
