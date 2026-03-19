package moriz.orangesunshine.chemistry;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import moriz.orangesunshine.OrangeSunshine;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

@MethodsReturnNonnullByDefault
public class MixtureItem extends Item implements MatterStateItem {

    private final String compoundName;
    private String abbreviation = "";
    private final MatterState matterState;
    private final Map<String, Integer> components;
    private final int color;

    public MixtureItem(String pCompoundName, MatterState pMatterState, Map<String, Integer> pComponents, String pColor) {
        super(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, OrangeSunshine.id(pCompoundName))));
        this.compoundName = pCompoundName;
        this.matterState = pMatterState;
        this.components = pComponents;
        this.color = (int) Long.parseLong(pColor, 16);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, consumer, flag);
        consumer.accept(Component.literal(getAbbreviation()).withStyle(ChatFormatting.DARK_AQUA));
    }


    public String getChemicalName() {
        return this.compoundName;
    }

    public String getAbbreviation() {
        if (abbreviation.isEmpty()) {
            abbreviation = buildAbbreviation();
        }
        return abbreviation;
    }

    @Override
    public MatterState getMatterState() {
        return matterState;
    }

    public int getColor() {
        return this.color;
    }

    public int getColor(ItemStack pItemStack, int pTintIndex) {
        return pTintIndex > 0 ? -1 : color;
    }

    public static String getSubscript(String pString) {
        final int subscriptZeroCodepoint = 0x2080;
        StringBuilder builder = new StringBuilder();
        for (char character : pString.toCharArray()) {
            builder.append(Character.toChars(subscriptZeroCodepoint + Character.getNumericValue(character)));
        }
        return builder.toString();
    }

    public String buildAbbreviation() {
        StringBuilder builder = new StringBuilder();

        for (String name : components.keySet()) {
            builder.append(" + ").append(name);

            Integer count = components.get(name);
            if (count > 1) {
                builder.append(getSubscript(Integer.toString(count)));
            }
        }

        String str = builder.toString();
        str = str.substring(3);

        return str;
    }
}
