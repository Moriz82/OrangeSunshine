package moriz.orangesunshine.chemistry;

import java.util.Map;
import java.util.function.Consumer;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.entity.drug.influence.DrugInfluence;
import moriz.orangesunshine.item.EdibleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * An EdibleItem that also displays a chemical compound formula tooltip,
 * combining the drug influence of EdibleItem with CompoundItem's tooltip.
 */
public class EdibleCompoundItem extends EdibleItem implements MatterStateItem {

    private final String compoundName;
    private String abbreviation = "";
    private final MatterState matterState;
    private final Map<String, Integer> components;
    private final int color;

    public EdibleCompoundItem(String pCompoundName, MatterState pMatterState, Map<String, Integer> pComponents, String pColor, DrugInfluence influence) {
        super(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, OrangeSunshine.id(pCompoundName)))
                .food(EdibleItem.NON_FILLING_EDIBLE),
              influence);
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

    public String buildAbbreviation() {
        StringBuilder builder = new StringBuilder();
        for (String name : components.keySet()) {
            builder.append(name);
            Integer count = components.get(name);
            if (count > 1) {
                builder.append(CompoundItem.getSubscript(Integer.toString(count)));
            }
        }
        return builder.toString();
    }
}
