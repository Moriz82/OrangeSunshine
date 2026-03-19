/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block.entity;

import moriz.orangesunshine.fluid.Processable;
import moriz.orangesunshine.fluid.container.Resovoir;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class FluidProcessingBlockEntity extends FlaskBlockEntity {
    private final Processable.ProcessType processType;

    public FluidProcessingBlockEntity(
            BlockEntityType<? extends FluidProcessingBlockEntity> type,
            BlockPos pos, BlockState state, int capacity,
            Processable.ProcessType processType) {
        super(type, pos, state, capacity);
        this.processType = processType;
    }

    @Override
    protected int getTotalProperties() {
        return super.getTotalProperties() + 2;
    }

    public int getTimeNeeded() {
        return propertyDelegate.get(4);
    }

    public void setTimeNeeded(int value) {
        propertyDelegate.set(4, value);
    }

    public int getTimeProcessed() {
        return propertyDelegate.get(5);
    }

    public void setTimeProcessed(int value) {
        propertyDelegate.set(5, value);
    }

    public float getProgress() {
        if (isActive()) {
            return (float)getTimeProcessed() / getTimeNeeded();
        }
        return 0;
    }

    public Processable.ProcessType getProcessType() {
        return processType;
    }

    public boolean isActive() {
        return getTimeNeeded() != Processable.UNCONVERTABLE;
    }

    @Override
    public void tick(ServerLevel level) {
        super.tick(level);

        Resovoir compliment = getTank(Direction.UP);
        Resovoir tank = getTank(Direction.DOWN);

        if (compliment != tank && !compliment.isEmpty() && tank.isEmpty()) {
            compliment.transferTo(tank);
        }

        if (tank.getFluidType() instanceof Processable p) {
            if (compliment != tank && !compliment.isEmpty() && p.getProcessingTime(tank, Processable.ProcessType.REACT, compliment) > 0) {
                p.process(tank, Processable.ProcessType.REACT, compliment);
            }

            setTimeNeeded(p.getProcessingTime(tank, processType, compliment));

            if (canProcess(level, getTimeNeeded())) {
                if (getTimeProcessed() >= getTimeNeeded()) {
                    onProcessCompleted(level, tank, p.process(tank, processType, compliment));
                } else {
                    setTimeProcessed(getTimeProcessed() + 1);
                }
            } else {
                setTimeProcessed(0);
            }
        } else {
            setTimeNeeded(Processable.UNCONVERTABLE);
        }
    }

    protected boolean canProcess(ServerLevel level, int timeNeeded) {
        return timeNeeded >= 0;
    }

    protected void onProcessCompleted(ServerLevel level, Resovoir tank, ItemStack solids) {
        setTimeProcessed(0);
        setTimeNeeded(Processable.UNCONVERTABLE);
        markForUpdate();
    }

    @Override
    public void onDrain(Resovoir resovoir) {
        if (resovoir.isEmpty()) {
            setTimeProcessed(0);
        }
        super.onDrain(resovoir);
    }

    @Override
    public void onFill(Resovoir resovoir, int amountFilled) {
        super.onFill(resovoir, amountFilled);
        double percentFilled = amountFilled / (double)resovoir.getLevel();
        setTimeNeeded(Mth.floor(getTimeProcessed() * (1 - percentFilled)));
    }

    @Override
    protected void writeNbt(CompoundTag compound) {
        super.writeNbt(compound);
        compound.putInt("timeProcessed", getTimeProcessed());
        compound.putInt("timeNeeded", getTimeNeeded());
    }

    @Override
    protected void readNbt(CompoundTag compound) {
        super.readNbt(compound);
        setTimeProcessed(compound.getIntOr("timeProcessed", 0));
        setTimeNeeded(compound.getIntOr("timeNeeded", Processable.UNCONVERTABLE));
    }
}
