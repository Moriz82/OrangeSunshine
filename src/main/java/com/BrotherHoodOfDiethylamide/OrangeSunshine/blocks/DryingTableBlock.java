package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity.TileDryingTable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DryingTableBlock extends Block implements ITileEntityProvider {
    public DryingTableBlock() {
        super(Material.WOOD);
        setRegistryName(OrangeSunshine.MODID, "drying_table");
        setUnlocalizedName(OrangeSunshine.MODID + ".drying_table");
        setCreativeTab(OrangeSunshine.CreativeTab);
        setHardness(2.5F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileDryingTable();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                     EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(OrangeSunshine.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
}
