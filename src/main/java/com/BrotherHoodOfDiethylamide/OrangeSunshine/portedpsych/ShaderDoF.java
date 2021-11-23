/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvDepthBuffer;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvOpenGLTexturePingPong;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvShaderInstance2D;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.math.IvMathHelper;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.Logger;

/**
 * Created by lukas on 18.02.14.
 */
public class ShaderDoF extends IvShaderInstance2D
{
    public int depthTextureIndex;

    public float zNear;
    public float zFar;

    public float focalPointNear;
    public float focalBlurNear;

    public float focalPointFar;
    public float focalBlurFar;

    public ShaderDoF(Logger logger)
    {
        super(logger);
    }

    @Override
    public boolean shouldApply(float ticks)
    {
        return depthTextureIndex > 0 && (focalBlurNear > 0.0f || focalBlurFar > 0.0f) && super.shouldApply(ticks);
    }

    @Override
    public void apply(int screenWidth, int screenHeight, float ticks, IvOpenGLTexturePingPong pingPong)
    {
        useShader();

        IvDepthBuffer.bindTextureForSource(OpenGlHelper.lightmapTexUnit + 1, depthTextureIndex);
        setUniformInts("depthTex", 2);

        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        setUniformInts("tex", 0);

        setUniformFloats("pixelSize", 1.0f / screenWidth, 1.0f / screenHeight);

        setUniformFloats("focalPointNear", focalPointNear);
        setUniformFloats("focalPointFar", focalPointFar);

        float maxDof = Math.max(focalBlurFar, focalBlurNear);

        for (int n = 0; n < MathHelper.ceil(maxDof); n++)
        {
            float curBlurNear = IvMathHelper.clamp(0.0f, focalBlurNear - n, 1.0f);
            float curBlurFar = IvMathHelper.clamp(0.0f, focalBlurFar - n, 1.0f);

            if (curBlurNear > 0.0f || curBlurFar > 0.0f)
            {
                setUniformFloats("focalBlurNear", curBlurNear);
                setUniformFloats("focalBlurFar", curBlurFar);

                for (int i = 0; i < 2; i++)
                {
                    setUniformInts("vertical", i);
                    drawFullScreen(screenWidth, screenHeight, pingPong);
                }
            }
        }

        setUniformFloats("depthRange", zNear, zFar);

        stopUsingShader();
    }
}
