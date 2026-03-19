package moriz.orangesunshine.fluid.container;

import java.util.function.IntConsumer;

import org.jetbrains.annotations.Nullable;

import moriz.orangesunshine.fluid.PSFluids;
import moriz.orangesunshine.fluid.SimpleFluid;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class MutableFluidContainer {

    public static MutableFluidContainer of(ItemStack stack) {
        return FluidContainer.of(stack).toMutable(stack);
    }

    protected FluidContainer container;

    protected SimpleFluid fluid;
    protected int level;
    protected CompoundTag attributes;

    @Nullable
    protected CompoundTag stackNbt;

    protected MutableFluidContainer(FluidContainer container, SimpleFluid fluid, int level, CompoundTag attributes, @Nullable CompoundTag stackNbt) {
        this.container = container;
        this.fluid = fluid;
        this.level = level;
        this.attributes = attributes;
        this.stackNbt = stackNbt;
    }

    public MutableFluidContainer copy() {
        return new MutableFluidContainer(container, getFluid(), getLevel(), attributes.copy(), stackNbt == null ? null : stackNbt.copy());
    }

    public ItemStack asStack() {
        if (isEmpty()) {
            return container.asEmpty().getDefaultInstance();
        }

        ItemStack stack = container.asFilled(getFluid()).getDefaultInstance();
        CompoundTag rootTag = stackNbt == null ? new CompoundTag() : stackNbt.copy();
        CompoundTag fluidTag = rootTag.getCompoundOrEmpty("fluid").copy();
        fluidTag.putInt("level", getLevel());
        fluidTag.putString("id", getFluid().getId().toString());
        fluidTag.put("attributes", (isEmpty() ? FluidContainer.EMPTY_NBT : attributes).copy());
        rootTag.put("fluid", fluidTag);

        if (rootTag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            CustomData.set(DataComponents.CUSTOM_DATA, stack, rootTag);
        }
        return stack;
    }

    public boolean isEmpty() {
        return getLevel() <= 0 || getFluid().isEmpty();
    }

    public int getLevel() {
        return level;
    }

    public SimpleFluid getFluid() {
        return fluid;
    }

    public int getCapacity() {
        return container.getMaxCapacity();
    }

    public CompoundTag getAttributes() {
        return attributes;
    }

    public MutableFluidContainer withLevel(int level) {
        this.level = Mth.clamp(level, 0, getCapacity());
        if (this.level == 0) {
            return withFluid(PSFluids.EMPTY);
        }
        return this;
    }

    public MutableFluidContainer decrement(int levels) {
        return withLevel(getLevel() - levels);
    }

    public MutableFluidContainer withFluid(SimpleFluid fluid) {
        this.fluid = fluid;
        if (fluid.isEmpty()) {
            this.level = 0;
            this.attributes = FluidContainer.EMPTY_NBT;
        }

        return this;
    }

    public MutableFluidContainer withAttributes(@Nullable CompoundTag attributes) {
        this.attributes = isEmpty() || attributes == null || attributes.isEmpty() ? FluidContainer.EMPTY_NBT : attributes.copy();
        return this;
    }

    /**
     * Removes a certain amount of fluid from the given stack and returns a
     * new stack containing the amount of fluid that was drained.
     */
    public MutableFluidContainer drain(int amount) {
        amount = Math.min(getLevel(), amount);
        MutableFluidContainer removed = copy().withLevel(amount);
        decrement(amount);
        return removed;
    }

    /**
     * Adds fluid to this container.
     * Returns the remaining levels.
     */
    public int deposit(int amount, SimpleFluid fluid) {
        int newLevel = Math.min(getCapacity(), getLevel() + amount);
        if (getFluid().isEmpty()) {
            withFluid(fluid);
        } else if (getFluid() != fluid) {
            return amount;
        }
        withLevel(newLevel);
        return amount - newLevel;
    }

    /**
     * Copies the fluid information from another stack to this one.
     */
    public MutableFluidContainer fillFrom(MutableFluidContainer other) {
        if (other.isEmpty()) {
            return this;
        }

        attributes = other.attributes;
        return withFluid(other.getFluid()).withLevel(getLevel() + other.getLevel());
    }

    /**
     * Transfers fluid from this container to another.
     * Returns a stack with whatever remains after the transfer is completed.
     */
    public MutableFluidContainer transfer(int levels, MutableFluidContainer outputContainer, @Nullable IntConsumer changeCallback) {
        var inputFluid = getFluid();
        var outputFluid = outputContainer.getFluid();

        if (isEmpty()
                || (!outputFluid.isEmpty() && outputFluid != inputFluid)
                || !(outputFluid.isEmpty() || outputContainer.attributes.isEmpty() || NbtUtils.compareNbt(attributes, outputContainer.attributes, true))) {
            return this;
        }

        levels = Math.min(
            Math.min(levels, getLevel()),
            outputContainer.getCapacity() - outputContainer.getLevel()
        );

        if (levels <= 0) {
            return this;
        }

        outputContainer.attributes = attributes.copy();
        drain(levels);
        outputContainer.deposit(levels, inputFluid);
        if (changeCallback != null) {
            changeCallback.accept(levels);
        }
        return this;
    }

    public boolean canReceive(SimpleFluid fluid) {
        return !fluid.isEmpty()
            && (isEmpty() || getFluid() == fluid)
            && getLevel() < getCapacity()
            && fluid.isSuitableContainer(container);
    }
}
