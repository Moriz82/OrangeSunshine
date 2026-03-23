package moriz.orangesunshine.client.render.effect;

import moriz.orangesunshine.client.OrangeSunshineClient;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.entity.drug.type.PowerDrug;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class PowerOverlayScreenEffect extends DrugOverlayScreenEffect<PowerDrug> {

    public PowerOverlayScreenEffect() {
        super(DrugType.POWER);
    }

    @Override
    protected void render(GuiGraphics context, MultiBufferSource vertices, int screenWidth, int screenHeight, float ticks, DrugProperties properties, PowerDrug drug) {
        float power = (float) drug.getActiveValue();
        if (power <= 0) {
            return;
        }
        float boost = OrangeSunshineClient.getConfig().visual.visualEffectIntensity;
        float alpha = Mth.clamp(power * 0.35f * boost, 0f, 0.75f);
        context.fill(RenderPipelines.GUI, 0, 0, screenWidth, screenHeight, ARGB.colorFromFloat(alpha, 0.4f, 0.05f, 0.9f));
    }
}
