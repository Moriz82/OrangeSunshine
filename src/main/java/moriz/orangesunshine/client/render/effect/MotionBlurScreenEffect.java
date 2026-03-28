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
 * Screen overlay for motion blur. Renders a semi-transparent directional smear
 * tinted by speed / direction when {@link Drug.AggregateModifier#MOTION_BLUR}
 * is above zero (alcohol at 0.5+, sleep deprivation at 0.6+, power drug, etc.).
 *
 * The heavy lifting (actual Gaussian blur) is done by the {@code ps_blur} post-effect
 * shader in ShaderLoader. This overlay is the lightweight HUD complement: it darkens
 * the edges and adds a subtle directional tint so the player perceives "blur" even
 * if post shaders are disabled.
 */
public class MotionBlurScreenEffect implements ScreenEffect {

    /** Smoothed motion blur strength (avoids harsh pop-in). */
    private float smoothedStrength;

    @Override
    public boolean shouldApply(float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        DrugProperties props = DrugProperties.of(mc.player);
        return props.getModifier(Drug.MOTION_BLUR) > 0.01f;
    }

    @Override
    public void update(float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            smoothedStrength = 0;
            return;
        }
        DrugProperties props = DrugProperties.of(mc.player);
        float target = Mth.clamp(props.getModifier(Drug.MOTION_BLUR), 0, 1);
        // Smoothly approach the target to avoid sudden flashes.
        smoothedStrength = Mth.lerp(0.15f, smoothedStrength, target);
    }

    @Override
    public void render(GuiGraphics context, MultiBufferSource vertices, int screenWidth, int screenHeight, float ticks, PingPong pingPong) {
        if (smoothedStrength < 0.01f) return;
        // When the post-shader pipeline is active, ps_blur already handles the real
        // blur.  Only add the overlay when shaders are off, or add a very subtle
        // vignette when shaders are on so the two layers do not fight.
        boolean shadersActive = PSClientConfig.getConfig().visual.shader2DEnabled;
        float boost = PSClientConfig.getConfig().visual.visualEffectIntensity;
        float strength = smoothedStrength * boost;

        if (!shadersActive || !PSClientConfig.getConfig().visual.doMotionBlur) {
            // Fallback: full overlay when shaders are off.
            // Directional darkening along the edges to simulate radial blur.
            float edgeAlpha = Mth.clamp(strength * 0.35f, 0, 0.6f);
            int barH = Math.max(4, (int) (screenHeight * 0.08f * strength));
            int barW = Math.max(4, (int) (screenWidth * 0.06f * strength));

            int darkColor = ARGB.colorFromFloat(edgeAlpha, 0.05f, 0.05f, 0.08f);
            // Top/bottom bands
            context.fill(RenderPipelines.GUI, 0, 0, screenWidth, barH, darkColor);
            context.fill(RenderPipelines.GUI, 0, screenHeight - barH, screenWidth, screenHeight, darkColor);
            // Left/right bands
            context.fill(RenderPipelines.GUI, 0, barH, barW, screenHeight - barH, darkColor);
            context.fill(RenderPipelines.GUI, screenWidth - barW, barH, screenWidth, screenHeight - barH, darkColor);
        }

        // Subtle full-screen tint that makes motion feel "smeared".
        float tintAlpha = Mth.clamp(strength * (shadersActive ? 0.06f : 0.12f), 0, 0.25f);
        if (tintAlpha > 0.005f) {
            context.fill(RenderPipelines.GUI, 0, 0, screenWidth, screenHeight,
                    ARGB.colorFromFloat(tintAlpha, 0.15f, 0.12f, 0.2f));
        }
    }

    @Override
    public void close() throws Exception {
    }
}
