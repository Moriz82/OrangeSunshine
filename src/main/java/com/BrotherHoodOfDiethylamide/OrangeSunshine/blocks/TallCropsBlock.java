package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.*;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TallCropsBlock extends CropsBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    public static final IntegerProperty HEIGHT = IntegerProperty.create("height",0,2);
    public static final BooleanProperty CROP = BooleanProperty.create("crop");
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

    public TallCropsBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected IItemProvider getBaseSeedId() {
        return ModItems.COCA_SEEDS.get();
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return !isMaxAge(blockState) || !isMaxHeight(blockState);
    }

    @Override
    public void randomTick(BlockState blockState, ServerWorld world, BlockPos blockPos, Random random) {
        if (!world.isAreaLoaded(blockPos, 1)) return;
        if (world.getRawBrightness(blockPos, 0) >= 9) {
            int age = getAge(blockState);
            int height = getHeight(blockState);
            if (age < getMaxAge()) {
                float f = getGrowthSpeed(this, world, blockPos);
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(world, blockPos, blockState, random.nextInt((int)(25.0F / f) + 1) == 0)) {
                    world.setBlock(blockPos, blockState.setValue(getAgeProperty(), age + 1), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(world, blockPos, blockState);
                }
            } else if (height < getMaxHeight() && world.isEmptyBlock(blockPos.above())) {
                float f = getGrowthSpeed(this, world, blockPos);
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(world, blockPos, blockState, random.nextInt((int)(25.0F / f) + 1) == 0)) {
                    world.setBlockAndUpdate(blockPos.above(), blockState.setValue(getAgeProperty(), 0).setValue(getHeightProperty(), getHeight(blockState) + 1));
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(world, blockPos, blockState);
                }
            }
        }

    }

    @Override
    public int getMaxAge() {
        return 3;
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxHeight() {
        return 2;
    }

    public IntegerProperty getHeightProperty() {
        return HEIGHT;
    }

    public int getHeight(BlockState blockState) {
        return blockState.getValue(getHeightProperty());
    }

    public boolean isMaxHeight(BlockState blockState) {
        return blockState.getValue(getHeightProperty()) >= getMaxHeight();
    }

    public BooleanProperty getCropProperty() {
        return CROP;
    }

    public boolean isCrop(BlockState blockState) {
        return blockState.getValue(getCropProperty());
    }

    @Override
    protected int getBonemealAgeIncrease(World world) {
        return super.getBonemealAgeIncrease(world)/3;
    }

    @Override
    public boolean isValidBonemealTarget(IBlockReader blockReader, BlockPos blockPos, BlockState blockState, boolean isClient) {
        return !isMaxAge(blockState) || (!isMaxHeight(blockState) && !blockReader.getBlockState(blockPos.above()).getBlock().equals(this));
    }

    @Override
    public boolean canSurvive(BlockState blockState, IWorldReader worldReader, BlockPos blockPos) {
        if (getHeight(blockState) > 0) {
            BlockState belowState = worldReader.getBlockState(blockPos.below());
            return belowState.getBlock().equals(this) && getHeight(belowState) < getHeight(blockState);
        } else if (!isCrop(blockState)) {
            return worldReader.getBlockState(blockPos.below()).is(Blocks.GRASS_BLOCK);
        } else {
            return super.canSurvive(blockState, worldReader, blockPos);
        }
    }

    @Override
    public void growCrops(World world, BlockPos blockPos, BlockState blockState) {
        if (getAge(blockState) < getMaxAge()) {
            int i = getAge(blockState) + getBonemealAgeIncrease(world);
            int j = getMaxAge();
            if (i > j) {
                i = j;
            }

            world.setBlock(blockPos, blockState.setValue(getAgeProperty(), i), 2);
        } else if (getHeight(blockState) < getMaxHeight() && world.isEmptyBlock(blockPos.above())) {
            world.setBlockAndUpdate(blockPos.above(), blockState.setValue(getAgeProperty(), 0).setValue(getHeightProperty(), getHeight(blockState) + 1));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE);
        builder.add(HEIGHT);
        builder.add(CROP);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, IBlockReader blockReader, BlockPos blockPos, ISelectionContext context) {
        return SHAPE_BY_AGE[blockState.getValue(getAgeProperty())];
    }
}
