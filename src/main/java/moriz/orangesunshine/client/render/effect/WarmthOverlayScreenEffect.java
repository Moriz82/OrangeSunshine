package moriz.orangesunshine.client.render.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.client.OrangeSunshineClient;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.entity.drug.type.WarmthDrug;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class WarmthOverlayScreenEffect extends DrugOverlayScreenEffect<WarmthDrug> {
    private static final Identifier COFFEE_OVERLAY = OrangeSunshine.id("textures/drug/coffee/overlay.png");

    public WarmthOverlayScreenEffect() {
        super(DrugType.WARMTH);
    }

    @Override
    protected void render(GuiGraphics context, MultiBufferSource vertices, int screenWidth, int screenHeight, float ticks, DrugProperties properties, WarmthDrug drug) {
        float warmth = (float) drug.getActiveValue();
        if (warmth > 0) {
            float boost = OrangeSunshineClient.getConfig().visual.visualEffectIntensity;
            renderWarmthOverlay(context, warmth * 0.5f * boost, screenWidth, screenHeight);
        }
    }

    private void renderWarmthOverlay(GuiGraphics context, float alpha, int width, int height) {
        // Simple color fill for now if blit doesn't support alpha directly
        context.fill(RenderPipelines.GUI, 0, 0, width, height, ARGB.colorFromFloat(alpha, 1, 0.5f, 0));
    }
}
