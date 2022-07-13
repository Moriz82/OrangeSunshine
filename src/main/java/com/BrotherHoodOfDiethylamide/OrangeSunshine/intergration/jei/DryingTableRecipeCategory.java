package com.BrotherHoodOfDiethylamide.OrangeSunshine.intergration.jei;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ModBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.CompoundCompressorRecipe;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.DryingTableRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class DryingTableRecipeCategory implements IRecipeCategory<DryingTableRecipe> {

	public final static ResourceLocation UID = new ResourceLocation(OrangeSunshine.MOD_ID, "drying_table_crafting");
	public final static ResourceLocation TEXTURE = new ResourceLocation(OrangeSunshine.MOD_ID, "textures/gui/drying_table_gui.png");

	private final IDrawable background;
	private final IDrawable icon;
	//private final IDrawableStatic sun;

	public DryingTableRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
		this.icon = helper.createDrawableIngredient(new ItemStack(ModBlocks.DRYING_TABLE.get()));
		//this.sun = helper.createDrawable(TEXTURE, 176, 0, 13, 17);
	}

	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@Override
	public Class<? extends DryingTableRecipe> getRecipeClass() {
		return DryingTableRecipe.class;
	}

	@Override
	public String getTitle() {
		return ModBlocks.DRYING_TABLE.get().getName().getString();
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setIngredients(DryingTableRecipe recipe, IIngredients ingredients) {
		ingredients.setInputIngredients(recipe.getIngredients());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, DryingTableRecipe recipe, IIngredients ingredients) {
		recipeLayout.getItemStacks().init(0, true, 30, 17);
		recipeLayout.getItemStacks().init(1, true, 30, 35);
		recipeLayout.getItemStacks().init(2, true, 30, 53);

		recipeLayout.getItemStacks().init(3, true, 48, 17);
		recipeLayout.getItemStacks().init(4, true, 48, 35);
		recipeLayout.getItemStacks().init(5, true, 48, 53);

		recipeLayout.getItemStacks().init(6, true, 66, 17);
		recipeLayout.getItemStacks().init(7, true, 66, 35);
		recipeLayout.getItemStacks().init(8, true, 66, 53);

		recipeLayout.getItemStacks().init(9, false, 124, 35);

		recipeLayout.getItemStacks().set(ingredients);
	}


}
