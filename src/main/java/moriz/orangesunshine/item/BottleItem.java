/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import moriz.orangesunshine.fluid.*;
import moriz.orangesunshine.fluid.ConsumableFluid;
import net.minecraft.item.*;

/**
 * Created by Sollace on Jan 1 2023
 */
public class BottleItem extends DrinkableItem implements DyeableItem {
    public BottleItem(Settings settings, int capacity, int consumptionVolume, ConsumableFluid.ConsumptionType consumptionType) {
        super(settings, capacity, consumptionVolume, Item.DEFAULT_MAX_USE_TIME, consumptionType);
    }
}
