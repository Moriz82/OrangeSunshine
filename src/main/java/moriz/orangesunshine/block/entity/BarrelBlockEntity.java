/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block.entity;

import moriz.orangesunshine.fluid.FluidVolumes;
import moriz.orangesunshine.fluid.Processable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class BarrelBlockEntity extends FluidProcessingBlockEntity {

    public int timeFermented;

    public float tapRotation = 0;
    public int timeLeftTapOpen = 0;

    public BarrelBlockEntity(BlockPos pos, BlockState state) {
        super(PSBlockEntities.BARREL, pos, state, FluidVolumes.BARREL, Processable.ProcessType.MATURE);
    }

    @Override
    public void tick(ServerLevel level) {
        super.tick(level);
        tickAnimations();
    }

    public void tickAnimations() {
        if (timeLeftTapOpen > 0) {
            timeLeftTapOpen--;
        }

        if (timeLeftTapOpen > 0 && tapRotation < Mth.HALF_PI) {
            tapRotation += Mth.PI * 0.1F;
        }

        if (timeLeftTapOpen == 0 && tapRotation > 0) {
            tapRotation -= Mth.PI * 0.1F;
        }

        if (timeLeftTapOpen > 0 && timeLeftTapOpen % 5 == 0 && getLevel() != null) {
            getLevel().playSound(null, getBlockPos(), SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.025F, 0.5F);
        }
    }

    @Override
    protected void writeNbt(CompoundTag compound) {
        super.writeNbt(compound);
        compound.putInt("timeLeftTapOpen", timeLeftTapOpen);
        compound.putFloat("tapRotation", tapRotation);
    }

    @Override
    protected void readNbt(CompoundTag compound) {
        super.readNbt(compound);
        timeLeftTapOpen = compound.getIntOr("timeLeftTapOpen", 0);
        tapRotation = compound.getFloatOr("tapRotation", 0);
    }
}
