package com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.compounds;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.utils.IHasModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class OrangeSunshine extends ItemFood implements IHasModel {

    public OrangeSunshine(String name, int amount, float saturation, boolean isWolfFood) {
        super(amount, saturation, isWolfFood);
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine.CreativeTab);
        this.setAlwaysEdible();

        Item_init.ITEMS.add(this);
    }
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.DRINK;
    }
    @Override
    public void registerModels() {
        com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine.proxy.registerItemRenderer(this, 0, "inventory");
    }
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase player) {
        if(!worldIn.isRemote) {
            //LSD.addEffect(player);
        }
        EntityPlayer plr = (EntityPlayer)player;
        if(!plr.isCreative()){
            stack.shrink(1);
        }
        return(stack);
    }
}

