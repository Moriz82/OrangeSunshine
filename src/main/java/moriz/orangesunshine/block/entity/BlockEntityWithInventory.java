/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block.entity;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockEntityWithInventory extends SyncedBlockEntity implements WorldlyContainer {
    protected static final int[] NO_SLOTS = new int[0];

    private final NonNullList<ItemStack> inventory;

    public BlockEntityWithInventory(BlockEntityType<? extends BlockEntityWithInventory> type, BlockPos pos, BlockState state, int size) {
        super(type, pos, state);
        inventory = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    protected void writeNbt(CompoundTag compound) {
        super.writeNbt(compound);
        compound.store("items", ItemStack.OPTIONAL_CODEC.listOf(), inventory);
    }

    @Override
    protected void readNbt(CompoundTag compound) {
        super.readNbt(compound);
        List<ItemStack> items = compound.read("items", ItemStack.OPTIONAL_CODEC.listOf()).orElse(List.of());
        for (int i = 0; i < inventory.size(); i++) {
            inventory.set(i, i < items.size() ? items.get(i) : ItemStack.EMPTY);
        }
    }

    @Override
    public int getContainerSize() {
        return inventory.size();
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.get(slot);
    }

    @Override
    public void clearContent() {
        inventory.clear();
        onInventoryChanged();
    }

    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack removed = inventory.get(slot);
        inventory.set(slot, ItemStack.EMPTY);
        if (!removed.isEmpty()) {
            onInventoryChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack current = inventory.get(slot);
        ItemStack removed = current.split(amount);
        if (!removed.isEmpty() || current.isEmpty()) {
            if (current.isEmpty()) {
                inventory.set(slot, ItemStack.EMPTY);
            }
            onInventoryChanged();
        }
        if (!current.isEmpty() && current.getCount() == 0) {
            inventory.set(slot, ItemStack.EMPTY);
        }
        if (!removed.isEmpty() && inventory.get(slot).isEmpty()) {
            inventory.set(slot, ItemStack.EMPTY);
        }
        return removed;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        onInventoryChanged();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return NO_SLOTS;
    }

    public void onInventoryChanged() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return true;
    }
}
