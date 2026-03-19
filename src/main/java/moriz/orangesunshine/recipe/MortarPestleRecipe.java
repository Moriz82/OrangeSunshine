package moriz.orangesunshine.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class MortarPestleRecipe implements Recipe<SingleRecipeInput> {
    private final ItemStack output;
    private final List<Ingredient> recipeItems;

    public MortarPestleRecipe(List<Ingredient> ingredients, ItemStack itemStack) {
        this.output = itemStack;
        this.recipeItems = List.copyOf(ingredients);
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return !level.isClientSide() && !recipeItems.isEmpty() && recipeItems.get(0).test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<MortarPestleRecipe> getSerializer() {
        return PSRecipes.MORTAR_PESTLE;
    }

    @Override
    public RecipeType<MortarPestleRecipe> getType() {
        return PSRecipes.MORTAR_PESTLE_TYPE;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(recipeItems);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public List<Ingredient> getIngredients() {
        return recipeItems;
    }

    public ItemStack getResult() {
        return output;
    }

    public static class Type implements RecipeType<MortarPestleRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<MortarPestleRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        private static final MapCodec<MortarPestleRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                validateAmount(Ingredient.CODEC, 9).fieldOf("ingredients").forGetter(MortarPestleRecipe::getIngredients),
                ItemStack.STRICT_CODEC.fieldOf("output").forGetter(MortarPestleRecipe::getResult)
        ).apply(instance, MortarPestleRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, MortarPestleRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(ArrayList::new, Ingredient.CONTENTS_STREAM_CODEC),
                MortarPestleRecipe::getIngredients,
                ItemStack.STREAM_CODEC,
                MortarPestleRecipe::getResult,
                MortarPestleRecipe::new
        );

        private static com.mojang.serialization.Codec<List<Ingredient>> validateAmount(
                com.mojang.serialization.Codec<Ingredient> delegate, int max) {
            return delegate.listOf().validate(list -> {
                if (list.isEmpty()) {
                    return DataResult.error(() -> "Recipe has no ingredients!");
                }
                if (list.size() > max) {
                    return DataResult.error(() -> "Recipe has too many ingredients!");
                }
                return DataResult.success(list);
            });
        }

        @Override
        public MapCodec<MortarPestleRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MortarPestleRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
