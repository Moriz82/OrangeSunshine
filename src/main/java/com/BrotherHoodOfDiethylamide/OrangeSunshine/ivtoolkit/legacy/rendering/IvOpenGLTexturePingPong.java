/*
 * Copyright 2014 Lukas Tenbrink
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

public class IvOpenGLTexturePingPong
{
    public Logger logger;

    public int[] cacheTextures = new int[2];
    public int activeBuffer;

    public int pingPongFB;
    public boolean setup = false;
    public boolean setupRealtimeFB = false;
    public boolean setupCacheTextureForTick = false;

    private int screenWidth;
    private int screenHeight;

    private int parentFrameBuffer;

    private boolean useFramebuffer;

    public IvOpenGLTexturePingPong(Logger logger)
    {
        this.logger = logger;
    }

    public void initialize(boolean useFramebuffer)
    {
        destroy();
        this.useFramebuffer = useFramebuffer;

        boolean fboFailed = false;
        for (int i = 0; i < 2; i++)
        {
            cacheTextures[i] = IvOpenGLHelper.genStandardTexture();
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, screenWidth, screenHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        }

        if (cacheTextures[0] <= 0 || cacheTextures[1] <= 0)
        {
            fboFailed = true;
            setup = false;
        }
        else if (OpenGlHelper.framebufferSupported && useFramebuffer)
        {
            pingPongFB = OpenGlHelper.glGenFramebuffers();

            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, pingPongFB);
            OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, cacheTextures[0], 0);
            OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0 + 1, GL11.GL_TEXTURE_2D, cacheTextures[1], 0);

            int status = OpenGlHelper.glCheckFramebufferStatus(OpenGlHelper.GL_FRAMEBUFFER);
            if (status != OpenGlHelper.GL_FRAMEBUFFER_COMPLETE)
            {
                logger.error("PingPong FBO failed setting up! (" + IvDepthBuffer.getFramebufferStatusString(status) + ")");

                fboFailed = true;
            }

            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, parentFrameBuffer);

            setup = true;
        }
        else
        {
            fboFailed = true;
            setup = true;
        }

        if (!fboFailed)
        {
            setupRealtimeFB = true;
        }
        else
        {
            logger.error("Can not PingPong! Using screen pong workaround");
        }
    }

    public void setScreenSize(int screenWidth, int screenHeight)
    {
        boolean gen = screenWidth != this.screenWidth || screenHeight != this.screenHeight;

        if (gen)
        {
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;

            if (setup)
            {
                initialize(useFramebuffer);
            }
        }
    }

    public int getScreenWidth()
    {
        return screenWidth;
    }

    public int getScreenHeight()
    {
        return screenHeight;
    }

    public void setParentFrameBuffer(int parentFrameBuffer)
    {
        this.parentFrameBuffer = parentFrameBuffer > 0 ? parentFrameBuffer : 0;
    }

    public int getParentFrameBuffer()
    {
        return this.parentFrameBuffer;
    }

    public void preTick(int screenWidth, int screenHeight)
    {
        setupCacheTextureForTick = false;

        setScreenSize(screenWidth, screenHeight);
    }

    public void pingPong()
    {
        if (setupRealtimeFB)
        {
            if (!setupCacheTextureForTick)
            {
                activeBuffer = 0;
                bindCurrentTexture();

                GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 0, 0, screenWidth, screenHeight, 0);

                OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, pingPongFB);
//                glPushAttrib(GL_VIEWPORT_BIT | GL_COLOR_BUFFER_BIT);
                GlStateManager.pushAttrib();

                setupCacheTextureForTick = true;
            }
            else
            {
                activeBuffer = 1 - activeBuffer;
                bindCurrentTexture();
            }

            GL11.glDrawBuffer(activeBuffer == 1 ? OpenGlHelper.GL_COLOR_ATTACHMENT0 : OpenGlHelper.GL_COLOR_ATTACHMENT0 + 1);
//            glReadBuffer(activeBuffer == 0 ? GL_COLOR_ATTACHMENT0_EXT : GL_COLOR_ATTACHMENT1_EXT);

            GlStateManager.viewport(0, 0, screenWidth, screenHeight);
//            glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
//            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//            IvOpenGLHelper.setUpOpenGLStandard2D(screenWidth, screenHeight);
        }
        else // Use direct draw workaround
        {
            GlStateManager.bindTexture(cacheTextures[0]);
            GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 0, 0, screenWidth, screenHeight, 0);
        }
    }

    public void bindCurrentTexture()
    {
        GlStateManager.bindTexture(cacheTextures[activeBuffer]);
    }

    public void postTick()
    {
        if (setupRealtimeFB && setupCacheTextureForTick)
        {
//            glDrawBuffer(GL_BACK);
//            glReadBuffer(GL_BACK);
            GlStateManager.popAttrib();
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, parentFrameBuffer);

            activeBuffer = 1 - activeBuffer;

            GlStateManager.color(1.0f, 1.0f, 1.0f);
            bindCurrentTexture();
            IvRenderHelper.drawRectFullScreen(screenWidth, screenHeight);
        }
    }

    public void destroy()
    {
        for (int i = 0; i < 2; i++)
        {
            if (cacheTextures[i] > 0)
            {
                GlStateManager.deleteTexture(cacheTextures[i]);
                cacheTextures[i] = 0;
            }
        }

        if (pingPongFB > 0)
        {
            OpenGlHelper.glDeleteFramebuffers(pingPongFB);
            pingPongFB = 0;
        }

        setupRealtimeFB = false;
        setup = false;
    }
}
