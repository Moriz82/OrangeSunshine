/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Created from by Sollace on 7 Feb 2023
 *
 * Used by the mash table to produce a particular fluid from items dropped in.
 */
public class MashingRecipe extends FillRecepticalRecipe {
    private final int stewTime;
    private final FluidIngredient fluid;

    public MashingRecipe(String group, CraftingBookCategory category, FluidIngredient output,
                         FluidIngredient fluid, List<Ingredient> input, int stewTime) {
        super(group, category, output, input, Ingredient.of(java.util.stream.Stream.of()));
        this.fluid = fluid;
        this.stewTime = stewTime;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeSerializer<FillRecepticalRecipe> getSerializer() {
        return (RecipeSerializer<FillRecepticalRecipe>)(RecipeSerializer<?>)PSRecipes.MASHING;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeType<CraftingRecipe> getType() {
        return (RecipeType<CraftingRecipe>)(RecipeType<?>)PSRecipes.MASHING_TYPE;
    }

    public int getStewTime() {
        return stewTime;
    }

    public FluidIngredient getPoolFluid() {
        return fluid;
    }

    public MatchResult matchPartially(Object2IntMap<Item> inputs) {
        List<Ingredient> expectedInputs = new ArrayList<>(getIngredients());
        Object2IntMap<Item> unmatchedInputs = new Object2IntOpenHashMap<>(inputs);

        for (Item item : inputs.keySet()) {
            ItemStack stack = item.getDefaultInstance();

            if (expectedInputs.isEmpty()) {
                return MatchResult.NONE;
            }

            Iterator<Ingredient> iter = expectedInputs.iterator();
            while (iter.hasNext()) {
                Ingredient ingredient = iter.next();
                if (ingredient.test(stack)) {
                    iter.remove();
                    if (!unmatchedInputs.containsKey(item)) {
                        return MatchResult.of(unmatchedInputs.isEmpty(), expectedInputs.isEmpty());
                    }

                    unmatchedInputs.computeInt(item, (entry, count) -> count <= 1 ? null : count - 1);

                    if (unmatchedInputs.isEmpty()) {
                        return MatchResult.of(true, expectedInputs.isEmpty());
                    }
                }
            }
        }

        return MatchResult.of(unmatchedInputs.isEmpty(), expectedInputs.isEmpty());
    }

    static class Serializer implements RecipeSerializer<MashingRecipe> {
        private static final MapCodec<MashingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(MashingRecipe::group),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(MashingRecipe::category),
                FluidIngredient.CODEC.fieldOf("result").forGetter(MashingRecipe::getOutputFluid),
                FluidIngredient.CODEC.fieldOf("base_fluid").forGetter(MashingRecipe::getPoolFluid),
                RecipeUtils.SHAPELESS_RECIPE_INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(MashingRecipe::getIngredients),
                Codec.INT.optionalFieldOf("stew_time", 0).forGetter(MashingRecipe::getStewTime)
        ).apply(instance, MashingRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, MashingRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                MashingRecipe::group,
                CraftingBookCategory.STREAM_CODEC,
                MashingRecipe::category,
                FluidIngredient.STREAM_CODEC,
                MashingRecipe::getOutputFluid,
                FluidIngredient.STREAM_CODEC,
                MashingRecipe::getPoolFluid,
                ByteBufCodecs.collection(ArrayList::new, Ingredient.CONTENTS_STREAM_CODEC),
                MashingRecipe::getIngredients,
                ByteBufCodecs.VAR_INT,
                MashingRecipe::getStewTime,
                MashingRecipe::new
        );

        @Override
        public MapCodec<MashingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MashingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public enum MatchResult {
        NONE,
        INPUTS_ONLY,
        INGREDIENTS_ONLY,
        BOTH;

        public boolean isMatch() {
            return this == INPUTS_ONLY || this == BOTH;
        }

        public boolean isCraftable() {
            return this == BOTH;
        }

        public static MatchResult of(boolean inputs, boolean ingredients) {
            return inputs && ingredients ? BOTH : inputs ? INPUTS_ONLY : ingredients ? INGREDIENTS_ONLY : NONE;
        }
    }
}
