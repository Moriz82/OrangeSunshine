package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity.TileCompoundExtractor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerCompoundExtractor extends Container {
    private final TileCompoundExtractor tile;
    private int lastBurnTime, lastCurrentBurnTime, lastCookTime, lastTotalCookTime;

    public ContainerCompoundExtractor(InventoryPlayer playerInventory, TileCompoundExtractor tile) {
        this.tile = tile;
        // Input slot
        addSlotToContainer(new Slot(tile, TileCompoundExtractor.SLOT_INPUT, 56, 17));
        // Fuel slot
        addSlotToContainer(new SlotFurnaceFuel(tile, TileCompoundExtractor.SLOT_FUEL, 56, 53));
        // Output slot
        addSlotToContainer(new SlotFurnaceOutput(playerInventory.player, tile, TileCompoundExtractor.SLOT_OUTPUT, 116, 35));

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override public boolean canInteractWith(EntityPlayer player) { return tile.isUsableByPlayer(player); }

    @Override public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, tile);
    }

    @Override public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener listener : listeners) {
            if (lastBurnTime != tile.getField(0)) { listener.sendWindowProperty(this, 0, tile.getField(0)); lastBurnTime = tile.getField(0); }
            if (lastCurrentBurnTime != tile.getField(1)) { listener.sendWindowProperty(this, 1, tile.getField(1)); lastCurrentBurnTime = tile.getField(1); }
            if (lastCookTime != tile.getField(2)) { listener.sendWindowProperty(this, 2, tile.getField(2)); lastCookTime = tile.getField(2); }
            if (lastTotalCookTime != tile.getField(3)) { listener.sendWindowProperty(this, 3, tile.getField(3)); lastTotalCookTime = tile.getField(3); }
        }
    }

    @Override @SideOnly(Side.CLIENT) public void updateProgressBar(int id, int value) { tile.setField(id, value); }

    @Override public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (index == TileCompoundExtractor.SLOT_OUTPUT) {
                if (!mergeItemStack(slotStack, 3, inventorySlots.size(), true)) return ItemStack.EMPTY;
                slot.onSlotChange(slotStack, stack);
            } else if (index > 2) {
                if (!mergeItemStack(slotStack, 0, 2, false)) return ItemStack.EMPTY;
            } else if (!mergeItemStack(slotStack, 3, inventorySlots.size(), false)) {
                return ItemStack.EMPTY;
            }
            if (slotStack.isEmpty()) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();
        }
        return stack;
    }
}
