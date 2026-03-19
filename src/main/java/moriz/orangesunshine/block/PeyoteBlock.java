/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import com.mojang.serialization.MapCodec;

import moriz.orangesunshine.block.entity.PeyoteBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PeyoteBlock extends SucculentPlantBlock implements EntityBlock {
    public static final MapCodec<PeyoteBlock> CODEC = simpleCodec(PeyoteBlock::new);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    public static final int MAX_AGE = 3;
    private static final VoxelShape[] SHAPES = {
            Block.box(6, 0, 6, 10, 4, 10),
            Block.box(5, 0, 5, 11, 4, 11),
            Block.box(4, 0, 4, 12, 4, 12),
            Block.box(3, 0, 3, 13, 4, 13)
    };

    public PeyoteBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MapCodec<BushBlock> codec() {
        return (MapCodec<BushBlock>)(MapCodec<?>)CODEC;
    }

    @Override
    protected VoxelShape[] getShapes() {
        return SHAPES;
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    protected int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected int getGrowthRate(BlockState state) {
        return state.getValue(getAgeProperty()) < getMaxAge() ? 20 : 120;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PeyoteBlockEntity(pos, state);
    }
}
