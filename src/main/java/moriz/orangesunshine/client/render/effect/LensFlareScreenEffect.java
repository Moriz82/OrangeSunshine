package moriz.orangesunshine.client.render.effect;

import moriz.orangesunshine.client.PSClientConfig;
import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

/**
 * Screen overlay for bloom / lens-flare hallucination.
 * Driven by {@link Drug.AggregateModifier#BLOOM_HALLUCINATION_STRENGTH}
 * (LSD, DMT, shrooms, cocaine, bath salts, warmth).
 *
 * The real bloom is done by the {@code ps_bloom} / {@code ps_colored_bloom} post-effect
 * shaders registered in ShaderLoader.  This overlay provides a complementary HUD-level
 * glow effect: pulsing bright washes that make the screen "breathe" with light.
 */
public class LensFlareScreenEffect implements ScreenEffect {

    /** Smoothed bloom strength. */
    private float smoothedStrength;

    @Override
    public boolean shouldApply(float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        DrugProperties props = DrugProperties.of(mc.player);
        return props.getModifier(Drug.BLOOM_HALLUCINATION_STRENGTH) > 0.01f;
    }

    @Override
    public void update(float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            smoothedStrength = 0;
            return;
        }
        DrugProperties props = DrugProperties.of(mc.player);
        float target = Mth.clamp(props.getModifier(Drug.BLOOM_HALLUCINATION_STRENGTH), 0, 1);
        smoothedStrength = Mth.lerp(0.12f, smoothedStrength, target);
    }

    @Override
    public void render(GuiGraphics context, MultiBufferSource vertices, int screenWidth, int screenHeight, float ticks, PingPong pingPong) {
        if (smoothedStrength < 0.01f) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        float tick = mc.player.tickCount + ticks;
        float boost = PSClientConfig.getConfig().visual.visualEffectIntensity;
        float strength = smoothedStrength * boost;

        // Pulsing bloom wash -- two overlapping sine waves for organic feel.
        float pulse1 = (Mth.sin(tick * 0.025f) + 1f) * 0.5f;
        float pulse2 = (Mth.sin(tick * 0.018f + 1.7f) + 1f) * 0.5f;

        // Warm white-gold bloom (bright areas glow).
        float bloomAlpha = Mth.clamp(strength * 0.18f * (0.6f + pulse1 * 0.4f), 0, 0.35f);
        if (bloomAlpha > 0.005f) {
            context.fill(RenderPipelines.GUI, 0, 0, screenWidth, screenHeight,
                    ARGB.colorFromFloat(bloomAlpha, 1.0f, 0.95f, 0.85f));
        }

        // Secondary colored flare that shifts hue slowly.
        if (strength > 0.25f) {
            float flareStrength = (strength - 0.25f) / 0.75f;
            float hue = tick * 0.008f;
            float r = (Mth.sin(hue) + 1f) * 0.5f;
            float g = (Mth.sin(hue + 2.094f) + 1f) * 0.5f;
            float b = (Mth.sin(hue + 4.189f) + 1f) * 0.5f;
            float flareAlpha = Mth.clamp(flareStrength * 0.12f * pulse2, 0, 0.2f);

            if (flareAlpha > 0.005f) {
                context.fill(RenderPipelines.GUI, 0, 0, screenWidth, screenHeight,
                        ARGB.colorFromFloat(flareAlpha, r, g, b));
            }
        }

        // Bright center spot to simulate lens flare hotspot.
        if (strength > 0.4f) {
            float spotStrength = (strength - 0.4f) / 0.6f;
            float spotAlpha = Mth.clamp(spotStrength * 0.1f * pulse1, 0, 0.15f);
            int cx = screenWidth / 2;
            int cy = screenHeight / 2;
            int spotW = (int) (screenWidth * 0.3f);
            int spotH = (int) (screenHeight * 0.3f);
            if (spotAlpha > 0.005f) {
                context.fill(RenderPipelines.GUI,
                        cx - spotW / 2, cy - spotH / 2,
                        cx + spotW / 2, cy + spotH / 2,
                        ARGB.colorFromFloat(spotAlpha, 1.0f, 1.0f, 0.9f));
            }
        }
    }

    @Override
    public void close() throws Exception {
    }
}
