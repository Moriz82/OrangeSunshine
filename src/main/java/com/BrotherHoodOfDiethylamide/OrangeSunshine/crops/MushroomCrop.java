package com.BrotherHoodOfDiethylamide.OrangeSunshine.crops;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Block_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class MushroomCrop extends Crop {
    public MushroomCrop(String name)
    {
        super();
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
        this.setTickRandomly(true);
        this.setSoundType(SoundType.PLANT);

        setUnlocalizedName(name);
        setRegistryName(name);

        Block_init.BLOCKS.add(this);
        //Item_init.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return(Item_init.MUSHROOMCUBENSIS);
    }
    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(Item_init.MUSHROOMCUBENSIS);
    }
    public boolean isOpaqueCube()
    {
        return false;
    }
    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return net.minecraftforge.common.EnumPlantType.Crop;
    }
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
}