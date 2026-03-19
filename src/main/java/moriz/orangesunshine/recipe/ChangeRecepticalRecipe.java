package moriz.orangesunshine.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import moriz.orangesunshine.fluid.container.FluidContainer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 10.11.14.
 * Updated by Sollace on 5 Jan 2023
 *
 * A shapeless recipe that preserves a drink bottle's contents between crafting.
 *
 * Used to change the container a fluid is in without losing any of its contents.
 */
public class ChangeRecepticalRecipe extends ShapelessRecipe {
    private final ItemStack output;
    private final List<Ingredient> ingredients;

    public ChangeRecepticalRecipe(String group, CraftingBookCategory category, ItemStack output, List<Ingredient> input) {
        super(group, category, output, input);
        this.output = output;
        this.ingredients = List.copyOf(input);
    }

    public String getGroup() {
        return group();
    }

    public CraftingBookCategory getCategory() {
        return category();
    }

    public ItemStack getResult() {
        return output;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeSerializer<ShapelessRecipe> getSerializer() {
        return (RecipeSerializer<ShapelessRecipe>)(RecipeSerializer<?>)PSRecipes.CHANGE_RECEPTICAL;
    }

    @Override
    public boolean matches(CraftingInput inventory, Level level) {
        return RecipeUtils.recepticals(inventory).count() == 1 && super.matches(inventory, level);
    }

    @Override
    public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registries) {
        return RecipeUtils.recepticals(inventory).findFirst().map(receptical -> {
            ItemStack input = receptical.getValue().copy();
            ItemStack result = output.copy();
            FluidContainer outputContainer = FluidContainer.of(result, null);
            if (outputContainer != null) {
                result = outputContainer.toMutable(result).fillFrom(receptical.getKey().toMutable(input)).asStack();
            }
            if (input.has(DataComponents.DYED_COLOR)) {
                result.copyFrom(DataComponents.DYED_COLOR, input);
            } else {
                result.remove(DataComponents.DYED_COLOR);
            }
            return result;
        }).orElseGet(output::copy);
    }

    static class Serializer implements RecipeSerializer<ChangeRecepticalRecipe> {
        private static final MapCodec<ChangeRecepticalRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(ChangeRecepticalRecipe::getGroup),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ChangeRecepticalRecipe::getCategory),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ChangeRecepticalRecipe::getResult),
                RecipeUtils.SHAPELESS_RECIPE_INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(ChangeRecepticalRecipe::getIngredients)
        ).apply(instance, ChangeRecepticalRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, ChangeRecepticalRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                ChangeRecepticalRecipe::getGroup,
                CraftingBookCategory.STREAM_CODEC,
                ChangeRecepticalRecipe::getCategory,
                ItemStack.STREAM_CODEC,
                ChangeRecepticalRecipe::getResult,
                ByteBufCodecs.collection(ArrayList::new, Ingredient.CONTENTS_STREAM_CODEC),
                ChangeRecepticalRecipe::getIngredients,
                ChangeRecepticalRecipe::new
        );

        @Override
        public MapCodec<ChangeRecepticalRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ChangeRecepticalRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
