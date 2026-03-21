package com.BrotherHoodOfDiethylamide.OrangeSunshine.items;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;

public class ModArmorMaterial {
    public static final ItemArmor.ArmorMaterial PSYCH_ARMOR_MATERIAL = EnumHelper.addArmorMaterial(
            "PSYCH",
            OrangeSunshine.MODID + ":psych",
            40, // durability
            new int[]{4, 7, 9, 4}, // reductionAmounts
            25, // enchantability
            SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
            3.0F // toughness
    );
}
