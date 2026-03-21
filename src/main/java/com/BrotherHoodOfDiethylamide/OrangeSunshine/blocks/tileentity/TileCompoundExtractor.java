package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.MachineRecipe;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.MachineRecipeManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;

public class TileCompoundExtractor extends TileEntityLockable implements ITickable, ISidedInventory {
    // Slots: 0=input, 1=fuel, 2=output
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int TOTAL_SLOTS = 3;

    private static final int[] SLOTS_TOP = {SLOT_INPUT};
    private static final int[] SLOTS_BOTTOM = {SLOT_OUTPUT, SLOT_FUEL};
    private static final int[] SLOTS_SIDE = {SLOT_FUEL};

    private NonNullList<ItemStack> inventory = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
    private int burnTime = 0;
    private int currentBurnTime = 0;
    private int cookTime = 0;
    private int totalCookTime = 0;

    net.minecraftforge.items.IItemHandler handlerTop = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, EnumFacing.UP);
    net.minecraftforge.items.IItemHandler handlerBottom = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, EnumFacing.DOWN);
    net.minecraftforge.items.IItemHandler handlerSide = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, EnumFacing.WEST);

    @Override
    public void update() {
        if (world.isRemote) return;

        boolean wasBurning = isBurning();
        boolean dirty = false;

        if (isBurning()) { burnTime--; dirty = true; }

        ItemStack fuel = inventory.get(SLOT_FUEL);
        ItemStack input = inventory.get(SLOT_INPUT);

        if (isBurning() || (!fuel.isEmpty() && !input.isEmpty())) {
            MachineRecipe recipe = MachineRecipeManager.findRecipe(MachineRecipeManager.COMPOUND_EXTRACTOR, input);

            if (!isBurning() && canProcess(recipe)) {
                burnTime = getItemBurnTime(fuel);
                currentBurnTime = burnTime;
                if (isBurning()) {
                    dirty = true;
                    if (!fuel.isEmpty()) {
                        fuel.shrink(1);
                        if (fuel.isEmpty()) inventory.set(SLOT_FUEL, fuel.getItem().getContainerItem(fuel));
                    }
                }
            }

            if (isBurning() && canProcess(recipe)) {
                totalCookTime = recipe != null ? recipe.getProcessingTime() : 200;
                cookTime++;
                if (cookTime >= totalCookTime) {
                    cookTime = 0;
                    processItem(recipe);
                }
            } else if (!isBurning()) {
                cookTime = MathHelper.clamp(cookTime - 2, 0, totalCookTime);
            }
            dirty = true;
        } else if (!isBurning() && cookTime > 0) {
            cookTime = MathHelper.clamp(cookTime - 2, 0, totalCookTime);
            dirty = true;
        }

        if (dirty) markDirty();
    }

    private boolean canProcess(MachineRecipe recipe) {
        if (recipe == null) return false;
        ItemStack input = inventory.get(SLOT_INPUT);
        if (input.isEmpty()) return false;
        ItemStack output = inventory.get(SLOT_OUTPUT);
        ItemStack recipeOut = recipe.getOutput();
        if (output.isEmpty()) return true;
        if (output.getItem() != recipeOut.getItem()) return false;
        return output.getCount() + recipeOut.getCount() <= output.getMaxStackSize();
    }

    private void processItem(MachineRecipe recipe) {
        if (recipe == null) return;
        ItemStack input = inventory.get(SLOT_INPUT);
        ItemStack output = inventory.get(SLOT_OUTPUT);
        ItemStack recipeOut = recipe.getOutput();
        input.shrink(1);
        if (input.isEmpty()) inventory.set(SLOT_INPUT, ItemStack.EMPTY);
        if (output.isEmpty()) inventory.set(SLOT_OUTPUT, recipeOut.copy());
        else output.grow(recipeOut.getCount());
    }

    public boolean isBurning() { return burnTime > 0; }

    public static int getItemBurnTime(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        int bt = net.minecraftforge.event.ForgeEventFactory.getItemBurnTime(stack);
        if (bt >= 0) return bt;
        return net.minecraft.tileentity.TileEntityFurnace.getItemBurnTime(stack);
    }

    @Override public int getSizeInventory() { return TOTAL_SLOTS; }
    @Override public boolean isEmpty() { for (ItemStack s : inventory) if (!s.isEmpty()) return false; return true; }
    @Override public ItemStack getStackInSlot(int index) { return inventory.get(index); }
    @Override public ItemStack decrStackSize(int index, int count) { return ItemStackHelper.getAndSplit(inventory, index, count); }
    @Override public ItemStack removeStackFromSlot(int index) { return ItemStackHelper.getAndRemove(inventory, index); }
    @Override public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.set(index, stack);
        if (index == SLOT_INPUT) totalCookTime = 200;
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
    @Override public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == SLOT_OUTPUT) return false;
        if (index == SLOT_FUEL) return getItemBurnTime(stack) > 0 || SlotFurnaceFuel.isBucket(stack);
        return true;
    }
    @Override public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.DOWN) return SLOTS_BOTTOM;
        if (side == EnumFacing.UP) return SLOTS_TOP;
        return SLOTS_SIDE;
    }
    @Override public boolean canInsertItem(int index, ItemStack stack, EnumFacing dir) { return isItemValidForSlot(index, stack); }
    @Override public boolean canExtractItem(int index, ItemStack stack, EnumFacing dir) {
        if (dir == EnumFacing.DOWN && index == SLOT_FUEL) {
            return stack.getItem() == Items.WATER_BUCKET || stack.getItem() == Items.BUCKET;
        }
        return true;
    }
    @Override public String getName() { return "container.compound_extractor"; }
    @Override public boolean hasCustomName() { return false; }
    @Override public String getGuiID() { return "orangesunshine:compound_extractor"; }
    @Override public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container.ContainerCompoundExtractor(playerInventory, this);
    }
    @Override public int getField(int id) {
        switch (id) {
            case 0: return burnTime;
            case 1: return currentBurnTime;
            case 2: return cookTime;
            case 3: return totalCookTime;
            default: return 0;
        }
    }
    @Override public void setField(int id, int value) {
        switch (id) {
            case 0: burnTime = value; break;
            case 1: currentBurnTime = value; break;
            case 2: cookTime = value; break;
            case 3: totalCookTime = value; break;
        }
    }
    @Override public int getFieldCount() { return 4; }
    @Override public void clear() { inventory.clear(); }

    @Override public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, inventory);
        burnTime = compound.getInteger("BurnTime");
        cookTime = compound.getInteger("CookTime");
        totalCookTime = compound.getInteger("CookTimeTotal");
        currentBurnTime = getItemBurnTime(inventory.get(SLOT_FUEL));
    }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("BurnTime", burnTime);
        compound.setInteger("CookTime", cookTime);
        compound.setInteger("CookTimeTotal", totalCookTime);
        ItemStackHelper.saveAllItems(compound, inventory);
        return compound;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, EnumFacing facing) {
        if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == EnumFacing.DOWN) return (T) handlerBottom;
            if (facing == EnumFacing.UP) return (T) handlerTop;
            return (T) handlerSide;
        }
        return super.getCapability(capability, facing);
    }
}
