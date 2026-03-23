package moriz.orangesunshine.client.render;

import java.util.function.Function;
import java.util.stream.IntStream;

import moriz.orangesunshine.OrangeSunshine;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.Mth;

public class ZeroScreen {
    public static final Identifier[] TEXTURES = IntStream.range(0, 8)
            .mapToObj(i -> OrangeSunshine.id("textures/entity/reality_rift/zero_screen_" + i + ".png"))
            .toArray(Identifier[]::new);
    public static final float X_PIXELS = 140 / 2F;
    public static final float Y_PIXELS = 224 / 2F;

    private static final Function<Identifier, RenderType> PS_ZERO_SCREEN = Util.memoize(id -> RenderTypes.entityTranslucent(id));

    public static void render(float ticks, Renderable action) {
        int seed = Mth.floor(ticks * 0.5F);
        var rng = RenderUtil.random(seed);
        action.render(
                PS_ZERO_SCREEN.apply(TEXTURES[seed % TEXTURES.length]),
                rng.nextInt(10) * 0.1F * ZeroScreen.X_PIXELS,
                rng.nextInt(8) * 0.125f * ZeroScreen.Y_PIXELS
        );
    }

    @FunctionalInterface
    public interface Renderable {
        void render(RenderType layer, float u, float v);
    }
}
