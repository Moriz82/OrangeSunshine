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
public class WrapperWaterOverlay extends ShaderWrapper<ShaderDistortionMap>
{
    public ResourceLocation waterDropletsDistortionTexture;

    public WrapperWaterOverlay(String utils)
    {
        super(new ShaderDistortionMap(OrangeSunshine.logger), getRL("shaderBasic.vert"), getRL("shaderDistortionMap.frag"), utils);

        waterDropletsDistortionTexture = new ResourceLocation(OrangeSunshine.MODID, OrangeSunshine.filePathTextures + "waterDistortion.png");
    }

    @Override
    public void setShaderValues(float partialTicks, int ticks, IvDepthBuffer depthBuffer)
    {
        DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity());

        if (drugProperties != null && DrugProperties.waterOverlayEnabled)
        {
            float waterScreenDistortion = drugProperties.renderer.getCurrentWaterScreenDistortion();
            shaderInstance.strength = waterScreenDistortion * 0.2f;
            shaderInstance.alpha = waterScreenDistortion;
            shaderInstance.noiseTextureIndex0 = PSRenderStates.getTextureIndex(waterDropletsDistortionTexture);
            shaderInstance.noiseTextureIndex1 = PSRenderStates.getTextureIndex(waterDropletsDistortionTexture);
            shaderInstance.texTranslation0 = new float[]{0.0f, ticks * 0.005f};
            shaderInstance.texTranslation1 = new float[]{0.5f, ticks * 0.007f};
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
