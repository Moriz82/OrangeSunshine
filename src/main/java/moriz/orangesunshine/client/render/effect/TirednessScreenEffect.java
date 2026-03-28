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
 * Screen overlay for drowsiness / tiredness.
 * Driven by {@link Drug.AggregateModifier#DROWSYNESS}
 * (sleep deprivation is the primary source, kava also contributes).
 *
 * The depth-of-field blur for distant objects is handled by the {@code depth_of_field}
 * post-effect shader.  This overlay adds the classic "closing eyelids" dark vignette
 * that darkens the screen edges and pulses slowly, simulating heavy eyelids.
 */
public class TirednessScreenEffect implements ScreenEffect {

    /** Smoothed drowsiness strength. */
    private float smoothedStrength;

    @Override
    public boolean shouldApply(float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        DrugProperties props = DrugProperties.of(mc.player);
        return props.getModifier(Drug.DROWSYNESS) > 0.01f;
    }

    @Override
    public void update(float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            smoothedStrength = 0;
            return;
        }
        DrugProperties props = DrugProperties.of(mc.player);
        float target = Mth.clamp(props.getModifier(Drug.DROWSYNESS), 0, 1);
        // Drowsiness fades in slowly (heavy eyelids effect).
        smoothedStrength = Mth.lerp(0.08f, smoothedStrength, target);
    }

    @Override
    public void render(GuiGraphics context, MultiBufferSource vertices, int screenWidth, int screenHeight, float ticks, PingPong pingPong) {
        if (smoothedStrength < 0.01f) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        float tick = mc.player.tickCount + ticks;
        float boost = PSClientConfig.getConfig().visual.visualEffectIntensity;
        float strength = smoothedStrength * boost;

        // Slow breathing pulse -- eyelids droop and recover.
        float breathCycle = Mth.sin(tick * 0.015f);
        float breathPulse = (breathCycle + 1f) * 0.5f; // 0..1

        // Dark vignette from all four edges (closing eyelids).
        float vignetteAlpha = Mth.clamp(strength * 0.45f * (0.7f + breathPulse * 0.3f), 0, 0.75f);
        int barTop = (int) (screenHeight * 0.12f * strength * (0.8f + breathPulse * 0.2f));
        int barBot = (int) (screenHeight * 0.10f * strength * (0.8f + breathPulse * 0.2f));
        int barSide = (int) (screenWidth * 0.06f * strength);

        int darkColor = ARGB.colorFromFloat(vignetteAlpha, 0.02f, 0.02f, 0.04f);

        // Top band (heavier -- eyelid closing)
        context.fill(RenderPipelines.GUI, 0, 0, screenWidth, barTop, darkColor);
        // Bottom band
        context.fill(RenderPipelines.GUI, 0, screenHeight - barBot, screenWidth, screenHeight, darkColor);
        // Left/right bands
        context.fill(RenderPipelines.GUI, 0, barTop, barSide, screenHeight - barBot, darkColor);
        context.fill(RenderPipelines.GUI, screenWidth - barSide, barTop, screenWidth, screenHeight - barBot, darkColor);

        // At high drowsiness, add periodic "blink" -- a fast dark flash.
        if (strength > 0.5f) {
            // Blink every ~4 seconds, lasting ~0.3 seconds.
            float blinkCycle = tick * 0.05f;
            float blinkPhase = blinkCycle - (float) Math.floor(blinkCycle);
            if (blinkPhase < 0.08f) {
                float blinkProgress = blinkPhase / 0.08f; // 0..1 during blink
                // Parabolic: peaks in the middle of the blink.
                float blinkIntensity = 4f * blinkProgress * (1f - blinkProgress);
                float blinkAlpha = Mth.clamp((strength - 0.5f) * 2f * blinkIntensity * 0.6f, 0, 0.85f);
                context.fill(RenderPipelines.GUI, 0, 0, screenWidth, screenHeight,
                        ARGB.colorFromFloat(blinkAlpha, 0.01f, 0.01f, 0.02f));
            }
        }

        // Subtle warm desaturated tint across the whole screen.
        float tintAlpha = Mth.clamp(strength * 0.08f, 0, 0.15f);
        if (tintAlpha > 0.005f) {
            context.fill(RenderPipelines.GUI, 0, 0, screenWidth, screenHeight,
                    ARGB.colorFromFloat(tintAlpha, 0.15f, 0.12f, 0.08f));
        }
    }

    @Override
    public void close() throws Exception {
    }
}
