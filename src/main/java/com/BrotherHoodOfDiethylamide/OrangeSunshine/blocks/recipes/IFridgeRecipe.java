package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public interface IFridgeRecipe extends IRecipe<IInventory> {

	ResourceLocation TYPE_ID = new ResourceLocation(OrangeSunshine.MOD_ID, "fridge_crafting");

	@Override
	default NonNullList<Ingredient> getIngredients(){
		return NonNullList.create();
	}

	@Override
	default boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	default boolean isSpecial() {
		return true;
	}

	@Override
	default IRecipeType<?> getType(){
		return Registry.RECIPE_TYPE.getOptional(TYPE_ID).get();
	}
}
