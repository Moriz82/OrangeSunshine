package com.BrotherHoodOfDiethylamide.OrangeSunshine.items;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SyringeItem extends DrugItem {
    private final int color;

    public SyringeItem(String name, DrugEffectProperties[] effects, int color) {
        super(name, effects, EnumAction.BOW, 1);
        this.color = color;
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        ItemStack result = super.onItemUseFinish(stack, world, entity);

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (!player.capabilities.isCreativeMode) {
                if (result.isEmpty()) {
                    return new ItemStack(ModItems.EMPTY_SYRINGE);
                }
                player.inventory.addItemStackToInventory(new ItemStack(ModItems.EMPTY_SYRINGE));
            }
        }

        return result;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @SideOnly(Side.CLIENT)
    public int getColor() {
        return color;
    }
}
