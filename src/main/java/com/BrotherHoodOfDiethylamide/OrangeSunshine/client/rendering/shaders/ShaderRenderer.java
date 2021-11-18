package com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.post.PostShaders;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugEffects;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.events.hooks.BufferDrawEvent;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.events.hooks.RenderEvent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

@OnlyIn(Dist.CLIENT)
public class ShaderRenderer {
    public static boolean useShader = false;
    public static boolean isRenderingWorld = false;
    public static boolean lightmapEnable = true;
    private static final Deque<WorldShader> shaderStack = new ArrayDeque<>();
    private static WorldShader shaderWorld;
    private static WorldShader shaderOutlineBox;
    @Nullable
    private static WorldShader activeShader = null;

    public static void setup() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(ShaderRenderer::setup);
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        clear(true);

        MinecraftForge.EVENT_BUS.register(GlobalUniforms.EventHandler.class);
        MinecraftForge.EVENT_BUS.register(ShaderRenderer.EventHandler.class);

        try {
            PostShaders.setup();

            shaderWorld = new WorldShader(mc.getResourceManager(), "orangesunshine:world");
            shaderWorld.setSampler("texture", () -> 0);
            shaderWorld.setSampler("overlay", () -> 1);
            shaderWorld.setSampler("lightMap", () -> 2);

            shaderOutlineBox = new WorldShader(mc.getResourceManager(), "orangesunshine:world_outline");

            useShader = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void clear(boolean clearPostShaders) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> ShaderRenderer.clear(clearPostShaders));
            return;
        }

        MinecraftForge.EVENT_BUS.unregister(GlobalUniforms.EventHandler.class);
        MinecraftForge.EVENT_BUS.unregister(ShaderRenderer.EventHandler.class);

        useShader = false;
        isRenderingWorld = false;
        activeShader = null;
        shaderStack.clear();

        if (clearPostShaders) PostShaders.cleanup();

        if (shaderWorld != null) shaderWorld.close();
        shaderWorld = null;

        if (shaderOutlineBox != null) shaderOutlineBox.close();
        shaderOutlineBox = null;
    }

    public static void startRenderPass(@Nullable WorldShader shader) {
        if (activeShader == shader) return;
        if (activeShader != null) {
            RenderUtil.flushRenderBuffer();
            activeShader.clear();
        }
        activeShader = shader;
        if (shader == null) return;

        DrugEffects drugEffects = Drug.getDrugEffects();
        shader.clear();

        shader.safeGetUniform("modelViewMat").setMatrix(GlobalUniforms.modelView);
        shader.safeGetUniform("modelViewInverseMat").setMatrix(GlobalUniforms.modelViewInverse);
        shader.safeGetUniform("timePassed").setFloat(GlobalUniforms.timePassed);
        shader.safeGetUniform("fogMode").setInt(GlobalUniforms.fogMode);

        shader.safeGetUniform("smallWaves").setFloat(drugEffects.SMALL_WAVES.getClamped());
        shader.safeGetUniform("bigWaves").setFloat(drugEffects.BIG_WAVES.getClamped());
        shader.safeGetUniform("wiggleWaves").setFloat(drugEffects.WIGGLE_WAVES.getClamped());
        shader.safeGetUniform("distantWorldDeformation").setFloat(drugEffects.WORLD_DEFORMATION.getValue());

        shader.apply();
    }

    @SuppressWarnings("deprecation")
    public static void processPostShaders(float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Framebuffer framebuffer = mc.getMainRenderTarget();

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.disableAlphaTest();
        RenderSystem.enableTexture();
        RenderSystem.matrixMode(GL11.GL_TEXTURE);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        PostShaders.processShaders(partialTicks);
        RenderSystem.popMatrix();
        RenderSystem.enableTexture();

        framebuffer.bindWrite(false);
    }

    public static WorldShader getWorldShader() {
        return shaderWorld;
    }

    public static WorldShader getWorldOutlineShader() {
        return shaderOutlineBox;
    }

    public static void pushShader() {
        if (isActive()) shaderStack.addLast(activeShader);
    }

    public static void popShader() {
        if (shaderStack.isEmpty()) return;
        WorldShader shader = shaderStack.pollLast();
        startRenderPass(shader);
    }

    public static boolean isActive() {
        return activeShader != null;
    }

    @Nullable
    public static WorldShader getActiveShader() {
        return activeShader;
    }


    static class EventHandler {
        @SubscribeEvent
        public static void onTerrain(RenderEvent.RenderTerrainEvent event) {
            boolean flag = event.blockLayer == RenderType.translucent();
            if (event.phase == RenderEvent.Phase.START) {
                ShaderRenderer.lightmapEnable = !flag;
                ShaderRenderer.isRenderingWorld = true;
                ShaderRenderer.getWorldShader().safeGetUniform("lightmapEnabled").setInt(1);
                ShaderRenderer.startRenderPass(ShaderRenderer.getWorldShader());
            } else {
                ShaderRenderer.lightmapEnable = flag;
            }

            RenderUtil.checkGlErrors("Terrain");
        }

        @SubscribeEvent
        public static void onEntity(RenderEvent.RenderEntityEvent event) {
            if (event.phase == RenderEvent.Phase.START) {
                ShaderRenderer.startRenderPass(ShaderRenderer.getWorldShader());
            }

            RenderUtil.checkGlErrors("Entity");
        }

        @SubscribeEvent
        public static void onTileEntity(RenderEvent.RenderBlockEntityEvent event) {
            if (event.phase == RenderEvent.Phase.START) {
                ShaderRenderer.startRenderPass(ShaderRenderer.getWorldShader());
            }

            RenderUtil.checkGlErrors("Block entity");
        }

        @SubscribeEvent
        public static void onBlockOutline(RenderEvent.RenderBlockOutlineEvent event) {
            if (event.phase == RenderEvent.Phase.START) {
                ShaderRenderer.startRenderPass(ShaderRenderer.getWorldOutlineShader());
            } else {
                event.buffer.endBatch(RenderType.LINES);
            }

            RenderUtil.checkGlErrors("Block outline");
        }

        @SubscribeEvent
        public static void onParticle(RenderEvent.RenderParticlesEvent event) {
            if (event.phase == RenderEvent.Phase.START) {
                ShaderRenderer.startRenderPass(ShaderRenderer.getWorldShader());
            } else {
                ShaderRenderer.startRenderPass(null);
                ShaderRenderer.isRenderingWorld = false;
            }

            RenderUtil.checkGlErrors("Particles");
        }

        @SubscribeEvent
        public static void preDraw(BufferDrawEvent.Pre event) {
            if (!ShaderRenderer.isRenderingWorld) return;
            switch (event.name) {
                case "crumbling":
                case "armor_glint":
                case "armor_entity_glint":
                case "entity_glint":
                case "entity_glint_direct":
                    ShaderRenderer.pushShader();
                    ShaderRenderer.startRenderPass(ShaderRenderer.getWorldShader());
                    break;
                case "lightning":
                    ShaderRenderer.pushShader();
                    ShaderRenderer.startRenderPass(ShaderRenderer.getWorldOutlineShader());
                    break;
                case "end_portal":
                    ShaderRenderer.pushShader();
                    ShaderRenderer.startRenderPass(null);
                    break;
            }
        }

        @SubscribeEvent
        public static void postDraw(BufferDrawEvent.Post event) {
            if (!ShaderRenderer.isRenderingWorld) return;
            switch (event.name) {
                case "crumbling":
                    RenderUtil.checkGlErrors("Block damage");
                    ShaderRenderer.popShader();
                    break;
                case "armor_glint":
                case "armor_entity_glint":
                case "entity_glint":
                case "entity_glint_direct":
                    RenderUtil.checkGlErrors("Armor glint");
                    ShaderRenderer.popShader();
                    break;
                case "lightning":
                    RenderUtil.checkGlErrors("Lightning");
                    ShaderRenderer.popShader();
                    break;
                case "end_portal":
                    RenderUtil.checkGlErrors("End portal");
                    ShaderRenderer.popShader();
                    break;
            }
        }
    }
}
