/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Created by lukas on 18.10.14.
 */
public class SpecialFoodItem extends Item {
    public int eatSpeed;

    public SpecialFoodItem(Item.Properties settings, int eatSpeed) {
        super(settings);
        this.eatSpeed = eatSpeed;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return eatSpeed;
    }
}
