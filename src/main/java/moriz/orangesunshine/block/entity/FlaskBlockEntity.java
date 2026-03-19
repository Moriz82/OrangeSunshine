/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */
package moriz.orangesunshine.block.entity;

import java.util.ArrayList;
import java.util.List;

import moriz.orangesunshine.block.BlockWithFluid;
import moriz.orangesunshine.fluid.FluidVolumes;
import moriz.orangesunshine.fluid.PSFluids;
import moriz.orangesunshine.fluid.container.FluidContainer;
import moriz.orangesunshine.fluid.container.Resovoir;
import moriz.orangesunshine.util.NbtSerialisable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Created by lukas on 25.10.14.
 * Updated by Sollace on 2 Jan 2023
 */
public class FlaskBlockEntity extends SyncedBlockEntity
        implements BlockWithFluid.DirectionalFluidResovoir,
        Resovoir.ChangeListener, WorldlyContainer,
                   SidedStorageBlockEntity {
    private static final int[] NO_SLOT_ID = {};
    private static final int[] INPUT_SLOT_ID = {0};
    private static final int[] OUTPUT_SLOT_ID = {1};

    public static final int FLASK_CAPACITY = FluidVolumes.BUCKET * 8;

    private final Resovoir tank;
    private boolean pendingSync;

    public final IoSlot inputSlot = new IoSlot(0);
    public final IoSlot outputSlot = new IoSlot(1);

    public final IoInventory ioInventory = new IoInventory();
    public final ContainerData propertyDelegate = new SimpleContainerData(getTotalProperties());

    public FlaskBlockEntity(BlockPos pos, BlockState state) {
        this(PSBlockEntities.FLASK, pos, state, FLASK_CAPACITY);
    }

    public FlaskBlockEntity(BlockEntityType<? extends FlaskBlockEntity> type, BlockPos pos, BlockState state, int capacity) {
        super(type, pos, state);
        tank = new Resovoir(capacity, this);
    }

    public void markForUpdate() {
        pendingSync = true;
    }

    protected int getTotalProperties() {
        return 4;
    }

    @Override
    public void onDrain(Resovoir resovoir) {
        onIdle(resovoir);
    }

    @Override
    public void onFill(Resovoir resovoir, int amountFilled) {
        onIdle(resovoir);
    }

    @Override
    public void onIdle(Resovoir resovoir) {
        markForUpdate();
    }

    @Override
    public Resovoir getTank(Direction direction) {
        return tank;
    }

    @Override
    public void tick(ServerLevel level) {
        ItemStack output = outputSlot.getStack();
        boolean playSound = false;
        FluidContainer container = FluidContainer.of(output, null);
        if (container != null && container.getFillPercentage(output) < 1) {
            int oldLevel = container.getLevel(output);
            ioInventory.setItem(1, tank.drain(50, output, outputSlot::incrementLevelsTransferred));
            playSound |= oldLevel != FluidContainer.of(outputSlot.getStack()).getLevel(outputSlot.getStack());
        }

        ItemStack input = inputSlot.getStack();
        container = FluidContainer.of(input, null);
        if (container != null && container.getFillPercentage(input) > 0) {
            int oldLevel = container.getLevel(input);
            ioInventory.setItem(0, tank.deposit(50, input, inputSlot::incrementLevelsTransferred));
            playSound |= oldLevel != FluidContainer.of(inputSlot.getStack()).getLevel(inputSlot.getStack());
        }

        if (playSound && level.getGameTime() % 9 == 0) {
            level.playSound(null, getBlockPos(), SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.025F, 0.5F);
        }

        if (pendingSync) {
            pendingSync = false;
            setChanged();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public List<ItemStack> getDroppedStacks(FluidContainer container) {
        List<ItemStack> stacks = new ArrayList<>();
        ItemStack flaskStack = container.getDefaultStack(PSFluids.EMPTY);
        int maxCapacity = container.getMaxCapacity(flaskStack);
        while (!tank.isEmpty()) {
            stacks.add(tank.drain(maxCapacity, flaskStack));
        }
        return stacks;
    }

    @Override
    protected void writeNbt(CompoundTag compound) {
        super.writeNbt(compound);
        compound.put("tank", tank.toNbt());
        compound.store("items", ItemStack.OPTIONAL_CODEC.listOf(), List.copyOf(ioInventory.getItems()));
        compound.put("inputSlot", inputSlot.toNbt());
        compound.put("outputSlot", outputSlot.toNbt());
    }

    @Override
    protected void readNbt(CompoundTag compound) {
        super.readNbt(compound);
        tank.fromNbt(compound.getCompoundOrEmpty("tank"));
        var items = compound.read("items", ItemStack.OPTIONAL_CODEC.listOf()).orElse(List.of());
        var slots = ioInventory.getItems();
        for (int i = 0; i < slots.size(); i++) {
            slots.set(i, i < items.size() ? items.get(i) : ItemStack.EMPTY);
        }
        inputSlot.fromNbt(compound.getCompoundOrEmpty("inputSlot"));
        outputSlot.fromNbt(compound.getCompoundOrEmpty("outputSlot"));
    }

    @Override
    public int getContainerSize() {
        return ioInventory.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return ioInventory.isEmpty() && tank.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return ioInventory.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ioInventory.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ioInventory.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ioInventory.setItem(slot, stack);
        onContentsExternallyChanged(slot);
    }

    public void onContentsExternallyChanged(int slot) {
        if (slot == 0) {
            inputSlot.onChange();
        }
        if (slot == 1) {
            outputSlot.onChange();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        ioInventory.clearContent();
        tank.clearContent();
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        Direction facing = getBlockState().getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).orElse(Direction.UP);
        if (facing.getAxis() != Direction.Axis.Y && direction.getAxis() != Direction.Axis.Y) {
            direction = Direction.fromYRot(facing.toYRot() - direction.toYRot());
        }

        if (direction == Direction.EAST) {
            return INPUT_SLOT_ID;
        }
        if (direction == Direction.WEST) {
            return OUTPUT_SLOT_ID;
        }
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return new int[] {0, 1};
        }
        return NO_SLOT_ID;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
        int[] availableSlots = getSlotsForFace(direction);

        if (availableSlots.length == 0) {
            return false;
        }

        if (slot == OUTPUT_SLOT_ID[0]) {
            return ioInventory.canPlaceItem(slot, stack) && FluidContainer.of(stack).getFillPercentage(stack) < 1;
        }

        if (slot == INPUT_SLOT_ID[0]) {
            return ioInventory.canPlaceItem(slot, stack) && FluidContainer.of(stack).getFillPercentage(stack) > 0;
        }

        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        int[] availableSlots = getSlotsForFace(direction);

        if (availableSlots.length == 0) {
            return false;
        }

        if (slot == OUTPUT_SLOT_ID[0] && outputSlot.getFillPercentage(-1) >= 1) {
            return true;
        }

        if (slot == INPUT_SLOT_ID[0] && inputSlot.getFillPercentage(1) <= 0) {
            return true;
        }

        return false;
    }

    @Override
    public Storage<FluidVariant> getFluidStorage(Direction side) {
        return getTank(side);
    }

    class IoInventory extends SimpleContainer {
        IoInventory() {
            super(2);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            var container = FluidContainer.of(stack, null);
            return container != null && (slot == 0 ? container.getFillPercentage(stack) > 0 : container.getFillPercentage(stack) < 1);
        }
    }

    public class IoSlot implements NbtSerialisable {
        public final int index;

        private final int levelsTransferredIndex;
        private final int inputtedLevelsIndex;

        public IoSlot(int index) {
            this.index = index;
            levelsTransferredIndex = index * 2;
            inputtedLevelsIndex = 1 + (index * 2);
        }

        public float getProgress() {
            return propertyDelegate.get(inputtedLevelsIndex) == 0 ? 0 : (float)propertyDelegate.get(levelsTransferredIndex) / propertyDelegate.get(inputtedLevelsIndex);
        }

        public void incrementLevelsTransferred(int amount) {
            propertyDelegate.set(levelsTransferredIndex, propertyDelegate.get(levelsTransferredIndex) + amount);
        }

        public void onChange() {
            ItemStack stack = ioInventory.getItem(index);
            var container = FluidContainer.of(stack, null);
            int levels = container == null ? 0 : container.getLevel(stack);
            if (container != null && index == 1) {
                levels = container.getMaxCapacity(stack) - levels;
            }
            propertyDelegate.set(inputtedLevelsIndex, levels);
            propertyDelegate.set(levelsTransferredIndex, 0);
        }

        public ItemStack getStack() {
            return ioInventory.getItem(index);
        }

        public float getFillPercentage(float def) {
            ItemStack stack = getStack();
            var container = FluidContainer.of(stack, null);
            return container == null ? def : container.getFillPercentage(stack);
        }

        @Override
        public void toNbt(CompoundTag compound) {
            compound.putInt("inputtedLevels", propertyDelegate.get(inputtedLevelsIndex));
            compound.putInt("levelsTransferred", propertyDelegate.get(levelsTransferredIndex));
        }

        @Override
        public void fromNbt(CompoundTag compound) {
            propertyDelegate.set(inputtedLevelsIndex, compound.getIntOr("inputtedLevels", 0));
            propertyDelegate.set(levelsTransferredIndex, compound.getIntOr("levelsTransferred", 0));
        }
    }
}
