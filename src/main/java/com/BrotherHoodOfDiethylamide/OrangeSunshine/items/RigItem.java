package com.BrotherHoodOfDiethylamide.OrangeSunshine.items;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.PlayerProperties;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugInstance;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.sounds.ModSounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class RigItem extends Item {
    public static final Map<Item, DrugItem.DrugEffectProperties[]> RIGABLES = new HashMap<>();

    public RigItem(String name) {
        setRegistryName(OrangeSunshine.MODID, name);
        setUnlocalizedName(OrangeSunshine.MODID + "." + name);
        setCreativeTab(OrangeSunshine.CreativeTab);
        setMaxDamage(32);
        setMaxStackSize(1);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return 0x4287F5;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (hand == EnumHand.MAIN_HAND && !itemStack.isEmpty() && itemStack.getItemDamage() < itemStack.getMaxDamage()) {
            ItemStack offhandItem = player.getHeldItem(EnumHand.OFF_HAND);
            if (!offhandItem.isEmpty() && RIGABLES.containsKey(offhandItem.getItem())) {
                player.setActiveHand(hand);
                return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
            }
            return new ActionResult<>(EnumActionResult.FAIL, itemStack);
        }
        return new ActionResult<>(EnumActionResult.PASS, itemStack);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            PlayerProperties.getPlayerDrugs(player).setSmokeTicks(20);
            ItemStack offhandItem = player.getHeldItem(EnumHand.OFF_HAND);

            DrugItem.DrugEffectProperties[] effects = RIGABLES.get(offhandItem.getItem());
            if (effects != null) {
                for (DrugItem.DrugEffectProperties prop : effects) {
                    Drug drug = Drug.byName(prop.drugName);
                    if (drug != null) {
                        Drug.addDrug(player, new DrugInstance(drug, prop.delayTick, prop.potencyPercentage, prop.duration));
                    }
                }
            }

            if (!player.capabilities.isCreativeMode) {
                stack.damageItem(1, player);
                offhandItem.shrink(1);
            }

            world.playSound(player, player.getPosition(), ModSounds.BONG_HIT, SoundCategory.PLAYERS, 1F, 1F);
        }
        return stack;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }
}
