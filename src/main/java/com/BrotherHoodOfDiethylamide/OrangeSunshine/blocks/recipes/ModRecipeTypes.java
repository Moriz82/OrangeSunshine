package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipeTypes {
	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, OrangeSunshine.MOD_ID);

	public static final RegistryObject<FridgeRecipe.Serializer> FRIDGE_SERIALIZER = RECIPE_SERIALIZER.register("fridge_crafting", FridgeRecipe.Serializer::new);
	public static final RegistryObject<CompoundExtractorRecipe.Serializer> COMPOUND_EXTRACTOR_SERIALIZER = RECIPE_SERIALIZER.register("compound_extractor_crafting", CompoundExtractorRecipe.Serializer::new);
	public static final RegistryObject<CompoundExtractorRecipe.Serializer> COMPOUND_COMPRESSOR_SERIALIZER = RECIPE_SERIALIZER.register("compound_compressor_crafting", CompoundExtractorRecipe.Serializer::new);
	public static final RegistryObject<DryingTableRecipe.Serializer> DRYING_TABLE_SERIALIZER = RECIPE_SERIALIZER.register("drying_table_crafting", DryingTableRecipe.Serializer::new);

	public static IRecipeType<FridgeRecipe> FRIDGE_RECIPE = new FridgeRecipe.FridgeRecipeType();
	public static IRecipeType<CompoundExtractorRecipe> COMPOUND_EXTRACTOR_RECIPE = new CompoundExtractorRecipe.CompoundExtractorRecipeType();
	public static IRecipeType<CompoundCompressorRecipe> COMPOUND_COMPRESSOR_RECIPE = new CompoundCompressorRecipe.CompoundCompressorRecipeType();
	public static IRecipeType<DryingTableRecipe> DRYING_TABLE_RECIPE = new DryingTableRecipe.DryingTableRecipeType();

	public static void register (IEventBus eventBus) {
		RECIPE_SERIALIZER.register(eventBus);

		Registry.register(Registry.RECIPE_TYPE, FridgeRecipe.TYPE_ID, FRIDGE_RECIPE);
		Registry.register(Registry.RECIPE_TYPE, CompoundExtractorRecipe.TYPE_ID, COMPOUND_EXTRACTOR_RECIPE);
		Registry.register(Registry.RECIPE_TYPE, CompoundCompressorRecipe.TYPE_ID, COMPOUND_COMPRESSOR_RECIPE);
		Registry.register(Registry.RECIPE_TYPE, DryingTableRecipe.TYPE_ID, DRYING_TABLE_RECIPE);
	}
}
