package moriz.orangesunshine.client.render.effect;

import moriz.orangesunshine.entity.drug.*;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;

public abstract class DrugOverlayScreenEffect<D extends Drug> implements ScreenEffect {

    private final DrugType type;

    public DrugOverlayScreenEffect(DrugType type) {
        this.type = type;
    }

    @Override
    public boolean shouldApply(float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && DrugProperties.of(mc.player).getDrug(type).getActiveValue() > 0;
    }

    @Override
    public void update(float tickDelta) {
    }

    @Override
    public final void render(GuiGraphics context, MultiBufferSource vertices, int screenWidth, int screenHeight, float ticks, PingPong pingPong) {
        Minecraft mc = Minecraft.getInstance();
        DrugProperties properties = DrugProperties.of(mc.player);
        D drug = (D)properties.getDrug(type);

        render(context, vertices, screenWidth, screenHeight, ticks, properties, drug);
    }

    protected abstract void render(GuiGraphics context, MultiBufferSource vertices, int screenWidth, int screenHeight, float ticks, DrugProperties properties, D drug);

    @Override
    public void close() throws Exception {
    }

}
