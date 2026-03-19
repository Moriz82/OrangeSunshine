/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;

/**
 * Created by Sollace on 5 Jan 2023
 *
 * Recipe that alters a container's fluid when cooked in a furnace.
 *
 *  {
 *    "type": "orangesunshine:smelting_fluid",
 *    "cookingtime": 200,
 *    "experience": 0.2,
 *    "input": {
 *      "fluid": "orangesunshine:coffee"
 *    },
 *    "result": {
 *      "item": "minecraft:empty", <empty to keep as the same>
 *      "attributes": {
 *        "temperature": {
 *          "type": "add",
 *          "value": 1
 *        }
 *      }
 *    }
 *  }
 */
public class SmeltingFluidRecipe extends SmeltingRecipe {
    private final FluidIngredient fluid;
    private final FluidModifyingResult result;

    private WeakReference<ItemStack> lastQueriedStack = new WeakReference<>(null);

    public SmeltingFluidRecipe(
            String group,
            CookingBookCategory category,
            FluidIngredient fluid,
            Ingredient inputStack,
            FluidModifyingResult result,
            float experience,
            int cookingTime
    ) {
        super(group, category, inputStack, result.result(), experience, cookingTime);
        this.fluid = fluid;
        this.result = result;
    }

    private static Ingredient emptyIngredient() {
        return Ingredient.of(Stream.of());
    }

    public String getGroup() {
        return group();
    }

    public CookingBookCategory getCategory() {
        return category();
    }

    public FluidIngredient getFluid() {
        return fluid;
    }

    public FluidModifyingResult getResult() {
        return result;
    }

    public List<Ingredient> getIngredients() {
        return List.of(input());
    }

    public float getExperience() {
        return experience();
    }

    public int getCookingTime() {
        return cookingTime();
    }

    public ItemStack getResult(HolderLookup.Provider registries) {
        ItemStack stack = lastQueriedStack.get();
        return stack == null ? result().copy() : result.applyTo(stack);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeSerializer<SmeltingRecipe> getSerializer() {
        return (RecipeSerializer<SmeltingRecipe>)(RecipeSerializer<?>)PSRecipes.SMELTING_RECEPTICAL;
    }

    @Override
    public boolean matches(SingleRecipeInput inventory, Level level) {
        ItemStack stack = inventory.item();
        lastQueriedStack = new WeakReference<>(stack.copy());
        return (input().isEmpty() || input().test(stack)) && fluid.test(stack);
    }

    @Override
    public ItemStack assemble(SingleRecipeInput inventory, HolderLookup.Provider registries) {
        ItemStack stack = inventory.item();
        lastQueriedStack = new WeakReference<>(stack.copy());
        return result.applyTo(stack);
    }

    static class Serializer implements RecipeSerializer<SmeltingFluidRecipe> {
        private static final MapCodec<SmeltingFluidRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(SmeltingFluidRecipe::getGroup),
                CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(SmeltingFluidRecipe::getCategory),
                FluidIngredient.CODEC.fieldOf("input").forGetter(SmeltingFluidRecipe::getFluid),
                Ingredient.CODEC.optionalFieldOf("item").forGetter(recipe ->
                        recipe.input().isEmpty() ? Optional.empty() : Optional.of(recipe.input())),
                FluidModifyingResult.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                com.mojang.serialization.Codec.FLOAT.fieldOf("experience").forGetter(SmeltingFluidRecipe::getExperience),
                com.mojang.serialization.Codec.INT.optionalFieldOf("cookingtime", 200).forGetter(SmeltingFluidRecipe::getCookingTime)
        ).apply(instance, (group, category, fluid, item, result, experience, cookingTime) ->
                new SmeltingFluidRecipe(group, category, fluid, item.orElseGet(SmeltingFluidRecipe::emptyIngredient), result, experience, cookingTime)));

        private static final StreamCodec<RegistryFriendlyByteBuf, SmeltingFluidRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                SmeltingFluidRecipe::getGroup,
                CookingBookCategory.STREAM_CODEC,
                SmeltingFluidRecipe::getCategory,
                FluidIngredient.STREAM_CODEC,
                SmeltingFluidRecipe::getFluid,
                Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
                recipe -> recipe.input().isEmpty() ? Optional.empty() : Optional.of(recipe.input()),
                ByteBufCodecs.fromCodecWithRegistriesTrusted(FluidModifyingResult.CODEC),
                recipe -> recipe.result,
                ByteBufCodecs.FLOAT,
                SmeltingFluidRecipe::getExperience,
                ByteBufCodecs.VAR_INT,
                SmeltingFluidRecipe::getCookingTime,
                (group, category, fluid, item, result, experience, cookingTime) ->
                        new SmeltingFluidRecipe(group, category, fluid, item.orElseGet(SmeltingFluidRecipe::emptyIngredient), result, experience, cookingTime)
        );

        @Override
        public MapCodec<SmeltingFluidRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SmeltingFluidRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
