package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ModBlocks;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CompoundExtractorRecipe implements ICompoundExtractorRecipe {

	private final ResourceLocation id;
	private final ItemStack output;
	private final NonNullList<Ingredient> recipeItems;

	public CompoundExtractorRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems) {
		this.id = id;
		this.output = output;
		this.recipeItems = recipeItems;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		List<String> invList = new ArrayList<>();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			invList.add(inv.getItem(i).getDisplayName().getString());
		}

		List<String> items = new ArrayList<>();

		for (Ingredient ingredient : recipeItems) {
			items.add(ingredient.getItems()[0].getDisplayName().getString());
		}

		if (invList.containsAll(items)){
			return true;
		}

		return false;
	}

	@Override
	public NonNullList<Ingredient> getIngredients(){
		return recipeItems;
	}

	@Override
	public ItemStack assemble(IInventory p_77572_1_) {
		return null;
	}

	@Override
	public ItemStack getResultItem() {
		return output;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	public ItemStack getIcon() {
		return new ItemStack(ModBlocks.COMPOUND_EXTRACTOR.get());
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipeTypes.COMPOUND_EXTRACTOR_SERIALIZER.get();
	}

	public static class CompoundExtractorRecipeType implements IRecipeType<CompoundExtractorRecipe> {
		@Override
		public String toString(){
			return CompoundExtractorRecipe.TYPE_ID.toString();
		}
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CompoundExtractorRecipe>{

		@Override
		public CompoundExtractorRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			ItemStack output = ShapedRecipe.itemFromJson (JSONUtils.getAsJsonObject(json, "output"));

			JsonArray ingredients = JSONUtils.getAsJsonArray(json, "ingredients");
			NonNullList<Ingredient> inputs = NonNullList.withSize(JSONUtils.getAsInt(json, "size"), Ingredient.EMPTY);
			for (int i = 0; i < inputs.size(); i++) {
				inputs.set(i, Ingredient.fromJson (ingredients.get(i)));
			}

			return new CompoundExtractorRecipe(recipeId, output, inputs);
		}

		@Nullable
		@Override
		public CompoundExtractorRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
			NonNullList<Ingredient> inputs = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY);
			for (int i = 0; i < inputs.size(); i++) {
				inputs.set(i, Ingredient.fromNetwork (buffer));
			}

			ItemStack output = buffer.readItem();
			return new CompoundExtractorRecipe(recipeId, output, inputs);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, CompoundExtractorRecipe recipe) {
			buffer.writeInt(recipe.getIngredients().size());
			for (Ingredient ing : recipe.getIngredients()) {
				ing.toNetwork (buffer);
			}
			buffer.writeItemStack(recipe.getResultItem(), false);
		}
	}
}
