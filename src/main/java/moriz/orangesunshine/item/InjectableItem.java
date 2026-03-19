/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import moriz.orangesunshine.fluid.ConsumableFluid;
import net.minecraft.world.item.Item;

public class InjectableItem extends DrinkableItem {
    public static final int FLUID_PER_INJECTION = 10;
    private static final int DEFAULT_USE_TIME = 32;

    public InjectableItem(Item.Properties settings, int capacity) {
        super(settings, capacity, FLUID_PER_INJECTION, DEFAULT_USE_TIME, ConsumableFluid.ConsumptionType.INJECT);
    }
}
