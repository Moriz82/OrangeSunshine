package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Block_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import java.util.Random;

public class PsychOre extends Block {

    String name;

    public PsychOre(String name, Material material) {
        super(material);
        this.name = name;
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(OrangeSunshine.CreativeTab);
         Block_init.BLOCKS.add(this);
/*         item = new ItemBlock(this).setRegistryName(this.getRegistryName());
        Item_init.ITEMS.add(item);*/
        setHardness(3.0f);
        setResistance(15.0f);
        setHarvestLevel("pickaxe",2);
        setLightLevel(0.0f);
    }

    public void registerItemModel(Item itemBlock) {
        OrangeSunshine.proxy.registerItemRenderer(itemBlock, 0, name);
    }

    public Item createItemBlock() {
        return new ItemBlock(this).setRegistryName(getRegistryName());
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(ComplexBlocks.psychOre);
    }

    @Override
    public int quantityDropped(Random rand) {
        return 1;
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random)
    {
        return 1;
    }

}