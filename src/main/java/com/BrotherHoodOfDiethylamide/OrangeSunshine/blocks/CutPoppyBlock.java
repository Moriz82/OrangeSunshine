package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class CutPoppyBlock extends BlockCrops {
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.625D, 0.6875D);

    public CutPoppyBlock() {
        setRegistryName(OrangeSunshine.MODID, "cut_poppy");
        setUnlocalizedName(OrangeSunshine.MODID + ".cut_poppy");
        setDefaultState(blockState.getBaseState().withProperty(AGE, 0));
    }

    @Override
    public int getMaxAge() { return 3; }

    @Override
    protected PropertyInteger getAgeProperty() { return AGE; }

    @Override
    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        IBlockState below = world.getBlockState(pos.down());
        return below.getBlock() == Blocks.GRASS || below.getBlock() == Blocks.DIRT || below.getBlock() == Blocks.FARMLAND;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
    }

    @Override
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, AGE); }

    @Override
    public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(AGE, meta & 3); }

    @Override
    public int getMetaFromState(IBlockState state) { return state.getValue(AGE); }
}
