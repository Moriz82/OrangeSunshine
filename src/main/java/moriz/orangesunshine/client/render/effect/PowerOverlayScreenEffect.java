package moriz.orangesunshine.client.render.effect;

import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.entity.drug.type.PowerDrug;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;

public class PowerOverlayScreenEffect extends DrugOverlayScreenEffect<PowerDrug> {

    public PowerOverlayScreenEffect() {
        super(DrugType.POWER);
    }

    @Override
    protected void render(GuiGraphics context, MultiBufferSource vertices, int screenWidth, int screenHeight, float ticks, DrugProperties properties, PowerDrug drug) {
    }
}
