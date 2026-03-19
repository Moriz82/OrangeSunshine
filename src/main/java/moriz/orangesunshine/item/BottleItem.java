/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import moriz.orangesunshine.fluid.ConsumableFluid;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

/**
 * Created by Sollace on Jan 1 2023
 */
public class BottleItem extends DrinkableItem {
    private static final int DEFAULT_COLOR = 0xFFFFFF;
    private static final int DEFAULT_USE_DURATION = 32;

    public BottleItem(Item.Properties settings, int capacity, int consumptionVolume, ConsumableFluid.ConsumptionType consumptionType) {
        super(settings, capacity, consumptionVolume, DEFAULT_USE_DURATION, consumptionType);
    }

    public int getColor(ItemStack stack) {
        return DyedItemColor.getOrDefault(stack, DEFAULT_COLOR);
    }

    public boolean hasCustomColor(ItemStack stack) {
        return stack.get(DataComponents.DYED_COLOR) != null;
    }

    public void clearColor(ItemStack stack) {
        stack.remove(DataComponents.DYED_COLOR);
    }

    public void setColor(ItemStack stack, int color) {
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color));
    }
}
