package moriz.orangesunshine.client.render;

import moriz.orangesunshine.client.PSClientConfig;
import moriz.orangesunshine.client.render.shader.PostEffectRenderer;
import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.ARGB;

import java.util.ArrayList;
import java.util.List;

public class DebugOverlay {
    public static boolean isEnabled = false;

    public static void render(GuiGraphics context, float tickDelta) {
        if (!isEnabled) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        DrugProperties properties = DrugProperties.of(mc.player);
        if (properties == null) {
            return;
        }

        List<String> lines = new ArrayList<>();
        lines.add("\u00a76\u00a7lOrange Sunshine Debug\u00a7r (F7 toggle)");
        lines.add("Ticks: " + mc.player.tickCount);

        lines.add("--- Active Drugs ---");
        boolean anyDrug = false;
        for (DrugType type : DrugType.REGISTRY) {
            float value = properties.getDrugValue(type);
            if (value > 0.001f) {
                lines.add(String.format(" %s: %.4f", type.id().getPath(), value));
                anyDrug = true;
            }
        }
        if (!anyDrug) {
            lines.add(" (none)");
        }

        lines.add("--- Modifiers ---");
        lines.add(String.format(" Wobble: %.3f", properties.getModifier(Drug.VIEW_WOBBLYNESS)));
        lines.add(String.format(" Tremble: %.3f", properties.getModifier(Drug.VIEW_TREMBLE_STRENGTH)));
        lines.add(String.format(" DoubleVision: %.3f", properties.getModifier(Drug.DOUBLE_VISION)));
        lines.add(String.format(" MotionBlur: %.3f", properties.getModifier(Drug.MOTION_BLUR)));
        lines.add(String.format(" ColorHallu: %.3f", properties.getModifier(Drug.COLOR_HALLUCINATION_STRENGTH)));
        lines.add(String.format(" Inversion: %.3f", properties.getModifier(Drug.INVERSION_HALLUCINATION_STRENGTH)));

        lines.add("--- Shader Pipeline ---");
        PostEffectRenderer post = DrugRenderer.INSTANCE.getPostEffects();
        lines.add(" Loaded chains: " + post.getShaderCount());
        lines.add(" 2D shaders: " + (PSClientConfig.getConfig().visual.shader2DEnabled ? "ON" : "OFF"));
        lines.add(" debug disable underwater: " + (PSClientConfig.getConfig().visual.debugDisableUnderwaterDistortion ? "ON" : "OFF"));
        lines.add(" debug disable simple_depth: " + (PSClientConfig.getConfig().visual.debugDisableSimpleEffectsDepth ? "ON" : "OFF"));
        lines.add(" Heat dist: " + String.format("%.3f", DrugRenderer.INSTANCE.getEnvironmentalEffects().getHeatDistortion()));
        lines.add(" Water dist: " + String.format("%.3f", DrugRenderer.INSTANCE.getEnvironmentalEffects().getWaterDistortion()));
        if (PSClientConfig.getConfig().visual.visualDebugLogging) {
            for (String shaderState : post.getDebugSnapshots()) {
                lines.add(" " + shaderState);
            }
        }

        int y = 10;
        for (String line : lines) {
            int w = mc.font.width(line);
            context.fill(5, y - 1, 12 + w, y + 9, ARGB.color(180, 0, 0, 0));
            context.drawString(mc.font, line, 10, y, 0xFFFFFFFF, false);
            y += 10;
        }
    }
}
