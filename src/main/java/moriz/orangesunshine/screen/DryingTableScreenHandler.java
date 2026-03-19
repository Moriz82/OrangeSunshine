package moriz.orangesunshine.screen;

import moriz.orangesunshine.block.entity.DryingTableBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
* Created by lukas on 08.11.14.
*/
public class DryingTableScreenHandler extends AbstractContainerMenu {
    private final DryingTableBlockEntity blockEntity;

    private final ContainerData properties;

    public DryingTableScreenHandler(int syncId, Inventory inventory, PSScreenHandlers.BlockPosData data) {
        this(syncId, inventory, (DryingTableBlockEntity)inventory.player.level().getBlockEntity(data.pos()));
    }

    public DryingTableScreenHandler(int syncId, Inventory inventory, DryingTableBlockEntity container) {
        super(PSScreenHandlers.DRYING_TABLE, syncId);
        this.blockEntity = container;
        this.properties = container.propertyDelegate;

        addSlot(new SlotDryingTableResult(inventory.player, container, 0, 124, 35));

        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                this.addSlot(new Slot(container, 1 + x * 3 + y, 30 + x * 18, 17 + y * 18));
            }
        }

        int var3;

        for (var3 = 0; var3 < 3; ++var3) {
            for (int var4 = 0; var4 < 9; ++var4) {
                this.addSlot(new Slot(inventory, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
            }
        }

        for (var3 = 0; var3 < 9; ++var3) {
            this.addSlot(new Slot(inventory, var3, 8 + var3 * 18, 142));
        }

        this.addDataSlots(properties);
    }

    public float getHeatRatio() {
        return properties.get(0) / 1000F;
    }

    public float getProgress() {
        return properties.get(1) / 1000F;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack originalStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            originalStack = stack.copy();

            if (index < 10) {
                if (!moveItemStackTo(stack, 10, 46, false)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stack, originalStack);
            } else if (index >= 10 && index < 37) {
                if (!moveItemStackTo(stack, 37, 46, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (index >= 37 && index < 46) {
                if (!moveItemStackTo(stack, 10, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 10, 37, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == originalStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, originalStack);
        }

        return originalStack;
    }
}
