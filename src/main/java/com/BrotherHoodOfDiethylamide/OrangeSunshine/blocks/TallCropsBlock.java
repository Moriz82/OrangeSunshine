package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class TallCropsBlock extends BlockCrops {
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);
    public static final PropertyInteger HEIGHT = PropertyInteger.create("height", 0, 2);

    private static final AxisAlignedBB[] AABB_BY_AGE = new AxisAlignedBB[]{
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)
    };

    public TallCropsBlock(String name) {
        setRegistryName(OrangeSunshine.MODID, name);
        setUnlocalizedName(OrangeSunshine.MODID + "." + name);
        setDefaultState(blockState.getBaseState().withProperty(AGE, 0).withProperty(HEIGHT, 0));
    }

    @Override
    public int getMaxAge() {
        return 3;
    }

    public int getMaxHeight() {
        return 2;
    }

    @Override
    protected PropertyInteger getAgeProperty() {
        return AGE;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isAreaLoaded(pos, 1)) return;
        if (world.getLightFromNeighbors(pos.up()) >= 9) {
            int age = getAge(state);
            int height = state.getValue(HEIGHT);
            if (age < getMaxAge()) {
                float f = getGrowthChance(this, world, pos) * 5;
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(world, pos, state, rand.nextInt((int) (25.0F / f) + 1) == 0)) {
                    world.setBlockState(pos, state.withProperty(AGE, age + 1), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(world, pos, state, world.getBlockState(pos));
                }
            } else if (height < getMaxHeight() && world.isAirBlock(pos.up())) {
                float f = getGrowthChance(this, world, pos);
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(world, pos, state, rand.nextInt((int) (25.0F / f) + 1) == 0)) {
                    world.setBlockState(pos.up(), getDefaultState().withProperty(AGE, 0).withProperty(HEIGHT, height + 1));
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(world, pos, state, world.getBlockState(pos.up()));
                }
            }
        }
    }

    @Override
    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        int height = state.getValue(HEIGHT);
        if (height > 0) {
            IBlockState belowState = world.getBlockState(pos.down());
            return belowState.getBlock() == this && belowState.getValue(HEIGHT) < height;
        }
        return super.canBlockStay(world, pos, state);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB_BY_AGE[state.getValue(AGE)];
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AGE, HEIGHT);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        int age = meta & 3;
        int height = (meta >> 2) & 3;
        return getDefaultState().withProperty(AGE, age).withProperty(HEIGHT, height);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AGE) | (state.getValue(HEIGHT) << 2);
    }
}
