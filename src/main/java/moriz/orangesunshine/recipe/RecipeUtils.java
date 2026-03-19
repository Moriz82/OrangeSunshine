/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.recipe;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import moriz.orangesunshine.fluid.container.FluidContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;

public interface RecipeUtils {
    Consumer<ItemStack> IMMUTABLE_SLOT = stack -> {
        throw new UnsupportedOperationException("Cannot write to an immutable recipe input");
    };

    Codec<List<Ingredient>> SHAPELESS_RECIPE_INGREDIENTS_CODEC = Ingredient.CODEC.listOf().flatXmap(ingredients -> {
        List<Ingredient> filtered = ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toList();
        if (filtered.isEmpty()) {
            return DataResult.error(() -> "No ingredients for shapeless recipe");
        }
        if (filtered.size() > 9) {
            return DataResult.error(() -> "Too many ingredients for shapeless recipe");
        }
        return DataResult.success(filtered);
    }, DataResult::success);

    static Stream<Map.Entry<FluidContainer, ItemStack>> recepticals(RecipeInput inventory) {
        return stacks(inventory)
                .map(stack -> Map.entry(FluidContainer.of(stack, null), stack))
                .filter(entry -> entry.getKey() != null);
    }

    static Stream<Map.Entry<FluidContainer, ItemStack>> recepticals(Container inventory) {
        return stacks(inventory)
                .map(stack -> Map.entry(FluidContainer.of(stack, null), stack))
                .filter(entry -> entry.getKey() != null);
    }

    static Stream<ItemStack> stacks(RecipeInput inventory) {
        return IntStream.range(0, inventory.size())
                .mapToObj(inventory::getItem)
                .filter(stack -> !stack.isEmpty());
    }

    static Stream<ItemStack> stacks(Container inventory) {
        return IntStream.range(0, inventory.getContainerSize())
                .mapToObj(inventory::getItem)
                .filter(stack -> !stack.isEmpty());
    }

    static Stream<Slot<Map.Entry<FluidContainer, ItemStack>>> recepticalSlots(RecipeInput inventory) {
        return slots(inventory, stack -> FluidContainer.of(stack, null) != null,
                stack -> Map.entry(FluidContainer.of(stack), stack));
    }

    static Stream<Slot<Map.Entry<FluidContainer, ItemStack>>> recepticalSlots(Container inventory) {
        return slots(inventory, stack -> FluidContainer.of(stack, null) != null,
                stack -> Map.entry(FluidContainer.of(stack), stack));
    }

    static <T> Stream<Slot<T>> slots(RecipeInput inventory, Predicate<ItemStack> filter, Function<ItemStack, T> func) {
        return IntStream.range(0, inventory.size())
                .filter(i -> filter.test(inventory.getItem(i)))
                .mapToObj(i -> new Slot<>(func.apply(inventory.getItem(i)), i, IMMUTABLE_SLOT));
    }

    static <T> Stream<Slot<T>> slots(Container inventory, Predicate<ItemStack> filter, Function<ItemStack, T> func) {
        return IntStream.range(0, inventory.getContainerSize())
                .filter(i -> filter.test(inventory.getItem(i)))
                .mapToObj(i -> new Slot<>(func.apply(inventory.getItem(i)), i, stack -> inventory.setItem(i, stack)));
    }

    static <T> List<T> checkLength(List<T> ingredients) {
        if (ingredients.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
        }
        if (ingredients.size() > 9) {
            throw new JsonParseException("Too many ingredients for shapeless recipe");
        }
        return ingredients;
    }

    static Optional<ItemStack> consume(Container inventory, Predicate<ItemStack> filter) {
        return IntStream.range(0, inventory.getContainerSize())
                .filter(i -> filter.test(inventory.getItem(i)))
                .mapToObj(i -> inventory.getItem(i).split(1))
                .findFirst();
    }

    static <T> T iDontCareWhich(Either<T, T> either) {
        return either.left().or(either::right).orElseThrow();
    }

    record Slot<T>(T content, int slot, Consumer<ItemStack> setter) {
        public void set(ItemStack stack) {
            setter.accept(stack);
        }

        public <V> V map(Function<T, V> func) {
            return func.apply(content);
        }
    }
}
