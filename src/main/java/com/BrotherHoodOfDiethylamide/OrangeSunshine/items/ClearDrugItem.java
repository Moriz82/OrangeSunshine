package com.BrotherHoodOfDiethylamide.OrangeSunshine.items;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Supplier;

public class ClearDrugItem extends SyringeItem {

	public ClearDrugItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemStack, World world, LivingEntity entity) {
		ItemStack itemStack1 = super.finishUsingItem(itemStack, world, entity);

		if (entity instanceof PlayerEntity){
			Drug.clearDrugs((PlayerEntity) entity);
		}

		return itemStack1;
	}

}
