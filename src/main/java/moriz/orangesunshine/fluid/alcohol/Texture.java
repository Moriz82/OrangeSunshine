package moriz.orangesunshine.fluid.alcohol;

import java.util.Locale;
import java.util.Map;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public enum Texture {
    THIN,
    SMOOTH,
    FIZZY,
    CHUNKY,
    THICK;

    private static final Map<Texture, Texture[]> MATURATION_PHASES = Map.of(
        THIN, new Texture[] { THIN, SMOOTH, CHUNKY, THICK },
        SMOOTH, new Texture[] { SMOOTH, CHUNKY, THICK },
        FIZZY, new Texture[] { FIZZY, CHUNKY, THICK },
        CHUNKY, new Texture[] { CHUNKY, CHUNKY, CHUNKY },
        THICK, new Texture[] { THICK, CHUNKY }
    );

    public Texture applyState(int distillation, int fermentation, int maturation) {
        Texture[] phases = Texture.MATURATION_PHASES.get(this);
        int combinedProgress = Mth.clamp((int)(((fermentation + maturation) / 32F) * phases.length), 0, phases.length - 1);
        return phases[combinedProgress];
    }

    private final Component name = Component.translatable("orangesunshine.alcohol.texture." + name().toLowerCase(Locale.ROOT));

    public Component getName() {
        return name;
    }
}
