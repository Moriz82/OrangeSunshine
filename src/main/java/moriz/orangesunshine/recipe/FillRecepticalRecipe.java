/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import moriz.orangesunshine.fluid.container.FluidContainer;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Created from "RecipeFillDrink" by Sollace on 5 Jan 2023
 * Original by lukas on 21.10.14.
 * Recipe that takes as a config:
 * - Input Ingrediences (unshaped)
 * - Input Container
 * - Preconfigured fluid+level
 *
 * Outputs:
 * - Original Container filled with assigned fluid and level
 */
public class FillRecepticalRecipe implements CraftingRecipe {
    private final String group;
    private final CraftingBookCategory category;
    private final Ingredient receptical;
    private final List<Ingredient> input;
    private final FluidIngredient output;
    private final PlacementInfo placementInfo;

    public FillRecepticalRecipe(String group, CraftingBookCategory category, FluidIngredient output,
                                List<Ingredient> input, Ingredient receptical) {
        this.group = group;
        this.category = category;
        this.input = List.copyOf(input);
        this.output = output;
        this.receptical = receptical;
        this.placementInfo = PlacementInfo.create(getCombinedIngredients(this.input, receptical));
    }

    private static List<Ingredient> getCombinedIngredients(List<Ingredient> input, Ingredient receptical) {
        return receptical.isEmpty()
                ? input
                : Stream.concat(Stream.of(receptical), input.stream()).toList();
    }

    private static Ingredient emptyIngredient() {
        return Ingredient.of(Stream.of());
    }

    public FluidIngredient getOutputFluid() {
        return output;
    }

    public List<Ingredient> getIngredients() {
        return input;
    }

    public Ingredient getReceptical() {
        return receptical;
    }

    @Override
    public RecipeSerializer<FillRecepticalRecipe> getSerializer() {
        return PSRecipes.FILL_RECEPTICAL;
    }

    @Override
    public String group() {
        return group;
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }

    @Override
    public PlacementInfo placementInfo() {
        return placementInfo;
    }

    @Override
    public boolean matches(CraftingInput inventory, Level level) {
        if (RecipeUtils.recepticals(inventory).count() != 1) {
            return false;
        }

        long nonEmptyStacks = RecipeUtils.stacks(inventory).count();
        if (nonEmptyStacks != placementInfo.ingredients().size()) {
            return false;
        }

        StackedItemContents stackedContents = inventory.stackedContents();
        return stackedContents.canCraft(getCombinedIngredients(input, receptical), null);
    }

    @Override
    public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registries) {
        return RecipeUtils.recepticals(inventory).findFirst().map(receptical -> {
            ItemStack stack = output.fluid().getDefaultStack(receptical.getKey(), output.level() <= 0
                    ? receptical.getKey().getMaxCapacity(receptical.getValue())
                    : output.level() + receptical.getKey().getLevel(receptical.getValue()));
            FluidContainer.updateFluidAttributes(stack, tag -> tag.merge(output.attributes().copy()));
            return stack;
        }).orElse(ItemStack.EMPTY);
    }

    static class Serializer implements RecipeSerializer<FillRecepticalRecipe> {
        private static final MapCodec<FillRecepticalRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(FillRecepticalRecipe::group),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(FillRecepticalRecipe::category),
                FluidIngredient.CODEC.fieldOf("result").forGetter(FillRecepticalRecipe::getOutputFluid),
                RecipeUtils.SHAPELESS_RECIPE_INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(FillRecepticalRecipe::getIngredients),
                Ingredient.CODEC.optionalFieldOf("receptical").forGetter(recipe ->
                        recipe.getReceptical().isEmpty() ? Optional.empty() : Optional.of(recipe.getReceptical()))
        ).apply(instance, (group, category, result, ingredients, receptical) ->
                new FillRecepticalRecipe(group, category, result, ingredients, receptical.orElseGet(FillRecepticalRecipe::emptyIngredient))));

        private static final StreamCodec<RegistryFriendlyByteBuf, FillRecepticalRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                FillRecepticalRecipe::group,
                CraftingBookCategory.STREAM_CODEC,
                FillRecepticalRecipe::category,
                FluidIngredient.STREAM_CODEC,
                FillRecepticalRecipe::getOutputFluid,
                ByteBufCodecs.collection(ArrayList::new, Ingredient.CONTENTS_STREAM_CODEC),
                FillRecepticalRecipe::getIngredients,
                ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC),
                recipe -> recipe.getReceptical().isEmpty() ? Optional.empty() : Optional.of(recipe.getReceptical()),
                (group, category, result, ingredients, receptical) ->
                        new FillRecepticalRecipe(group, category, result, ingredients, receptical.orElseGet(FillRecepticalRecipe::emptyIngredient))
        );

        @Override
        public MapCodec<FillRecepticalRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FillRecepticalRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
