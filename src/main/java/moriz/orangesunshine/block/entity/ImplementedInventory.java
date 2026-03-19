package moriz.orangesunshine.block.entity;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface ImplementedInventory extends Container {
    NonNullList<ItemStack> getItems();

    static ImplementedInventory of(NonNullList<ItemStack> items) {
        return () -> items;
    }

    static ImplementedInventory ofSize(int size) {
        return of(NonNullList.withSize(size, ItemStack.EMPTY));
    }

    @Override
    default int getContainerSize() {
        return getItems().size();
    }

    @Override
    default boolean isEmpty() {
        for (ItemStack stack : getItems()) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    default ItemStack getItem(int slot) {
        return getItems().get(slot);
    }

    @Override
    default ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(getItems(), slot, amount);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    default ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(getItems(), slot);
    }

    @Override
    default void setItem(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        stack.limitSize(getMaxStackSize(stack));
        setChanged();
    }

    @Override
    default void clearContent() {
        getItems().clear();
    }

    @Override
    default void setChanged() {
    }

    @Override
    default boolean stillValid(Player player) {
        return true;
    }
}
