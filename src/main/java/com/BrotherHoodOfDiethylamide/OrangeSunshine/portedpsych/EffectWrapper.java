/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;


import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvDepthBuffer;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvOpenGLTexturePingPong;

/**
 * Created by lukas on 26.04.14.
 */
public interface EffectWrapper
{
    public void alloc();

    public void dealloc();

    public void update();

    public void apply(float partialTicks, IvOpenGLTexturePingPong pingPong, IvDepthBuffer depthBuffer);

    public boolean wantsDepthBuffer(float partialTicks);
}
