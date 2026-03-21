package com.BrotherHoodOfDiethylamide.OrangeSunshine.items;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ClearDrugItem extends SyringeItem {
    public ClearDrugItem(String name, int color) {
        super(name, new DrugEffectProperties[0], color);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        ItemStack result = super.onItemUseFinish(stack, world, entity);

        if (entity instanceof EntityPlayer) {
            Drug.clearDrugs((EntityPlayer) entity);
        }

        return result;
    }
}
