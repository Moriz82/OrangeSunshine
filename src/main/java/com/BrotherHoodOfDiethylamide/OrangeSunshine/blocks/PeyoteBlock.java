package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class PeyoteBlock extends BlockCrops {
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);

    private final Item cropItem;
    private final Item seedItem;

    public PeyoteBlock() {
        this("peyote_crop",
                com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems.PEYOTE,
                com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems.PEYOTE_SEEDS);
    }

    protected PeyoteBlock(String name, Item cropItem, Item seedItem) {
        this.cropItem = cropItem;
        this.seedItem = seedItem;
        setRegistryName(OrangeSunshine.MODID, name);
        setUnlocalizedName(OrangeSunshine.MODID + "." + name);
        setDefaultState(blockState.getBaseState().withProperty(AGE, 0));
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return isMaxAge(state) ? cropItem : seedItem;
    }

    @Override
    public int getMaxAge() { return 3; }

    @Override
    protected PropertyInteger getAgeProperty() { return AGE; }

    @Override
    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        IBlockState below = world.getBlockState(pos.down());
        return below.getBlock() == Blocks.SAND || below.getBlock() == Blocks.FARMLAND;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AGE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(AGE, meta & 3); }

    @Override
    public int getMetaFromState(IBlockState state) { return state.getValue(AGE); }
}
