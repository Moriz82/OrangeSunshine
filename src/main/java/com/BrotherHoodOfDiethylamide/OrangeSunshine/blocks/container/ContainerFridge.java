package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity.TileFridge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerFridge extends Container {
    private final TileFridge tile;
    private int[] lastProcess = new int[TileFridge.INPUT_SLOTS * 2];

    public ContainerFridge(InventoryPlayer playerInventory, TileFridge tile) {
        this.tile = tile;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlotToContainer(new Slot(tile, row * 3 + col, 8 + col * 18, 18 + row * 18));
            }
        }
        addSlotToContainer(new Slot(tile, TileFridge.OUTPUT_SLOT, 116, 36));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 102 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 160));
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
            for (int i = 0; i < TileFridge.INPUT_SLOTS * 2; i++) {
                int val = tile.getField(i);
                if (lastProcess[i] != val) { listener.sendWindowProperty(this, i, val); lastProcess[i] = val; }
            }
        }
    }

    @Override @SideOnly(Side.CLIENT) public void updateProgressBar(int id, int value) { tile.setField(id, value); }

    @Override public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            int tileSlots = TileFridge.TOTAL_SLOTS;
            if (index < tileSlots) { if (!mergeItemStack(slotStack, tileSlots, inventorySlots.size(), true)) return ItemStack.EMPTY; }
            else { if (!mergeItemStack(slotStack, 0, TileFridge.INPUT_SLOTS, false)) return ItemStack.EMPTY; }
            if (slotStack.isEmpty()) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();
        }
        return stack;
    }
}
