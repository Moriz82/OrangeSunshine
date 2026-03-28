package moriz.orangesunshine.client.render.effect;

import moriz.orangesunshine.client.PSClientConfig;
import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

/**
 * Overlay-based drug visual effects that replace the broken PostChain shader pipeline.
 * Renders color shifts, pulsing, desaturation tints, and psychedelic overlays
 * directly as screen fills, bypassing the uniform-setting reflection issue.
 */
public class DrugVisualOverlay implements ScreenEffect {

    @Override
    public boolean shouldApply(float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null;
    }

    @Override
    public void update(float tickDelta) {
    }

    @Override
    public void render(GuiGraphics context, MultiBufferSource vertices, int screenWidth, int screenHeight, float ticks, PingPong pingPong) {
        // This overlay was a fallback for a previously broken post-shader pipeline.
        // When post shaders are enabled, avoid stacking fullscreen fills on top.
        if (PSClientConfig.getConfig().visual.shader2DEnabled) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        DrugProperties props = DrugProperties.of(mc.player);
        float tick = mc.player.tickCount + ticks;

        float lsd = props.getDrugValue(DrugType.LSD);
        float cannabis = props.getDrugValue(DrugType.CANNABIS);
        float peyote = props.getDrugValue(DrugType.PEYOTE);
        float shrooms = props.getDrugValue(DrugType.BROWN_SHROOMS) + props.getDrugValue(DrugType.RED_SHROOMS);
        float bathSalts = props.getDrugValue(DrugType.BATH_SALTS);
        float cocaine = props.getDrugValue(DrugType.COCAINE);
        float morphine = props.getDrugValue(DrugType.MORPHINE) + props.getDrugValue(DrugType.CODEINE) + props.getDrugValue(DrugType.OPIUM);

        renderPsychedelicPulse(context, screenWidth, screenHeight, tick, lsd, peyote, shrooms);
        renderCannabisHaze(context, screenWidth, screenHeight, tick, cannabis);
        renderStimulantEdge(context, screenWidth, screenHeight, tick, cocaine, bathSalts);
        renderOpioidVignette(context, screenWidth, screenHeight, morphine);
        renderColorShift(context, screenWidth, screenHeight, tick, lsd, peyote, shrooms);
    }

    private void renderPsychedelicPulse(GuiGraphics ctx, int w, int h, float tick, float lsd, float peyote, float shrooms) {
        float psychedelic = Math.max(lsd, Math.max(peyote, shrooms * 0.7f));
        if (psychedelic < 0.05f) return;

        float pulse = (Mth.sin(tick * 0.03f) + 1f) * 0.5f;
        float pulse2 = (Mth.sin(tick * 0.017f + 2.1f) + 1f) * 0.5f;

        float r = 0.15f + pulse * 0.3f;
        float g = 0.05f + pulse2 * 0.15f;
        float b = 0.25f + (1f - pulse) * 0.2f;
        float a = psychedelic * 0.18f * (0.7f + pulse * 0.3f);

        ctx.fill(RenderPipelines.GUI, 0, 0, w, h, ARGB.colorFromFloat(a, r, g, b));

        if (psychedelic > 0.4f) {
            float wave = (Mth.sin(tick * 0.05f) + 1f) * 0.5f;
            float a2 = (psychedelic - 0.4f) * 0.25f * wave;
            ctx.fill(RenderPipelines.GUI, 0, 0, w, h, ARGB.colorFromFloat(a2, 0.9f, 0.3f, 0.1f));
        }
    }

    private void renderCannabisHaze(GuiGraphics ctx, int w, int h, float tick, float cannabis) {
        if (cannabis < 0.05f) return;

        float haze = cannabis * 0.12f;
        float drift = Mth.sin(tick * 0.008f) * 0.02f;
        ctx.fill(RenderPipelines.GUI, 0, 0, w, h, ARGB.colorFromFloat(haze + drift, 0.4f, 0.55f, 0.2f));

        if (cannabis > 0.3f) {
            int bar = (int) (h * 0.08f * cannabis);
            float va = cannabis * 0.15f;
            ctx.fill(RenderPipelines.GUI, 0, 0, w, bar, ARGB.colorFromFloat(va, 0.1f, 0.15f, 0.05f));
            ctx.fill(RenderPipelines.GUI, 0, h - bar, w, h, ARGB.colorFromFloat(va, 0.1f, 0.15f, 0.05f));
        }
    }

    private void renderStimulantEdge(GuiGraphics ctx, int w, int h, float tick, float cocaine, float bathSalts) {
        float stim = Math.max(cocaine, bathSalts);
        if (stim < 0.1f) return;

        float flicker = (Mth.sin(tick * 0.4f) > 0.7f) ? 1f : 0f;
        float edge = stim * 0.08f * (1f + flicker * 0.5f);

        int b = Math.max(4, (int) (h * 0.03f * stim));
        ctx.fill(RenderPipelines.GUI, 0, 0, w, b, ARGB.colorFromFloat(edge, 1f, 1f, 1f));
        ctx.fill(RenderPipelines.GUI, 0, h - b, w, h, ARGB.colorFromFloat(edge, 1f, 1f, 1f));

        if (bathSalts > 0.3f) {
            float jitter = Mth.sin(tick * 2.3f) * bathSalts * 0.06f;
            ctx.fill(RenderPipelines.GUI, 0, 0, w, h, ARGB.colorFromFloat(Math.abs(jitter), 0.8f, 0.1f, 0.1f));
        }
    }

    private void renderOpioidVignette(GuiGraphics ctx, int w, int h, float opioid) {
        if (opioid < 0.05f) return;

        float strength = Mth.clamp(opioid * 0.35f, 0f, 0.6f);
        int bar = (int) (h * 0.15f * opioid);
        ctx.fill(RenderPipelines.GUI, 0, 0, w, bar, ARGB.colorFromFloat(strength, 0f, 0f, 0f));
        ctx.fill(RenderPipelines.GUI, 0, h - bar, w, h, ARGB.colorFromFloat(strength, 0f, 0f, 0f));
        ctx.fill(RenderPipelines.GUI, 0, 0, bar, h, ARGB.colorFromFloat(strength * 0.7f, 0f, 0f, 0f));
        ctx.fill(RenderPipelines.GUI, w - bar, 0, w, h, ARGB.colorFromFloat(strength * 0.7f, 0f, 0f, 0f));
    }

    private void renderColorShift(GuiGraphics ctx, int w, int h, float tick, float lsd, float peyote, float shrooms) {
        float colorStr = props_colorHallucinationStrength(lsd, peyote, shrooms);
        if (colorStr < 0.01f) return;

        float hueShift = tick * 0.02f;
        float r = (Mth.sin(hueShift) + 1f) * 0.5f;
        float g = (Mth.sin(hueShift + 2.094f) + 1f) * 0.5f;
        float b = (Mth.sin(hueShift + 4.189f) + 1f) * 0.5f;
        float a = colorStr * 0.12f;

        ctx.fill(RenderPipelines.GUI, 0, 0, w, h, ARGB.colorFromFloat(a, r, g, b));
    }

    private float props_colorHallucinationStrength(float lsd, float peyote, float shrooms) {
        return lsd * 0.2f + peyote * 0.3f + shrooms * 0.15f;
    }

    @Override
    public void close() throws Exception {
    }
}
