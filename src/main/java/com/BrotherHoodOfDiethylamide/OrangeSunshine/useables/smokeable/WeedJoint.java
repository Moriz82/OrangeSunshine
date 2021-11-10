package com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.smokeable;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.effects.particles.Smoke;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.utils.IHasModel;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

public class WeedJoint extends ItemFood implements IHasModel {

    boolean isLit = false;

    public WeedJoint(String name, int amount, float saturation, boolean isWolfFood) {
        super(amount, saturation, isWolfFood);
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine.CreativeTab);
        this.setAlwaysEdible();

        this.setMaxDamage(8);
        this.setMaxStackSize(1);

        Item_init.ITEMS.add(this);
    }
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.BOW;
    }
    @Override
    public void registerModels() {
       /* ModelResourceLocation[] stages =
                {
                        new ModelResourceLocation(getRegistryName(), "inventory"),
                        new ModelResourceLocation(getRegistryName() + "_use", "inventory"),
                };

        ModelBakery.registerItemVariants(this,
                stages[0],
                stages[1]
        );
        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return stages[getIsLit()==false? 0 : 1];
            }
        });*/
        OrangeSunshine.proxy.registerItemRenderer(this, 0, "inventory");
    }
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase player) {
        Smoke.doEffect(player, player.world);
        if(!worldIn.isRemote) {
            //LSD.addEffect(player);
        }
        EntityPlayer plr = (EntityPlayer)player;
        stack.damageItem(1, player);
        return(stack);
    }
    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count)
    {
        Smoke.doEffect(player, player.world);
    }
    public boolean getIsLit(){
        return isLit;
    }
}
