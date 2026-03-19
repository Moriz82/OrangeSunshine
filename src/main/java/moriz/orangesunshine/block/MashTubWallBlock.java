/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import java.util.Optional;

import com.mojang.serialization.MapCodec;
import moriz.orangesunshine.block.entity.PSBlockEntities;
import moriz.orangesunshine.block.entity.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Updated by Sollace on 7 Feb 2023
 */
public class MashTubWallBlock extends BaseEntityBlock implements EntityBlock, LiquidBlockContainer {
    public static final MapCodec<MashTubWallBlock> CODEC = simpleCodec(MashTubWallBlock::new);

    public MashTubWallBlock(BlockBehaviour.Properties settings) {
        super(settings.lightLevel(state -> state.getValue(MashTubBlock.LIGHT)));
    }

    @Override
    public MapCodec<? extends MashTubWallBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MashTubBlock.LIGHT);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getValidMasterPosition(world, pos)
                .map(center -> MashTubBlock.COLLISSION_SHAPE.move(center.getX() - pos.getX(), 0, center.getZ() - pos.getZ()))
                .orElseGet(Shapes::empty);
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return MashTubBlock.RAYCAST_SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 1;
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return getValidMasterPosition(world, pos)
                .map(center -> world.getBlockState(center).getCloneItemStack(world, center, includeData))
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        getValidMasterPosition(world, pos).ifPresent(center -> {
            BlockState masterState = world.getBlockState(center);
            masterState.getBlock().animateTick(masterState, world, pos, random);
        });
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, net.minecraft.world.entity.player.Player player, BlockHitResult hit) {
        return getValidMasterPosition(world, pos)
                .map(center -> world.getBlockState(center).useWithoutItem(world, player, new BlockHitResult(hit.getLocation(), hit.getDirection(), center, hit.isInside())))
                .orElse(InteractionResult.PASS);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, net.minecraft.world.entity.player.Player player, InteractionHand hand, BlockHitResult hit) {
        return getValidMasterPosition(world, pos)
                .map(center -> world.getBlockState(center).useItemOn(stack, world, player, hand, new BlockHitResult(hit.getLocation(), hit.getDirection(), center, hit.isInside())))
                .orElse(InteractionResult.PASS);
    }

    @Override
    public boolean canPlaceLiquid(LivingEntity player, BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
        return getValidMasterPosition(world, pos).filter(center -> {
            BlockState masterState = world.getBlockState(center);
            return masterState.getBlock() instanceof LiquidBlockContainer fillable
                    && fillable.canPlaceLiquid(player, world, center, masterState, fluid);
        }).isPresent();
    }

    @Override
    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        return getValidMasterPosition(world, pos).filter(center -> {
            BlockState masterState = world.getBlockState(center);
            return masterState.getBlock() instanceof LiquidBlockContainer fillable
                    && fillable.placeLiquid(world, center, masterState, fluidState);
        }).isPresent();
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        getMasterPosition(world, pos).ifPresent(center -> {
            BlockState masterState = world.getBlockState(center);
            if (masterState.is(PSBlocks.MASH_TUB) || masterState.is(this)) {
                world.destroyBlock(center, true);
            }
        });
        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }

    private Optional<BlockPos> getMasterPosition(BlockGetter world, BlockPos pos) {
        return world.getBlockEntity(pos, PSBlockEntities.MASH_TUB_EDGE)
                .map(MasterPosition::getMasterPos)
                .filter(p -> !p.equals(pos));
    }

    private Optional<BlockPos> getValidMasterPosition(BlockGetter world, BlockPos pos) {
        return getMasterPosition(world, pos)
                .filter(p -> world.getBlockState(p).is(PSBlocks.MASH_TUB));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MasterPosition(pos, state);
    }

    public static class MasterPosition extends SyncedBlockEntity {
        private BlockPos masterPos;

        public MasterPosition(BlockPos pos, BlockState state) {
            super(PSBlockEntities.MASH_TUB_EDGE, pos, state);
            masterPos = pos;
        }

        public void setMasterPos(BlockPos pos) {
            masterPos = pos;
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }

        public BlockPos getMasterPos() {
            return masterPos;
        }

        @Override
        protected void writeNbt(CompoundTag compound) {
            super.writeNbt(compound);
            compound.putLong("masterPos", masterPos.asLong());
        }

        @Override
        protected void readNbt(CompoundTag compound) {
            super.readNbt(compound);
            masterPos = BlockPos.of(compound.getLongOr("masterPos", getBlockPos().asLong()));
        }
    }
}
