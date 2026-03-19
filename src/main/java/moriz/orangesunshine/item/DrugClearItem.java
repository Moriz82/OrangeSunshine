/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

/**
 * Created by Sollace on Jan 1 2023
 */
public class DrugClearItem extends Item {

    ItemUseAnimation action = ItemUseAnimation.NONE;
    int useTime = 0;
    public DrugClearItem(Item.Properties settings, ItemUseAnimation action, int useTime) {
        super(settings.stacksTo(1));
        this.action = action;
        this.useTime = useTime;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return action;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return stack;
        }
        DrugProperties properties = DrugProperties.of(player);
        for (Drug drug:properties.getAllDrugs()) {
            properties.setDrugValue(drug.getType(), 0);
        }
        return PSItems.SYRINGE.getDefaultInstance();
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return useTime;
    }
}
