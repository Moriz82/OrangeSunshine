package moriz.orangesunshine.fluid.alcohol;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public record FlavorProfile (Charisma charisma, Potency potency, Texture texture) {
    public static final FlavorProfile DEFAULT = new FlavorProfile(Charisma.PLAIN, Potency.WATERY, Texture.SMOOTH);

    public Component getFlavour(int distillation, int fermentation, int maturation) {
        Charisma charisma = this.charisma.applyState(distillation, fermentation, maturation);
        Potency potency = this.potency.applyState(distillation, fermentation, maturation);
        Texture texture = this.texture.applyState(distillation, fermentation, maturation);
        Maturity maturity = Maturity.getMaturity(maturation);

        return Component.translatable("psychedelicract.alcohol.flavor",
                texture.getName(), charisma.getName(),
                potency.getName(), maturity.getName()
        ).withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.ITALIC);
    }
}
