package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Random;

public class AyahuascaBlock extends TallCropsBlock {
    public AyahuascaBlock() {
        super("ayahuasca_crop");
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ModItems.ROOT_BARK;
    }
}
