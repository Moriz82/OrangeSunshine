package moriz.orangesunshine.fluid.alcohol;

import java.util.Locale;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public enum Potency {
    WATERY,
    WEAK,
    STRONG,
    AROMATIC,
    PUNGENT;

    private static final Potency[] VALUES = values();

    public Potency applyState(int distillation, int fermentation, int maturation) {
        int potency = (int)Mth.clamp(
            (distillation / 16F) - ((fermentation / 16F) / 3F) + ((maturation / 16F) / 2F) + ordinal(), 0, VALUES.length - 1
        ) % VALUES.length;
        return VALUES[potency];
    }

    private final Component name = Component.translatable("orangesunshine.alcohol.potency." + name().toLowerCase(Locale.ROOT));

    public Component getName() {
        return name;
    }
}
