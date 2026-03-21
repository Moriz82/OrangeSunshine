package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvOpenGLTexturePingPong;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvShaderInstance2D;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.opengl.GL11.glColor3f;

public class ShaderKaleidoscope extends IvShaderInstance2D
{
    public float kaleidoscopeStrength;

    public ShaderKaleidoscope(Logger logger)
    {
        super(logger);
    }

    @Override
    public boolean shouldApply(float ticks)
    {
        return kaleidoscopeStrength > 0.0f && super.shouldApply(ticks);
    }

    @Override
    public void apply(int screenWidth, int screenHeight, float ticks, IvOpenGLTexturePingPong pingPong)
    {
        useShader();

        setUniformInts("tex0", 0);
        setUniformFloats("totalAlpha", 1.0f);
        setUniformFloats("ticks", ticks);
        setUniformFloats("kaleidoscopeStrength", kaleidoscopeStrength);

        glColor3f(1.0f, 1.0f, 1.0f);
        drawFullScreen(screenWidth, screenHeight, pingPong);

        stopUsingShader();
    }
}
