package com.BrotherHoodOfDiethylamide.OrangeSunshine.intergration.jei;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;
import java.util.stream.Collectors;

@JeiPlugin
public class OrangeSunshineJEI implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(OrangeSunshine.MOD_ID, "jei_plugin");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(
				new CompoundExtractorRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
				new CompoundCompressorRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
				new FridgeRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
				new DryingTableRecipeCategory(registration.getJeiHelpers().getGuiHelper())
		);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

		registration.addRecipes(rm.getAllRecipesFor(ModRecipeTypes.COMPOUND_EXTRACTOR_RECIPE).stream()
					.filter(r -> r instanceof CompoundExtractorRecipe).collect(Collectors.toList()),
				CompoundExtractorRecipeCategory.UID);

		registration.addRecipes(rm.getAllRecipesFor(ModRecipeTypes.COMPOUND_COMPRESSOR_RECIPE).stream()
						.filter(r -> r instanceof CompoundCompressorRecipe).collect(Collectors.toList()),
				CompoundCompressorRecipeCategory.UID);

		registration.addRecipes(rm.getAllRecipesFor(ModRecipeTypes.FRIDGE_RECIPE).stream()
						.filter(r -> r instanceof FridgeRecipe).collect(Collectors.toList()),
				FridgeRecipeCategory.UID);

		registration.addRecipes(rm.getAllRecipesFor(ModRecipeTypes.DRYING_TABLE_RECIPE).stream()
						.filter(r -> r instanceof DryingTableRecipe).collect(Collectors.toList()),
				DryingTableRecipeCategory.UID);
	}
}
