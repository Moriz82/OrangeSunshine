/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by lukas on 26.04.14.
 */
public class WrapperMotionBlur extends ScreenEffectWrapper<EffectMotionBlur>
{
    public WrapperMotionBlur()
    {
        super(new EffectMotionBlur());
    }

    @Override
    public void setScreenEffectValues(float partialTicks, int ticks)
    {
        DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity());

        if (PSRenderStates.doMotionBlur && drugProperties != null)
            screenEffect.motionBlur = drugProperties.hallucinationManager.getMotionBlur(drugProperties, partialTicks);
        else
            screenEffect.motionBlur = 0.0f;
    }

    @Override
    public void update()
    {

    }
}
