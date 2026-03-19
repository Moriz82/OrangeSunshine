/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import java.util.function.Consumer;

import moriz.orangesunshine.fluid.container.FluidContainer;
import moriz.orangesunshine.fluid.SimpleFluid;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

/**
 * Created by lukas on 25.10.14.
 * Updated by Sollace on 1 Jan 2023
 * Updated by Moriz starting 5/19/2024
 */
public class FlaskItem extends BlockItem implements FluidContainer {

    private final int capacity;

    public FlaskItem(Block block, Item.Properties settings, int capacity) {
        super(block, settings);
        this.capacity = capacity;
    }

    @Override
    public Component getName(ItemStack stack) {
        SimpleFluid fluid = getFluid(stack);

        if (!fluid.isEmpty()) {
            return Component.translatable(getDescriptionId() + ".filled", fluid.getName(stack));
        }

        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
        if (flag.isAdvanced()) {
            consumer.accept(Component.literal(getLevel(stack) + "/" + getMaxCapacity(stack)));
        }
    }

    @Override
    public int getMaxCapacity() {
        return capacity;
    }
}
