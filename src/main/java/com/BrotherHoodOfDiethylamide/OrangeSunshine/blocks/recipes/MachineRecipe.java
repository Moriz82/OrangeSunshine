package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes;

import net.minecraft.item.ItemStack;

public class MachineRecipe {
    private final ItemStack[] inputs;
    private final ItemStack output;
    private final int processingTime;

    public MachineRecipe(ItemStack[] inputs, ItemStack output, int processingTime) {
        this.inputs = inputs;
        this.output = output;
        this.processingTime = processingTime;
    }

    public MachineRecipe(ItemStack input, ItemStack output, int processingTime) {
        this(new ItemStack[]{input}, output, processingTime);
    }

    public ItemStack[] getInputs() {
        return inputs;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public boolean matches(ItemStack[] inputItems) {
        if (inputs.length == 1) {
            for (ItemStack item : inputItems) {
                if (!item.isEmpty() && item.getItem() == inputs[0].getItem()) {
                    return true;
                }
            }
            return false;
        }
        // For multi-input: check that all required inputs are present
        boolean[] matched = new boolean[inputs.length];
        for (ItemStack required : inputs) {
            for (ItemStack item : inputItems) {
                if (!item.isEmpty() && item.getItem() == required.getItem() && item.getCount() >= required.getCount()) {
                    // mark as matched
                    break;
                }
            }
        }
        int matchCount = 0;
        for (ItemStack required : inputs) {
            for (ItemStack item : inputItems) {
                if (!item.isEmpty() && item.getItem() == required.getItem() && item.getCount() >= required.getCount()) {
                    matchCount++;
                    break;
                }
            }
        }
        return matchCount >= inputs.length;
    }

    /** Returns the first slot index in inputItems that matches the first input ingredient. */
    public int findMatchingSlot(ItemStack[] inputItems) {
        for (int i = 0; i < inputItems.length; i++) {
            if (!inputItems[i].isEmpty() && inputs.length > 0 && inputItems[i].getItem() == inputs[0].getItem()) {
                return i;
            }
        }
        return -1;
    }
}
