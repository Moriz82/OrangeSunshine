package moriz.orangesunshine.item;

import java.util.Optional;
import java.util.function.Consumer;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.entity.drug.DrugProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class SuspiciousItem extends Item {

    private final ItemLike hallucinatedFormSupplier;

    public static ItemLike createForms(ItemLike... items) {
        RandomSource rng = RandomSource.create();
        return () -> {
            return items[rng.nextInt(items.length)].asItem();
        };
    }

    @Nullable
    private Item chosenItem;

    public SuspiciousItem(Item.Properties settings, ItemLike hallucinatedFormSupplier) {
        super(settings);
        this.hallucinatedFormSupplier = hallucinatedFormSupplier;
    }

    public Optional<Item> getHallucinatedItem() {
        if (OrangeSunshine.getGlobalDrugProperties().filter(DrugProperties::isTripping).isPresent()) {
            if (chosenItem == null) {
                chosenItem = hallucinatedFormSupplier.asItem();
            }
            return Optional.of(chosenItem);
        }
        chosenItem = null;
        return Optional.empty();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
        getHallucinatedItem().ifPresent(item ->
                item.getDefaultInstance().addDetailsToTooltip(context, display, null, flag, consumer));
    }

    @Override
    public Component getName(ItemStack stack) {
        return getHallucinatedItem().map(i -> i.getName(i.getDefaultInstance())).orElseGet(() -> super.getName(stack));
    }
}
