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
public class WrapperColorBloom extends ShaderWrapper<ShaderColorBloom>
{
    public WrapperColorBloom(String utils)
    {
        super(new ShaderColorBloom(OrangeSunshine.logger), getRL("shaderBasic.vert"), getRL("shaderColoredBloom.frag"), utils);
    }

    @Override
    public void setShaderValues(float partialTicks, int ticks, IvDepthBuffer depthBuffer)
    {
        DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity());

        shaderInstance.coloredBloom = new float[]{1f, 1f, 1f, 0f};
        if (drugProperties != null)
            drugProperties.hallucinationManager.applyColorBloom(drugProperties, shaderInstance.coloredBloom, partialTicks);
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
