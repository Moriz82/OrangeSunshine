package com.orangesunshine.moriz.rendering.shaders;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class GlobalUniforms {
    public static final Matrix4f modelView = new Matrix4f();
    public static final Matrix4f modelViewInverse = new Matrix4f();
    public static float timePassed = 0;
    public static float timePassedSin = 0;
    public static boolean lightMapEnabled = false;
    public static boolean lightEnabled = false;
    public static boolean overlayEnabled = false;
    public static int fogMode = 9729;

    static {
        modelView.identity();
        modelViewInverse.identity();
    }


    static class EventHandler {
        @SubscribeEvent
        public static void onRenderStart(TickEvent.RenderTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                timePassed = (event.renderTickTime + Minecraft.getInstance().gui.getGuiTicks())*0.05F;
                timePassedSin = Mth.sin(timePassed);
            }
        }

        @SubscribeEvent
        public static void onCameraPostSetup(SetCameraEvent event) {
            Matrix4f matrix4f = event.matrixStack.last().pose().copy();
            modelView.set(matrix4f);
            matrix4f.invert();
            modelViewInverse.set(matrix4f);
        }

        @SubscribeEvent
        public static void onOverlay(OverlayEvent event) {
            if (overlayEnabled != event.enabled) {
                overlayEnabled = event.enabled;

                if (!ShaderRenderer.isActive()) return;

                WorldShaderDefault uniform = ShaderRenderer.getWorldShader().safeGetUniform("overlayEnabled");
                uniform.setInt(overlayEnabled ? 1 : 0);
                uniform.upload();
            }
        }

        // TODO: Fix invisible mob's eyes not glowing
        @SubscribeEvent
        public static void onLightmapEnable(EnableLightMapEvent event) {
            if (lightMapEnabled != event.enabled) {
                lightMapEnabled = event.enabled;

                if (!ShaderRenderer.isActive() || !ShaderRenderer.lightmapEnable) return;

                WorldShaderDefault uniform = ShaderRenderer.getWorldShader().safeGetUniform("lightmapEnabled");
                uniform.setInt(lightMapEnabled ? 1 : 0);
                uniform.upload();
            }
        }

        @SubscribeEvent
        public static void onLightEnable(EnableLightEvent event) {
            if (lightEnabled != event.enabled) {
                lightEnabled = event.enabled;

                if (!ShaderRenderer.isActive()) return;

                WorldShaderDefault uniform = ShaderRenderer.getWorldShader().safeGetUniform("lightEnabled");
                uniform.setInt(lightEnabled ? 1 : 0);
                uniform.upload();
            }
        }

        @SubscribeEvent
        public static void onFogMode(FogModeEvent event) {
            fogMode = event.mode;
        }
    }
}
