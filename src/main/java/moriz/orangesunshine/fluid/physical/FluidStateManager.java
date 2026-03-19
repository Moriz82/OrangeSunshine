package moriz.orangesunshine.fluid.physical;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

public record FluidStateManager(Set<FluidProperty<?>> properties) {

    public <O, S extends StateHolder<O, S>> void appendProperties(StateDefinition.Builder<O, S> builder) {
        properties.forEach(p -> builder.add(p.property));
    }

    public <O, S extends StateHolder<O, S>> S copyStateValues(StateHolder<?, ?> from, S to) {
        for (FluidProperty<?> property : properties) {
            to = copyStateValue(from, to, property.property);
        }
        return to;
    }

    public <O, S extends StateHolder<O, S>> S computeAverage(Stream<? extends StateHolder<?, ?>> states, S to) {
        return states.findFirst().map(state -> copyStateValues(state, to)).orElse(to);
    }

    public ItemStack writeStack(StateHolder<?, ?> state, ItemStack stack) {
        for (FluidProperty<?> property : properties) {
            stack = property.writeToStack(state, stack);
        }
        return stack;
    }

    public <O, S extends StateHolder<O, S>> S readStack(S state, ItemStack stack) {
        for (FluidProperty<?> property : properties) {
            state = property.readFromStack(state, stack);
        }
        return state;
    }

    private <O, S extends StateHolder<O, S>, T extends Comparable<T>> S copyStateValue(StateHolder<?, ?> from, S to, Property<T> property) {
        return from.getOptionalValue(property).map(v -> to.trySetValue(property, v)).orElse(to);
    }

    public record FluidProperty<T extends Comparable<T>>(
            Property<T> property,
            BiFunction<ItemStack, T, ItemStack> writer,
            Function<ItemStack, T> reader) {
        ItemStack writeToStack(StateHolder<?, ?> state, ItemStack stack) {
            return writer.apply(stack, state.getValue(property));
        }

        <O, S extends StateHolder<O, S>> S readFromStack(S state, ItemStack stack) {
            return state.setValue(property, reader.apply(stack));
        }
    }
}
