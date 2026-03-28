/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import net.minecraft.core.component.DataComponents;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.influence.DrugInfluence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

public class EdibleItem extends Item {
    public static final FoodProperties NON_FILLING_EDIBLE = new FoodProperties.Builder().nutrition(0).saturationModifier(0.1F).alwaysEdible().build();
    public static final FoodProperties HAS_MUFFIN = new FoodProperties.Builder().nutrition(2).saturationModifier(0.1F).alwaysEdible().build();
    public static final FoodProperties TOMATO = new FoodProperties.Builder().nutrition(1).saturationModifier(1.9F).alwaysEdible().build();

    private final DrugInfluence influence;
    private final ItemUseAnimation useAnimation;

    public EdibleItem(Item.Properties settings, DrugInfluence influence) {
        this(settings, null, influence);
    }

    public EdibleItem(Item.Properties settings, ItemUseAnimation useAnimation, DrugInfluence influence) {
        super(settings);
        this.influence = influence;
        this.useAnimation = useAnimation;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return useAnimation != null ? useAnimation : super.getUseAnimation(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        ItemStack remainder = super.finishUsingItem(stack, level, user);

        if (stack.get(DataComponents.FOOD) == null && user.hasInfiniteMaterials() && !remainder.isEmpty()) {
            remainder.shrink(1);
        }

        DrugProperties.of(user).ifPresent(drugProperties -> {
            drugProperties.addToDrug(influence.clone());
        });
        return remainder;
    }
}
