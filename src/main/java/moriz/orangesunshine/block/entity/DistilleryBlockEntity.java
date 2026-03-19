/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block.entity;

import moriz.orangesunshine.block.DistilleryBlock;
import moriz.orangesunshine.block.MashTubWallBlock;
import moriz.orangesunshine.fluid.*;
import moriz.orangesunshine.fluid.container.FluidContainer;
import moriz.orangesunshine.fluid.container.Resovoir;
import moriz.orangesunshine.fluid.Processable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Created by lukas on 25.10.14.
 */
public class DistilleryBlockEntity extends FluidProcessingBlockEntity {
    public static final int DISTILLERY_CAPACITY = FlaskBlockEntity.FLASK_CAPACITY;

    public DistilleryBlockEntity(BlockPos pos, BlockState state) {
        super(PSBlockEntities.DISTILLERY, pos, state, DISTILLERY_CAPACITY, Processable.ProcessType.DISTILL);
    }

    @Override
    protected boolean canProcess(ServerLevel world, int timeNeeded) {
        return super.canProcess(world, timeNeeded)
                && getFacing().getAxis() != Axis.Y
                && DistilleryBlock.canConnectTo(world.getBlockState(getOutputPos()), getFacing())
                && getOutput(world) instanceof FlaskBlockEntity;
    }

    @Override
    protected void onProcessCompleted(ServerLevel world, Resovoir tank, ItemStack results) {
        BlockPos pos = getBlockPos();
        world.sendParticles(ParticleTypes.CLOUD,
                pos.getX() + world.getRandom().triangle(0.5F, 0.5F),
                pos.getY() + 0.6F,
                pos.getZ() + world.getRandom().triangle(0.5F, 0.5F),
                2, 0, 0, 0, 0);
        if (world.getRandom().nextInt(10) == 0) {
            world.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.25F, 0.02F);
        }

        BlockPos outputPos = getOutputPos();
        if (getOutput(world) instanceof FlaskBlockEntity destination) {
            ItemStack overflow = destination.getTank(getFacing().getOpposite()).deposit(results);
            if (FluidContainer.of(overflow).getLevel(overflow) > 0) {
                Block.popResource(world, outputPos, overflow);
            }
        } else {
            Block.popResource(world, outputPos, results);
        }
        super.onProcessCompleted(world, tank, results);
    }

    private BlockEntity getOutput(ServerLevel world) {
        BlockPos pos = getOutputPos();
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof MashTubWallBlock f) {
            pos = world.getBlockEntity(pos, PSBlockEntities.MASH_TUB_EDGE).map(p -> p.getMasterPos()).orElse(pos);
        }
        return world.getBlockEntity(pos);
    }

    private BlockPos getOutputPos() {
        return getBlockPos().relative(getFacing());
    }

    private Direction getFacing() {
        return getBlockState().getValue(DistilleryBlock.FACING);
    }
}
