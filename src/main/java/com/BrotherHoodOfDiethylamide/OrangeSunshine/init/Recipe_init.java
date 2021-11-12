package com.BrotherHoodOfDiethylamide.OrangeSunshine.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ComplexBlocks.psychOre;

public class Recipe_init {
    public static void init(){
        GameRegistry.addSmelting(Item.getItemFromBlock(psychOre), new ItemStack(Item_init.PSYCHINGOT, 1), 20f);
    }
}
