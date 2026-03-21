package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Random;

public class TobaccoBlock extends TallCropsBlock {
    public TobaccoBlock() {
        super("tobacco_crop");
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return isMaxAge(state) ? ModItems.TOBACCO : ModItems.TOBACCO_SEEDS;
    }
}
