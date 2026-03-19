/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import moriz.orangesunshine.fluid.container.MutableFluidContainer;
import net.minecraft.core.NonNullList;
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

public class FluidAwareShapelessRecipe extends ShapelessRecipe {
    private final ItemStack output;
    private final NonNullList<OptionalFluidIngredient> ingredients;
    private final List<OptionalFluidIngredient> fluidRestrictions;

    public FluidAwareShapelessRecipe(
            String group,
            CraftingBookCategory category,
            ItemStack output,
            List<OptionalFluidIngredient> input
    ) {
        super(group, category, output, input.stream()
                .map(ingredient -> ingredient.receptical().orElseGet(FluidAwareShapelessRecipe::emptyIngredient))
                .toList());
        this.output = output;
        this.ingredients = NonNullList.createWithCapacity(input.size());
        this.ingredients.addAll(input);
        this.fluidRestrictions = this.ingredients.stream()
                .filter(ingredient -> ingredient.fluid().filter(fluid -> fluid.level() > 0).isPresent())
                .toList();
    }

    private static Ingredient emptyIngredient() {
        return Ingredient.of(Stream.of());
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

    public NonNullList<OptionalFluidIngredient> getFluidAwareIngredients() {
        return ingredients;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeSerializer<ShapelessRecipe> getSerializer() {
        return (RecipeSerializer<ShapelessRecipe>)(RecipeSerializer<?>)PSRecipes.SHAPELESS_FLUID;
    }

    @Override
    public boolean matches(CraftingInput inventory, Level level) {
        List<OptionalFluidIngredient> unmatchedInputs = new ArrayList<>(ingredients);
        long matched = RecipeUtils.stacks(inventory)
                .filter(stack -> unmatchedInputs.stream()
                        .filter(ingredient -> ingredient.test(stack))
                        .findFirst()
                        .map(unmatchedInputs::remove)
                        .orElse(false))
                .count();

        return matched == ingredients.size() && unmatchedInputs.isEmpty();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inventory) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(inventory.size(), ItemStack.EMPTY);

        for (int i = 0; i < remaining.size(); ++i) {
            ItemStack stack = inventory.getItem(i);
            ItemStack remainder = stack.getItem().getCraftingRemainder();
            if (!remainder.isEmpty()) {
                remaining.set(i, remainder.copy());
                continue;
            }

            ItemStack fluidRemainder = fluidRestrictions.stream()
                    .filter(ingredient -> ingredient.test(stack))
                    .findFirst()
                    .flatMap(OptionalFluidIngredient::fluid)
                    .map(fluid -> MutableFluidContainer.of(stack.copy()).decrement(fluid.level()))
                    .map(MutableFluidContainer::asStack)
                    .orElse(ItemStack.EMPTY);

            remaining.set(i, fluidRemainder);
        }

        return remaining;
    }

    static class Serializer implements RecipeSerializer<FluidAwareShapelessRecipe> {
        private static final StreamCodec<RegistryFriendlyByteBuf, OptionalFluidIngredient> OPTIONAL_FLUID_INGREDIENT_STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(FluidIngredient.STREAM_CODEC),
                OptionalFluidIngredient::fluid,
                ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC),
                OptionalFluidIngredient::receptical,
                OptionalFluidIngredient::new
        );

        private static final MapCodec<FluidAwareShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(FluidAwareShapelessRecipe::getGroup),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(FluidAwareShapelessRecipe::getCategory),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(FluidAwareShapelessRecipe::getResult),
                OptionalFluidIngredient.CODEC.listOf().fieldOf("ingredients").forGetter(FluidAwareShapelessRecipe::getFluidAwareIngredients)
        ).apply(instance, FluidAwareShapelessRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, FluidAwareShapelessRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                FluidAwareShapelessRecipe::getGroup,
                CraftingBookCategory.STREAM_CODEC,
                FluidAwareShapelessRecipe::getCategory,
                ItemStack.STREAM_CODEC,
                FluidAwareShapelessRecipe::getResult,
                ByteBufCodecs.collection(ArrayList::new, OPTIONAL_FLUID_INGREDIENT_STREAM_CODEC),
                FluidAwareShapelessRecipe::getFluidAwareIngredients,
                FluidAwareShapelessRecipe::new
        );

        @Override
        public MapCodec<FluidAwareShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FluidAwareShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
