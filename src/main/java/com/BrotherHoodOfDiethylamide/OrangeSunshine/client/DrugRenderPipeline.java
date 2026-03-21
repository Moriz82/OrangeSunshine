package com.BrotherHoodOfDiethylamide.OrangeSunshine.client;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugEffects;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.EffectWrapper;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.PSRenderStates;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.ShaderWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Connects the portedpsych rendering pipeline to Forge's render events.
 *
 * The original Psychedelicraft used ASM bytecode injection to hook into
 * EntityRenderer.renderWorldPass(). That hook was never ported, leaving the
 * entire shader/effect pipeline unreachable. This class replaces it using
 * Forge events:
 *
 *   RenderWorldLastEvent  → PSRenderStates.postRender()  (applies 2D post-process effects)
 *   ClientTickEvent       → DrugProperties.updateDrugEffects() + PSRenderStates.update()
 *   RenderGameOverlayEvent → direct GL overlay (guaranteed fallback when shaders fail)
 *
 * Shaders are lazily allocated on the first render event once a world is loaded.
 */
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = OrangeSunshine.MODID)
public class DrugRenderPipeline {

    private static boolean allocated = false;
    private static boolean allocationFailed = false;  // stop retrying after permanent failure
    static boolean allShadersWorking = false;          // package-private for DrugDebugOverlay

    // -------------------------------------------------------------------------
    // 2D post-process: fires after world is rendered, before HUD
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.player == null) return;

        if (allocationFailed) return;

        if (!allocated) {
            try {
                PSRenderStates.allocate();
                allocated = true;
                OrangeSunshine.logger.info("[DrugRenderPipeline] PSRenderStates allocated successfully");
                allShadersWorking = true;
                for (EffectWrapper ew : PSRenderStates.effectWrappers) {
                    if (ew instanceof ShaderWrapper) {
                        int id = ((ShaderWrapper) ew).shaderInstance.getShaderID();
                        OrangeSunshine.logger.info("[DrugRenderPipeline]   " + ew.getClass().getSimpleName()
                                + " shaderID=" + id + (id > 0 ? " OK" : " FAILED"));
                        if (id <= 0) allShadersWorking = false;
                    }
                }
                boolean pingPongOk = PSRenderStates.realtimePingPong != null
                        && PSRenderStates.realtimePingPong.setupRealtimeFB;
                OrangeSunshine.logger.info("[DrugRenderPipeline]   pingPong FBO: " + (pingPongOk ? "OK" : "FAILED"));
                if (!pingPongOk) allShadersWorking = false;
                OrangeSunshine.logger.info("[DrugRenderPipeline] allShadersWorking=" + allShadersWorking
                        + "; GL overlay fallback " + (allShadersWorking ? "inactive" : "ACTIVE"));
            } catch (Exception e) {
                OrangeSunshine.logger.error("[DrugRenderPipeline] Failed to allocate PSRenderStates (will not retry)", e);
                allocationFailed = true;
                allShadersWorking = false;
                return;
            }
        }

        try {
            float partialTicks = event.getPartialTicks();
            PSRenderStates.postRender(partialTicks, partialTicks);
        } catch (Exception e) {
            OrangeSunshine.logger.error("[DrugRenderPipeline] postRender threw", e);
        }
    }

    // -------------------------------------------------------------------------
    // Per-tick: update DrugProperties (hallucination manager, music, renderer)
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.player == null) return;

        EntityPlayer player = mc.player;
        DrugProperties dp = DrugProperties.getDrugProperties(player);
        if (dp != null) {
            try {
                dp.updateDrugEffects(player);
            } catch (Exception e) {
                OrangeSunshine.logger.error("[DrugRenderPipeline] updateDrugEffects threw", e);
            }
        }

        if (allocated) {
            PSRenderStates.update();
        }
    }

    // -------------------------------------------------------------------------
    // Direct GL overlay — guaranteed to show regardless of shader compilation.
    // This fires during HUD rendering (2D projection already set up by MC).
    // Maps SATURATION, HUE_AMPLITUDE, BLOOM_RADIUS → visible screen tint.
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.player == null) return;

        DrugEffects de = Drug.getDrugEffects();
        float saturation = de.SATURATION.getValue();   // >0 = vivid, <0 = gray
        float hue       = de.HUE_AMPLITUDE.getValue(); // color cycling speed
        float bloom     = de.BLOOM_RADIUS.getValue();  // brightness
        float bumpy     = de.BUMPY.getValue();         // wobble (unmappable to 2D tint, skip)

        float totalIntensity = Math.abs(saturation) + hue + bloom;
        if (totalIntensity < 0.05f) return;

        ScaledResolution sr = new ScaledResolution(mc);
        int sw = sr.getScaledWidth();
        int sh = sr.getScaledHeight();

        // Rainbow tint cycles with hue; white flash for bloom; gray for desaturation
        long ms = System.currentTimeMillis();
        float t = (ms % 4000L) / 4000.0f;  // 0..1 over 4 seconds

        float r, g, b, alpha;
        if (saturation < -0.1f) {
            // Desaturation: dark gray veil
            r = g = b = 0.15f;
            alpha = Math.min(0.65f, -saturation * 0.07f);
        } else {
            // Vivid/psychedelic: cycling rainbow
            r = 0.5f + 0.5f * (float) Math.sin(t * Math.PI * 2);
            g = 0.5f + 0.5f * (float) Math.sin(t * Math.PI * 2 + Math.PI * 2.0f / 3);
            b = 0.5f + 0.5f * (float) Math.sin(t * Math.PI * 2 + Math.PI * 4.0f / 3);
            alpha = Math.min(0.45f, (saturation * 0.035f + hue * 0.025f));
        }

        // Add bloom as extra white brightness
        if (bloom > 0.1f) {
            float bloomAlpha = Math.min(0.3f, bloom * 0.04f);
            drawColoredRect(sw, sh, 1.0f, 1.0f, 1.0f, bloomAlpha);
        }

        if (alpha > 0.005f) {
            drawColoredRect(sw, sh, r, g, b, alpha);
        }
    }

    private static void drawColoredRect(int w, int h, float r, float g, float b, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buf.pos(0, 0, 0).color(r, g, b, alpha).endVertex();
        buf.pos(0, h, 0).color(r, g, b, alpha).endVertex();
        buf.pos(w, h, 0).color(r, g, b, alpha).endVertex();
        buf.pos(w, 0, 0).color(r, g, b, alpha).endVertex();
        tess.draw();

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    /** Call this when the world unloads to free GPU resources. */
    public static void deallocate() {
        if (allocated) {
            PSRenderStates.deallocate();
            allocated = false;
        }
    }
}
