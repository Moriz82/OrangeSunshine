package com.BrotherHoodOfDiethylamide.OrangeSunshine.items;

import net.minecraftforge.common.util.EnumHelper;

public class ModItemTier {
    public static final net.minecraft.item.Item.ToolMaterial PSYCH_TOOL = EnumHelper.addToolMaterial(
            "PSYCH",
            4,      // harvestLevel
            10000,  // maxUses
            20.0F,  // efficiency
            8.0F,   // attackDamage
            25      // enchantability
    );
}
