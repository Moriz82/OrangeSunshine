/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CannabisPlantBlock extends CropBlock {
    public static final MapCodec<CannabisPlantBlock> CODEC = simpleCodec(CannabisPlantBlock::new);
    public static final int MAX_AGE = 15;
    public static final int MAX_AGE_WHILE_COVERED = 11;

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);

    public static final BooleanProperty GROWING = BooleanProperty.create("growing");
    public static final BooleanProperty NATURAL = BooleanProperty.create("natural");

    public CannabisPlantBlock(BlockBehaviour.Properties settings) {
        super(settings.noOcclusion());
        registerDefaultState(defaultBlockState().setValue(NATURAL, false));
    }

    @Override
    public MapCodec<? extends CannabisPlantBlock> codec() {
        return CODEC;
    }

    public BlockState getStateForHeight(int y) {
        return defaultBlockState();
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return BlockStateProperties.AGE_15;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // Do not call super: CropBlock hardcodes AGE_7, but we use AGE_15.
        // CropBlock.<init> uses virtual getAgeProperty() for registerDefaultState,
        // so we must include getAgeProperty() (AGE_15) here, not the super's AGE_7.
        builder.add(getAgeProperty(), GROWING, NATURAL);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public int getMaxAge(BlockState state) {
        return MAX_AGE;
    }

    public int getMaxHeight() {
        return 3;
    }

    protected float getRandomGrowthChance() {
        return 0.12F;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return asItem();
    }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
        return floor.is(this) || super.mayPlaceOn(floor, world, pos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(NATURAL)) {
            BlockState floor = world.getBlockState(pos.below());
            return floor.is(this) || floor.is(Blocks.GRASS_BLOCK) || floor.is(BlockTags.DIRT);
        }
        return super.canSurvive(state, world, pos);
    }

    public boolean isMature(BlockState state) {
        return state.getValue(getAgeProperty()) >= getMaxAge(state) && !state.getValue(GROWING);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (world.getRawBrightness(pos.above(), 0) >= 9 && random.nextFloat() < getRandomGrowthChance()) {
            if (isValidBonemealTarget(world, pos, state)) {
                applyGrowth(world, pos, state, false);
            }
        }
    }

    public void applyGrowth(Level world, BlockPos pos, BlockState state, boolean bonemeal) {
        int number = bonemeal ? world.random.nextInt(4) + 1 : 1;

        for (int i = 0; i < number; i++) {
            if (state.getValue(getAgeProperty()) < getMaxAge(state)) {
                state = state.cycle(getAgeProperty());
                world.setBlock(pos, state, Block.UPDATE_ALL);
            } else if (canGrowUpwards(world, pos, state)) {
                pos = pos.above();
                state = defaultBlockState();
                world.setBlock(pos, state, Block.UPDATE_ALL);
            }
        }
    }

    protected int getPlantSize(LevelReader world, BlockPos pos) {
        int plantSize = 1;
        while (world.getBlockState(pos.below(plantSize)).is(this)) {
            ++plantSize;
        }
        return plantSize;
    }

    protected boolean canGrowUpwards(Level world, BlockPos pos, BlockState state) {
        return world.isEmptyBlock(pos.above())
                && getPlantSize(world, pos) < getMaxHeight()
                && state.getValue(getAgeProperty()) > MAX_AGE_WHILE_COVERED;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        applyGrowth(world, pos, state, true);
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        if (direction == Direction.UP) {
            return state.setValue(GROWING, neighborState.isAir() && getPlantSize(world, pos) < getMaxHeight());
        }
        return super.updateShape(state, world, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }
}
