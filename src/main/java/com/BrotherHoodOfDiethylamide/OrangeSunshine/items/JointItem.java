package com.BrotherHoodOfDiethylamide.OrangeSunshine.items;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.PlayerProperties;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.sounds.ModSounds;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ColorHandlerEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.plaf.SeparatorUI;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JointItem extends DrugItem {
    public JointItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return isEdible() ? this.getItem().getUseDuration(itemStack) : 64;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        playerEntity.getCommandSenderWorld().playSound((PlayerEntity) playerEntity, playerEntity.blockPosition(), ModSounds.JOINT_INHALE.get(), SoundCategory.PLAYERS, 1F, 1F);
        return super.use(world, playerEntity, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, World world, LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerProperties.getPlayerDrugs((PlayerEntity) entity).setSmokeTicks(20);
            world.playSound((PlayerEntity) entity, entity.blockPosition(), ModSounds.JOINT_EXHALE.get(), SoundCategory.PLAYERS, 1F, 1F);
        }

        return super.finishUsingItem(itemStack, world, entity);
    }
}
