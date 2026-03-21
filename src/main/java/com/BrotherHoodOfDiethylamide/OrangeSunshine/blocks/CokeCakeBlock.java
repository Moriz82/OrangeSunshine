package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugInstance;
import net.minecraft.block.BlockCake;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CokeCakeBlock extends BlockCake {
    public CokeCakeBlock() {
        setRegistryName(OrangeSunshine.MODID, "coke_cake");
        setUnlocalizedName(OrangeSunshine.MODID + ".coke_cake");
        setCreativeTab(OrangeSunshine.CreativeTab);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                     EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        boolean result = super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
        if (result && !world.isRemote) {
            Drug cocaine = Drug.byName("cocaine");
            if (cocaine != null) {
                Drug.addDrug(player, new DrugInstance(cocaine, 100, 0.15F, 2000));
            }
        }
        return result;
    }
}
