package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity.TileDryingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerDryingTable extends Container {
    private final TileDryingTable tile;
    private int[] lastProcess = new int[TileDryingTable.INPUT_SLOTS * 2];

    public ContainerDryingTable(InventoryPlayer playerInventory, TileDryingTable tile) {
        this.tile = tile;

        // 3x3 input grid at x=8, y=18
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlotToContainer(new Slot(tile, row * 3 + col, 8 + col * 18, 18 + row * 18));
            }
        }
        // Output slot
        addSlotToContainer(new Slot(tile, TileDryingTable.OUTPUT_SLOT, 116, 36));

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 102 + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 160));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tile.isUsableByPlayer(player);
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, tile);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener listener : listeners) {
            for (int i = 0; i < TileDryingTable.INPUT_SLOTS * 2; i++) {
                int val = tile.getField(i);
                if (lastProcess[i] != val) {
                    listener.sendWindowProperty(this, i, val);
                    lastProcess[i] = val;
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        tile.setField(id, value);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            int tileSlots = TileDryingTable.TOTAL_SLOTS;
            if (index < tileSlots) {
                if (!mergeItemStack(slotStack, tileSlots, inventorySlots.size(), true)) return ItemStack.EMPTY;
            } else {
                if (!mergeItemStack(slotStack, 0, TileDryingTable.INPUT_SLOTS, false)) return ItemStack.EMPTY;
            }
            if (slotStack.isEmpty()) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();
        }
        return stack;
    }
}
