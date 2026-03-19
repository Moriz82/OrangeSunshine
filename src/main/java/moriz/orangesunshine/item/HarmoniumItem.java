/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public class HarmoniumItem extends Item {
    private static final int DEFAULT_COLOR = 0xFFFFFF;

    public HarmoniumItem(Item.Properties settings) {
        super(settings);
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
