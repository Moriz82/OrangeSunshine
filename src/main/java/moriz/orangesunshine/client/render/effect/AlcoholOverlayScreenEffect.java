package moriz.orangesunshine.client.render.effect;

import moriz.orangesunshine.client.OrangeSunshineClient;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.entity.drug.type.AlcoholDrug;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class AlcoholOverlayScreenEffect extends DrugOverlayScreenEffect<AlcoholDrug> {
    private static final Identifier NETHER_PORTAL = Identifier.fromNamespaceAndPath("minecraft", "block/nether_portal");

    public AlcoholOverlayScreenEffect() {
        super(DrugType.ALCOHOL);
    }

    @Override
    protected void render(GuiGraphics context, MultiBufferSource vertices, int screenWidth, int screenHeight, float ticks, DrugProperties properties, AlcoholDrug drug) {
        float alcohol = (float) drug.getActiveValue();

        if (alcohol > 0) {
            float boost = OrangeSunshineClient.getConfig().visual.visualEffectIntensity;
            renderAlcoholOverlay(context, alcohol * 0.5f * boost, screenWidth, screenHeight);
        }
    }

    private void renderAlcoholOverlay(GuiGraphics context, float alpha, int width, int height) {
        context.blitSprite(RenderPipelines.GUI, NETHER_PORTAL, 0, 0, width, height, ARGB.colorFromFloat(alpha, 1F, 1F, 1F));
    }
}
