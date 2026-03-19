/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class SucculentPlantBlock extends BushBlock implements BonemealableBlock {
    public SucculentPlantBlock(BlockBehaviour.Properties settings) {
        super(settings.offsetType(BlockBehaviour.OffsetType.XZ));
        registerDefaultState(defaultBlockState().setValue(getAgeProperty(), 0));
    }

    protected abstract IntegerProperty getAgeProperty();

    protected abstract int getMaxAge();

    protected abstract int getGrowthRate(BlockState state);

    protected abstract VoxelShape[] getShapes();

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(getAgeProperty());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Vec3 offset = state.getOffset(pos);
        return getShapes()[state.getValue(getAgeProperty())].move(offset.x, offset.y, offset.z);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
        return floor.isFaceSturdy(world, pos, Direction.UP, SupportType.CENTER);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(getAgeProperty()) < getMaxAge();
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (isValidBonemealTarget(world, pos, state) && random.nextInt(getGrowthRate(state)) == 0) {
            applyGrowth(world, random, pos, state, false);
        }
    }

    public void applyGrowth(Level world, RandomSource random, BlockPos pos, BlockState state, boolean bonemeal) {
        if (state.getValue(getAgeProperty()) < getMaxAge()) {
            world.setBlock(pos, state.cycle(getAgeProperty()), Block.UPDATE_ALL);
            return;
        }

        BlockPos.MutableBlockPos plantingPos = new BlockPos.MutableBlockPos();
        int i = 0;
        do {
            plantingPos.set(pos);
            plantingPos.move(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

            if (world.isEmptyBlock(plantingPos) && canSurvive(defaultBlockState(), world, plantingPos)) {
                world.setBlock(plantingPos, defaultBlockState(), Block.UPDATE_ALL);
                return;
            }
        } while (++i < 4);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return state.getValue(getAgeProperty()) < getMaxAge();
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        applyGrowth(world, random, pos, state, true);
    }
}
