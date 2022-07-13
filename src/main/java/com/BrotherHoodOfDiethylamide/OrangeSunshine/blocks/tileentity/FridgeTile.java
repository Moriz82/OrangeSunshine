package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container.FridgeContainer;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.DryingTableRecipe;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.FridgeRecipe;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.ModRecipeTypes;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class FridgeTile extends TileEntity implements ITickableTileEntity {

    private final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    public FridgeContainer container = null;
    public int requiredTicks = 1000;
    public int currTick = 0;
    public FridgeTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public FridgeTile() {
        this(ModTileEntities.FRIDGE_TILE.get());
    }


    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        itemHandler.deserializeNBT(nbt.getCompound("inv"));
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.put("inv", itemHandler.serializeNBT());
        return super.save(compound);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(10) {
            @Override
            protected void onContentsChanged(int slot) {
                //mark dirty
                super.onContentsChanged(slot);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == 9 ) {
                    List<FridgeRecipe> recipes =  level.getRecipeManager()
                            .getAllRecipesFor (ModRecipeTypes.FRIDGE_RECIPE);

                    List<ItemStack> outputs = new ArrayList<>();

                    for (FridgeRecipe recipe: recipes) {
                        if (ItemStack.isSame(recipe.getResultItem(), stack)){
                            return true;
                        }
                    }

                    return false;
                }
                return true;
            }

            @Override
            public int getSlotLimit(int slot) {
                if (slot == 9) {return 64;}
                return 1;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(!isItemValid(slot, stack)) {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }

        return super.getCapability(cap, side);
    }

    public void craft(){

        if (container.slots.get(9).getItem().getCount() >= 64) {return;}

        Inventory inv = new Inventory(9);
        for (int i = 0; i < 9; i++) {
            if (!container.slots.get(i).getItem().getDisplayName().getString().equals("[Air]")) {
                inv.setItem(i, container.slots.get(i).getItem());
            }
        }

        List<FridgeRecipe> recipes =  level.getRecipeManager()
                .getAllRecipesFor (ModRecipeTypes.FRIDGE_RECIPE);


        for (FridgeRecipe recipe : recipes) {

            if (recipe.matches(inv, level)) {
                container.isWorking = (double) currTick / requiredTicks;
                currTick++;

                if (currTick >= requiredTicks) {
                    currTick = 0;
                    ItemStack output = recipe.getResultItem();
                    container.isWorking = 0.0;
                    craftTheItem(output);
                }
                return;
            }
        }

        container.isWorking = 0.0;
    }

    private void craftTheItem(ItemStack output) {
        int count = 1;
        ItemStack stack = new ItemStack(Items.AIR);
        int num = 0;
        for (int i = 1; i < 9; i++) {
            if (!container.slots.isEmpty() && !container.slots.get(i).getItem().getDisplayName().getString().equals("[Air]")){
                stack = container.slots.get(i).getItem();
                num = i;
                break;
            }
        }

        for (int i = 1; i < 9; i++) {
            if (num != i && (!container.slots.isEmpty() && !container.slots.get(i).getItem().getDisplayName().getString().equals("[Air]")) &&
                    ItemStack.isSame(stack, container.slots.get(i).getItem())){
                count++;
            }
        }

        itemHandler.extractItem(0, 1, false);
        itemHandler.extractItem(1, 1, false);
        itemHandler.extractItem(2, 1, false);
        itemHandler.extractItem(3, 1, false);
        itemHandler.extractItem(4, 1, false);
        itemHandler.extractItem(5, 1, false);
        itemHandler.extractItem(6, 1, false);
        itemHandler.extractItem(7, 1, false);
        itemHandler.extractItem(8, 1, false);

        itemHandler.insertItem(9, new ItemStack(output.getItem(), count), false);
    }

    @Override
    public void tick() {
        if (container != null) {
            craft();
        }
    }
}
