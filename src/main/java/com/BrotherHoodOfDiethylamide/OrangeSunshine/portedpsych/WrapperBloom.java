/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvDepthBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by lukas on 26.04.14.
 */
public class WrapperBloom extends ShaderWrapper<ShaderBloom>
{
    public WrapperBloom(String utils)
    {
        super(new ShaderBloom(OrangeSunshine.logger), getRL("shaderBasic.vert"), getRL("shaderBloom.frag"), utils);
    }

    @Override
    public void setShaderValues(float partialTicks, int ticks, IvDepthBuffer depthBuffer)
    {
        DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity());

        shaderInstance.bloom = 0.0f;
        if (drugProperties != null)
            shaderInstance.bloom = drugProperties.hallucinationManager.getBloom(drugProperties, partialTicks);
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
