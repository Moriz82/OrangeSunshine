package com.BrotherHoodOfDiethylamide.OrangeSunshine.items;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.PlayerProperties;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.sounds.ModSounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class JointItem extends DrugItem {
    public JointItem(String name, DrugEffectProperties[] effects) {
        super(name, effects, EnumAction.BOW, 1);
        setMaxDamage(8);
        setMaxStackSize(1);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 64;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        world.playSound(player, player.getPosition(), ModSounds.JOINT_INHALE, SoundCategory.PLAYERS, 1F, 1F);
        ItemStack itemstack = player.getHeldItem(hand);
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            PlayerProperties.getPlayerDrugs((EntityPlayer) entity).setSmokeTicks(20);
            world.playSound((EntityPlayer) entity, entity.getPosition(), ModSounds.JOINT_EXHALE, SoundCategory.PLAYERS, 1F, 1F);
        }
        return super.onItemUseFinish(stack, world, entity);
    }
}
