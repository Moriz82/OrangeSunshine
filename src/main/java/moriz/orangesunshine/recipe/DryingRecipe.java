package moriz.orangesunshine.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Collections;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class DryingRecipe implements Recipe<CraftingInput> {
    private final String group;
    private final CookingBookCategory category;
    private final Ingredient ingredient;
    private final ItemStack result;
    private final float experience;
    private final int cookingTime;

    public DryingRecipe(String group, CookingBookCategory category, Ingredient input, ItemStack output, float experience, int cookTime) {
        this.group = group;
        this.category = category;
        this.ingredient = input;
        this.result = output;
        this.experience = experience;
        this.cookingTime = cookTime;
    }

    @Override
    public boolean matches(CraftingInput inventory, Level level) {
        if (inventory.width() != 3 || inventory.height() != 3 || inventory.size() != 9 || inventory.ingredientCount() != 9) {
            return false;
        }

        for (int slot = 0; slot < inventory.size(); slot++) {
            if (!ingredient.test(inventory.getItem(slot))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<DryingRecipe> getSerializer() {
        return PSRecipes.DRYING;
    }

    @Override
    public RecipeType<DryingRecipe> getType() {
        return PSRecipes.DRYING_TYPE;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(Collections.nCopies(9, ingredient));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CAMPFIRE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public Ingredient getInput() {
        return ingredient;
    }

    public ItemStack getResult() {
        return result;
    }

    public CookingBookCategory getCategory() {
        return category;
    }

    public float getExperience() {
        return experience;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    @Override
    public String group() {
        return group;
    }

    public String getGroup() {
        return group();
    }

    public ItemStack getResult(HolderLookup.Provider registries) {
        return getResult();
    }

    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<DryingRecipe> {
        private final MapCodec<DryingRecipe> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> streamCodec;

        public Serializer(int cookingTime) {
            codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(DryingRecipe::group),
                    CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(DryingRecipe::getCategory),
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(DryingRecipe::getInput),
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(DryingRecipe::getResult),
                    Codec.FLOAT.optionalFieldOf("experience", 0F).forGetter(DryingRecipe::getExperience),
                    Codec.INT.optionalFieldOf("cookingTime", cookingTime).forGetter(DryingRecipe::getCookingTime)
            ).apply(instance, DryingRecipe::new));

            streamCodec = StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    DryingRecipe::group,
                    CookingBookCategory.STREAM_CODEC,
                    DryingRecipe::getCategory,
                    Ingredient.CONTENTS_STREAM_CODEC,
                    DryingRecipe::getInput,
                    ItemStack.STREAM_CODEC,
                    DryingRecipe::getResult,
                    ByteBufCodecs.FLOAT,
                    DryingRecipe::getExperience,
                    ByteBufCodecs.VAR_INT,
                    DryingRecipe::getCookingTime,
                    DryingRecipe::new
            );
        }

        @Override
        public MapCodec<DryingRecipe> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> streamCodec() {
            return streamCodec;
        }
    }
}
