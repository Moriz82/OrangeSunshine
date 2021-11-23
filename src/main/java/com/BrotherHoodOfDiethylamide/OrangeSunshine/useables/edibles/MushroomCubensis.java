package com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.edibles;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugInfluence;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.DrugSimple;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.utils.IHasModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MushroomCubensis extends DrugSimple implements IHasModel {

    DrugInfluence influence = new DrugInfluence("regular_shrooms", 15, 0.005, 0.003, 0.5f);

    public MushroomCubensis(double decSpeed, double decSpeedPlus, String name, int amount, float saturation, boolean isWolfFood) {
        super(decSpeed, decSpeedPlus, amount, saturation, isWolfFood);
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(OrangeSunshine.CreativeTab);
        this.setAlwaysEdible();
        Item_init.ITEMS.add(this);
    }
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.EAT;
    }
    @Override
    public void registerModels() {
        OrangeSunshine.proxy.registerItemRenderer(this, 0, "inventory");
    }
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase player) {

        DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) player);
        if (drugProperties != null)
            drugProperties.addToDrug(influence.clone());

        EntityPlayer plr = (EntityPlayer)player;
        if(!plr.isCreative()){
            stack.shrink(1);
        }
        return(stack);
    }
    @Override
    public float colorHallucinationStrength()
    {
        return (float) getActiveValue() * 0.8f;
    }

    @Override
    public float movementHallucinationStrength()
    {
        return (float) getActiveValue() * 1.0f;
    }

    @Override
    public float contextualHallucinationStrength()
    {
        return (float) getActiveValue() * 0.35f;
    }

    @Override
    public float viewWobblyness()
    {
        return (float) getActiveValue() * 0.03f;
    }
}
