/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.Iv2DScreenEffect;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvDepthBuffer;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvOpenGLTexturePingPong;
import net.minecraft.client.Minecraft;

/**
 * Created by lukas on 26.04.14.
 */
public abstract class ScreenEffectWrapper<ScreenEffect extends Iv2DScreenEffect> implements EffectWrapper
{
    public ScreenEffect screenEffect;

    protected ScreenEffectWrapper(ScreenEffect screenEffect)
    {
        this.screenEffect = screenEffect;
    }

    @Override
    public void alloc()
    {

    }

    @Override
    public void dealloc()
    {

    }

    @Override
    public void apply(float partialTicks, IvOpenGLTexturePingPong pingPong, IvDepthBuffer depthBuffer)
    {
        Minecraft mc = Minecraft.getMinecraft();
        int ticks = mc.ingameGUI.getUpdateCounter();

        setScreenEffectValues(partialTicks, ticks);

        if (screenEffect.shouldApply(ticks + partialTicks))
        {
            screenEffect.apply(mc.displayWidth, mc.displayHeight, ticks + partialTicks, pingPong);
        }
    }

    public abstract void setScreenEffectValues(float partialTicks, int ticks);

    @Override
    public boolean wantsDepthBuffer(float partialTicks)
    {
        return false;
    }
}
