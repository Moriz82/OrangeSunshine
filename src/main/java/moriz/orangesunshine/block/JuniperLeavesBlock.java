/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import com.mojang.serialization.MapCodec;
import moriz.orangesunshine.item.PSItems;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class JuniperLeavesBlock extends LeavesBlock {
    public static final MapCodec<JuniperLeavesBlock> CODEC = simpleCodec(JuniperLeavesBlock::new);

    public JuniperLeavesBlock(BlockBehaviour.Properties settings) {
        super(0.01F, settings);
    }

    @Override
    public MapCodec<? extends JuniperLeavesBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(PERSISTENT);
    }

    @Override
    protected void randomTick(BlockState state, net.minecraft.server.level.ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);
        if (this == PSBlocks.JUNIPER_LEAVES && random.nextFloat() < 0.01F && !state.getValue(WATERLOGGED)) {
            world.setBlock(pos, PSBlocks.FRUITING_JUNIPER_LEAVES.defaultBlockState()
                    .setValue(DISTANCE, state.getValue(DISTANCE))
                    .setValue(PERSISTENT, state.getValue(PERSISTENT))
                    .setValue(WATERLOGGED, state.getValue(WATERLOGGED)), Block.UPDATE_ALL);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (this == PSBlocks.FRUITING_JUNIPER_LEAVES) {
            if (!world.isClientSide()) {
                Block.popResource(world, pos, PSItems.JUNIPER_BERRIES.getDefaultInstance());
                world.setBlock(pos, PSBlocks.JUNIPER_LEAVES.defaultBlockState()
                        .setValue(DISTANCE, state.getValue(DISTANCE))
                        .setValue(PERSISTENT, state.getValue(PERSISTENT))
                        .setValue(WATERLOGGED, state.getValue(WATERLOGGED)), Block.UPDATE_ALL);
                return InteractionResult.SUCCESS_SERVER;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
    }
}
