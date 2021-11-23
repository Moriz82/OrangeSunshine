/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvDepthBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * Created by lukas on 26.04.14.
 */
public class WrapperSimpleEffects extends ShaderWrapper<ShaderSimpleEffects>
{
    public WrapperSimpleEffects(String utils)
    {
        super(new ShaderSimpleEffects(OrangeSunshine.logger), getRL("shaderBasic.vert"), getRL("shaderSimpleEffects.frag"), utils);
    }

    @Override
    public void setShaderValues(float partialTicks, int ticks, IvDepthBuffer depthBuffer)
    {
        DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity());

        if (drugProperties != null)
        {
            shaderInstance.quickColorRotation = drugProperties.hallucinationManager.getQuickColorRotation(drugProperties, partialTicks);
            shaderInstance.slowColorRotation = drugProperties.hallucinationManager.getSlowColorRotation(drugProperties, partialTicks);
            shaderInstance.desaturation = drugProperties.hallucinationManager.getDesaturation(drugProperties, partialTicks);
            shaderInstance.colorIntensification = drugProperties.hallucinationManager.getColorIntensification(drugProperties, partialTicks);
        }
        else
        {
            shaderInstance.slowColorRotation = 0.0f;
            shaderInstance.quickColorRotation = 0.0f;
            shaderInstance.desaturation = 0.0f;
            shaderInstance.colorIntensification = 0.0f;
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
