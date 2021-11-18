package com.BrotherHoodOfDiethylamide.OrangeSunshine.items;

import net.minecraft.block.ComposterBlock;

public class CompostRegistry {
    public static void register() {
        ComposterBlock.COMPOSTABLES.put(ModItems.COCA_LEAF.get(), 0.3F);
        ComposterBlock.COMPOSTABLES.put(ModItems.COCA_SEEDS.get(), 0.3F);
        ComposterBlock.COMPOSTABLES.put(ModItems.WEED_LEAF.get(), 0.3F);
        ComposterBlock.COMPOSTABLES.put(ModItems.WEED_BUD.get(), 0.5F);
        ComposterBlock.COMPOSTABLES.put(ModItems.WEED_SEEDS.get(), 0.3F);
        ComposterBlock.COMPOSTABLES.put(ModItems.COKE_CAKE.get(), 1.0F);
    }
}
