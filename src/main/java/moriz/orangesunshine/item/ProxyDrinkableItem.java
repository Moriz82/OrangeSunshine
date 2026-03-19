/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import moriz.orangesunshine.fluid.ConsumableFluid;
import moriz.orangesunshine.fluid.SimpleFluid;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Created by Sollace on Jan 1 2023
 */
public class ProxyDrinkableItem extends DrinkableItem {
    private static final int DEFAULT_USE_TIME = 32;
    private final Item basis;

    public ProxyDrinkableItem(Item basis, Item.Properties settings, int capacity, ConsumableFluid.ConsumptionType consumptionType) {
        super(settings.craftRemainder(basis), capacity, capacity, DEFAULT_USE_TIME, consumptionType);
        this.basis = basis;
    }

    @Override
    public Item asEmpty() {
        return basis;
    }

    @Override
    public Component getName(ItemStack stack) {
        SimpleFluid fluid = getFluid(stack);

        if (!fluid.isEmpty()) {
            return Component.empty()
                    .append(fluid.getName(stack))
                    .append(Component.literal(" "))
                    .append(basis.getName(stack));
        }

        return basis.getName(stack);
    }
}
