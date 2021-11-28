package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container.FridgeContainer;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
                List<Item> slot0 = new ArrayList<>(Arrays.asList(
                        ModItems.WEED_BUD.get(),
                        ModItems.RED_SHROOMS.get(),
                        ModItems.BROWN_SHROOMS.get(),
                        ModItems.WEED_LEAF.get()
                ));
                switch (slot) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        return  slot0.contains(stack.getItem());
                    default:
                        return true;
                }
            }

            @Override
            public int getSlotLimit(int slot) {
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

    @Override
    public void tick() {
        if (container != null){
            ItemStack item = null;
            boolean notGood = true;
            int amnt = 0;

            for (int i = 0; i < 9; i++) {
                if (!container.slots.get(i).getItem().isEmpty() && item == null) {
                    notGood = false;
                    item = container.slots.get(i).getItem();
                    amnt++;
                }else if (!container.slots.get(i).getItem().isEmpty()) {
                    amnt++;
                    if (item != null && !item.getItem().equals(container.slots.get(i).getItem().getItem())){
                        notGood = true;
                        break;
                    }
                }
            }

            if (!notGood && container.slots.get(9).getItem().getCount() < 64){
                container.isWorking = true;
                currTick++;
                if (currTick >= requiredTicks){
                    currTick=0;
                    container.slots.get(9).set(new ItemStack(FridgeContainer.output.get(item.getItem()).getItem(), amnt));
                    for (int i = 0; i < 9; i++) {
                        container.slots.get(i).getItem().setCount(0);
                        container.slots.get(i).setChanged();
                    }
                    container.isWorking = false;
                    container.slots.get(0).setChanged();
                }
            }else {
                container.isWorking = false;
                container.slots.get(0).setChanged();
            }
        }
    }
}
