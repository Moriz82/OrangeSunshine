package com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.mixin.client.InvokerRenderUtilsOF;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

@OnlyIn(Dist.CLIENT)
public class RenderUtil {
    public static final boolean hasOptifine;

    static {
        boolean flag;
        try {
            Class.forName("optifine.Utils", false, Thread.currentThread().getContextClassLoader());
            flag = true;
        } catch (ClassNotFoundException e) {
            flag = false;
        }
        hasOptifine = flag;
    }

    public static void flushRenderBuffer() {
        if (hasOptifine) {
            boolean old = InvokerRenderUtilsOF.callSetFlushRenderBuffers(true);
            InvokerRenderUtilsOF.callFlushRenderBuffers();
            InvokerRenderUtilsOF.callSetFlushRenderBuffers(old);
        } else {
            Minecraft mc = Minecraft.getInstance();
            flushRenderBuffer(mc.renderBuffers().bufferSource());
            flushRenderBuffer(mc.renderBuffers().crumblingBufferSource());
        }
    }

    private static void flushRenderBuffer(IRenderTypeBuffer.Impl buffer) {
        RenderTypeBufferExt bufferExt = (RenderTypeBufferExt) buffer;
        bufferExt.flushRenderBuffers();
    }

    private static long lastErrorTime = System.currentTimeMillis();
    public static void checkGlErrors(String location) {
        int i = GlStateManager._getError();
        if (i != GL11.GL_NO_ERROR) {
            String e = getGlErrorString(i);
            if ((System.currentTimeMillis() - lastErrorTime) > 10000L) {
                lastErrorTime = System.currentTimeMillis();
                OrangeSunshine.LOGGER.error("OpenGL error: {} ({}) at: {}", e, i, location);

                String chatMessage = I18n.get("OrangeSunshine.message.glError", e, i, location);
                Minecraft.getInstance().gui.getChat().addMessage(new StringTextComponent(chatMessage));
            }
        }
    }

    public static String getGlErrorString(int i) {
        switch (i) {
            case GL11.GL_NO_ERROR:
                return "No error";
            case GL11.GL_INVALID_ENUM:
                return "Invalid enum";
            case GL11.GL_INVALID_VALUE:
                return "Invalid value";
            case GL11.GL_INVALID_OPERATION:
                return "Invalid operation";
            case GL11.GL_STACK_OVERFLOW:
                return "Stack overflow";
            case GL11.GL_STACK_UNDERFLOW:
                return "Stack underflow";
            case GL11.GL_OUT_OF_MEMORY:
                return "Out of memory";
            case GL30.GL_INVALID_FRAMEBUFFER_OPERATION:
                return "Invalid framebuffer operation";
            default:
                return "Unknown";
        }
    }
}
