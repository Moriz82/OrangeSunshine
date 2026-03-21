package moriz.orangesunshine.client.render.effect;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;

public class CompoundScreenEffect implements ScreenEffect {

    private final List<ScreenEffect> effects = new ArrayList<>();

    public static ScreenEffect of(ScreenEffect... effects) {
        return new CompoundScreenEffect().add(effects);
    }

    private CompoundScreenEffect() { }

    public CompoundScreenEffect add(ScreenEffect... effects) {
        for (ScreenEffect effect : effects) {
            add(effect);
        }
        return this;
    }

    public CompoundScreenEffect add(ScreenEffect effect) {
        if (effect instanceof CompoundScreenEffect comp) {
            effects.addAll(comp.effects);
        } else {
            effects.add(effect);
        }
        return this;
    }

    @Override
    public void update(float tickDelta) {
        effects.forEach(effect -> effect.update(tickDelta));
    }

    @Override
    public void render(GuiGraphics context, MultiBufferSource vertices, int screenWidth, int screenHeight, float ticks, PingPong pingPong) {
        for (ScreenEffect effect : effects) {
            if (effect.shouldApply(ticks)) {
                effect.render(context, vertices, screenWidth, screenHeight, ticks, pingPong);
            }
        }
    }

    @Override
    public void close() throws Exception {
        for (ScreenEffect effect : effects) {
            effect.close();
        }
    }

}
