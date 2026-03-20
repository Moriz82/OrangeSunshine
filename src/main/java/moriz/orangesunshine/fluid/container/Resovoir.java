package moriz.orangesunshine.fluid.container;

import java.util.function.IntConsumer;

import org.jetbrains.annotations.Nullable;

import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.util.NbtSerialisable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * @author Sollace
 * @since 3 Jan 2023
 */
public class Resovoir implements NbtSerialisable, FluidStore {
    private final FluidContainer container;
    private MutableFluidContainer stack;

    private final ChangeListener changeCallback;

    public Resovoir(int capacity, ChangeListener changeCallback) {
        this.container = FluidContainer.withCapacity(Items.STONE, capacity);
        this.stack = container.toMutable(Items.STONE.getDefaultInstance());
        this.changeCallback = changeCallback;
    }

    public SimpleFluid getFluidType() {
        return stack.getFluid();
    }

    public void transferTo(Resovoir tank) {
        tank.stack = stack;
        stack = container.toMutable(Items.STONE.getDefaultInstance());
    }

    @Override
    public MutableFluidContainer getContents() {
        return stack;
    }

    public boolean isEmpty() {
        return getContents().isEmpty();
    }

    public int getCapacity() {
        return getContents().getCapacity();
    }

    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public MutableFluidContainer deposit(int levels, MutableFluidContainer input, @Nullable IntConsumer changeCallback) {
        return input.transfer((int)Math.min(getCapacity() - getLevel(), levels), stack, levelsChange -> {
            this.changeCallback.onFill(this, levelsChange);
            if (changeCallback != null) {
                changeCallback.accept(levelsChange);
            }
        });
    }

    @Override
    public MutableFluidContainer drain(int levels, MutableFluidContainer output, @Nullable IntConsumer changeCallback) {
        stack.transfer(levels, output, levelsChange -> {
            this.changeCallback.onDrain(this);
            if (changeCallback != null) {
                changeCallback.accept(levelsChange);
            }
        });
        return output;
    }

    public void clearContent() {
        stack = container.toMutable(Items.STONE.getDefaultInstance());
        changeCallback.onDrain(this);
    }

    public int getContainerSize() {
        return 1;
    }

    public ItemStack getItem(int slot) {
        return getStack();
    }

    public ItemStack removeItem(int slot, int count) {
        return ItemStack.EMPTY;
    }

    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStack.EMPTY;
    }

    public void setItem(int slot, ItemStack stack) {
        boolean wasEmpty = isEmpty();
        int oldLevel = getLevel();
        this.stack = container.toMutable(stack);
        if (isEmpty() != wasEmpty) {
            if (wasEmpty) {
                changeCallback.onFill(this, getLevel());
            } else {
                changeCallback.onDrain(this);
            }
        } else if (oldLevel != getLevel()) {
            if (oldLevel < getLevel()) {
                changeCallback.onFill(this, getLevel() - oldLevel);
            } else {
                changeCallback.onDrain(this);
            }
        } else {
            changeCallback.onIdle(this);
        }
    }

    public void setChanged() {
    }

    public boolean stillValid(Player player) {
        return true;
    }

    public boolean canPlaceItem(int slot, ItemStack stack) {
        return !FluidContainer.of(stack).getFluid(stack).isEmpty();
    }

    @Override
    public void toNbt(CompoundTag compound) {
        compound.store("stack", ItemStack.OPTIONAL_CODEC, stack.asStack());
    }

    @Override
    public void fromNbt(CompoundTag compound) {
        stack = container.toMutable(compound.read("stack", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
    }

    public interface ChangeListener {
        void onDrain(Resovoir resovoir);

        void onFill(Resovoir resovoir, int amountFilled);

        default void onIdle(Resovoir resovoir) {

        }
    }
}
