package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Random;

public class PsychOre extends Block {
    public PsychOre() {
        super(Material.ROCK);
        setRegistryName(OrangeSunshine.MODID, "psych_ore");
        setUnlocalizedName(OrangeSunshine.MODID + ".psych_ore");
        setCreativeTab(OrangeSunshine.CreativeTab);
        setHardness(3.0f);
        setResistance(15.0f);
        setHarvestLevel("pickaxe", 2);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ModItems.PSYCH_INGOT;
    }

    @Override
    public int quantityDropped(Random rand) {
        return 1;
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        return 1 + random.nextInt(fortune + 1);
    }
}
