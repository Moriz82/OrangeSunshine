package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.MachineRecipe;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.MachineRecipeManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;

public class TileCompoundCompressor extends TileEntityLockable implements ITickable {
    public static final int INPUT_SLOTS = 9;
    public static final int OUTPUT_SLOT = 9;
    public static final int TOTAL_SLOTS = 10;

    private NonNullList<ItemStack> inventory = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
    private int[] processTimers = new int[INPUT_SLOTS];
    private int[] totalTimers = new int[INPUT_SLOTS];

    @Override
    public void update() {
        if (world.isRemote) return;

        boolean dirty = false;
        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack input = inventory.get(i);
            if (input.isEmpty()) {
                if (processTimers[i] > 0) { processTimers[i] = 0; dirty = true; }
                continue;
            }

            MachineRecipe recipe = MachineRecipeManager.findRecipe(MachineRecipeManager.COMPOUND_COMPRESSOR, input);
            if (recipe == null) continue;

            ItemStack output = inventory.get(OUTPUT_SLOT);
            ItemStack recipeOut = recipe.getOutput();

            if (!output.isEmpty()) {
                if (output.getItem() != recipeOut.getItem()) continue;
                if (output.getCount() + recipeOut.getCount() > output.getMaxStackSize()) continue;
            }

            totalTimers[i] = recipe.getProcessingTime();
            processTimers[i]++;
            dirty = true;

            if (processTimers[i] >= totalTimers[i]) {
                processTimers[i] = 0;
                input.shrink(recipe.getInputs()[0].getCount());
                if (input.isEmpty() || input.getCount() <= 0) inventory.set(i, ItemStack.EMPTY);

                if (output.isEmpty()) {
                    inventory.set(OUTPUT_SLOT, recipeOut.copy());
                } else {
                    output.grow(recipeOut.getCount());
                }
            }
        }

        if (dirty) markDirty();
    }

    @Override public int getSizeInventory() { return TOTAL_SLOTS; }
    @Override public boolean isEmpty() { for (ItemStack s : inventory) if (!s.isEmpty()) return false; return true; }
    @Override public ItemStack getStackInSlot(int index) { return inventory.get(index); }
    @Override public ItemStack decrStackSize(int index, int count) { return ItemStackHelper.getAndSplit(inventory, index, count); }
    @Override public ItemStack removeStackFromSlot(int index) { return ItemStackHelper.getAndRemove(inventory, index); }
    @Override public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) stack.setCount(getInventoryStackLimit());
        markDirty();
    }
    @Override public int getInventoryStackLimit() { return 64; }
    @Override public boolean isUsableByPlayer(EntityPlayer player) {
        return world.getTileEntity(pos) == this &&
                player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
    }
    @Override public void openInventory(EntityPlayer player) {}
    @Override public void closeInventory(EntityPlayer player) {}
    @Override public boolean isItemValidForSlot(int index, ItemStack stack) { return index < INPUT_SLOTS; }
    @Override public String getName() { return "container.compound_compressor"; }
    @Override public boolean hasCustomName() { return false; }
    @Override public String getGuiID() { return "orangesunshine:compound_compressor"; }
    @Override public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container.ContainerCompoundCompressor(playerInventory, this);
    }
    @Override public int getField(int id) { if (id < INPUT_SLOTS) return processTimers[id]; if (id < INPUT_SLOTS*2) return totalTimers[id-INPUT_SLOTS]; return 0; }
    @Override public void setField(int id, int value) { if (id < INPUT_SLOTS) processTimers[id] = value; else if (id < INPUT_SLOTS*2) totalTimers[id-INPUT_SLOTS] = value; }
    @Override public int getFieldCount() { return INPUT_SLOTS * 2; }
    @Override public void clear() { inventory.clear(); }

    @Override public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, inventory);
        if (compound.hasKey("Timers")) { int[] s = compound.getIntArray("Timers"); for (int i = 0; i < Math.min(s.length, INPUT_SLOTS); i++) processTimers[i] = s[i]; }
    }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, inventory);
        compound.setIntArray("Timers", processTimers);
        return compound;
    }

    public int getProcessTime(int slot) { return processTimers[slot]; }
    public int getTotalTime(int slot) { return totalTimers[slot] > 0 ? totalTimers[slot] : 400; }
}
