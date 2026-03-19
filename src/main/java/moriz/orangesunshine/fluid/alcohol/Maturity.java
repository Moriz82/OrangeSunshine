package moriz.orangesunshine.fluid.alcohol;

import java.util.Locale;

import net.minecraft.network.chat.Component;

public enum Maturity {
    YOUNG,
    AGED,
    MATURE,
    VERY_MATURE;

    private static final Maturity[] STAGES = {
            YOUNG, AGED, MATURE, VERY_MATURE, VERY_MATURE
    };

    public static Maturity getMaturity(int maturation) {
        return STAGES[(maturation / (STAGES.length - 1)) % STAGES.length];
    }

    private final Component name = Component.translatable("orangesunshine.alcohol.maturity." + name().toLowerCase(Locale.ROOT));

    public Component getName() {
        return name;
    }
}
