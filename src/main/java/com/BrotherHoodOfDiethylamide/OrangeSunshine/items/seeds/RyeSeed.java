package com.BrotherHoodOfDiethylamide.OrangeSunshine.items.seeds;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Block_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.utils.IHasModel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

public class RyeSeed extends Item implements IHasModel, IPlantable {
    public RyeSeed(String name) {
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(OrangeSunshine.CreativeTab);

        Item_init.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        OrangeSunshine.proxy.registerItemRenderer(this, 0, "inventory");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = worldIn.getBlockState(pos);
        if(facing == EnumFacing.UP && player.canPlayerEdit(pos, facing, stack) && state.getBlock() == Blocks.FARMLAND && worldIn.isAirBlock(pos.up())) {
            worldIn.setBlockState(pos.up(), Block_init.RyeCrop.getDefaultState());
            EntityPlayer plr = (EntityPlayer)player;
            if(!plr.isCreative()){
                stack.shrink(1);
            }
            return(EnumActionResult.SUCCESS);
        }else {
            return(EnumActionResult.FAIL);
        }
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return net.minecraftforge.common.EnumPlantType.Crop;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return null;
    }
}