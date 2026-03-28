package moriz.orangesunshine.block.entity;

import moriz.orangesunshine.recipe.MixingTableRecipe;
import moriz.orangesunshine.recipe.PSRecipes;
import moriz.orangesunshine.screen.MixingTableScreenHandler;
import moriz.orangesunshine.screen.PSScreenHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public class MixingTableBlockEntity extends SyncedBlockEntity
        implements MenuProvider, ImplementedInventory {

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    protected final ContainerData propertyDelegate = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 1 -> maxProgress = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };
    private int progress;
    private int maxProgress = 72;

    public MixingTableBlockEntity(BlockPos pos, BlockState state) {
        super(PSBlockEntities.MIXING_TABLE_BLOCK_ENTITY, pos, state);
    }

    public ItemStack getRenderStack() {
        return getItem(OUTPUT_SLOT).isEmpty() ? getItem(INPUT_SLOT) : getItem(OUTPUT_SLOT);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Mixing Table");
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(CompoundTag compound) {
        super.writeNbt(compound);
        compound.store("items", ItemStack.OPTIONAL_CODEC.listOf(), List.copyOf(inventory));
        compound.putInt("mixing_table.progress", progress);
    }

    @Override
    protected void readNbt(CompoundTag compound) {
        super.readNbt(compound);
        List<ItemStack> items = compound.read("items", ItemStack.OPTIONAL_CODEC.listOf()).orElse(List.of());
        for (int i = 0; i < inventory.size(); i++) {
            inventory.set(i, i < items.size() ? items.get(i) : ItemStack.EMPTY);
        }
        progress = compound.getIntOr("mixing_table.progress", 0);
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new MixingTableScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    public PSScreenHandlers.BlockPosData getScreenOpeningData() {
        return new PSScreenHandlers.BlockPosData(getBlockPos());
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        if (isOutputSlotEmptyOrReceivable()) {
            if (hasRecipe(level)) {
                increaseCraftProgress();
                setChanged();

                if (hasCraftingFinished()) {
                    craftItem(level);
                    resetProgress();
                }
            } else {
                resetProgress();
            }
        } else {
            resetProgress();
            setChanged();
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem(Level level) {
        Optional<RecipeHolder<MixingTableRecipe>> recipe = getCurrentRecipe(level);
        if (recipe.isEmpty()) {
            return;
        }

        ItemStack result = recipe.get().value().assemble(new SingleRecipeInput(getItem(INPUT_SLOT)), level.registryAccess());
        removeItem(INPUT_SLOT, 1);
        setItem(OUTPUT_SLOT, result.copyWithCount(getItem(OUTPUT_SLOT).getCount() + result.getCount()));
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftProgress() {
        progress++;
    }

    private boolean hasRecipe(Level level) {
        Optional<RecipeHolder<MixingTableRecipe>> recipe = getCurrentRecipe(level);
        if (recipe.isEmpty()) {
            return false;
        }

        ItemStack result = recipe.get().value().assemble(new SingleRecipeInput(getItem(INPUT_SLOT)), level.registryAccess());
        return canInsertAmountIntoOutputSlot(result) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private Optional<RecipeHolder<MixingTableRecipe>> getCurrentRecipe(Level level) {
        return level.getServer().getRecipeManager().getRecipeFor(PSRecipes.MIXING_TABLE_TYPE,
                new SingleRecipeInput(getItem(INPUT_SLOT)), level);
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return getItem(OUTPUT_SLOT).isEmpty() || getItem(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack result) {
        return getItem(OUTPUT_SLOT).getCount() + result.getCount() <= getItem(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return getItem(OUTPUT_SLOT).isEmpty()
                || getItem(OUTPUT_SLOT).getCount() < getItem(OUTPUT_SLOT).getMaxStackSize();
    }
}
