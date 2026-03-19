/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import moriz.orangesunshine.entity.MolotovCocktailEntity;
import moriz.orangesunshine.fluid.Combustable;
import moriz.orangesunshine.fluid.ConsumableFluid;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

public class MolotovCocktailItem extends BottleItem {
    public MolotovCocktailItem(Item.Properties settings, int capacity) {
        super(settings, capacity, 0, ConsumableFluid.ConsumptionType.DRINK);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 7200;
    }

    @Override
    public boolean useOnRelease(ItemStack stack) {
        return true;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity user, int timeLeft) {
        float strength = timeLeft / (float)getUseDuration(stack, user);
        level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 0.5f, 0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f));

        if (!level.isClientSide()) {
            MolotovCocktailEntity projectile = new MolotovCocktailEntity(level, user);
            projectile.setItem(stack);
            projectile.shootFromRotation(user, user.getXRot(), user.getYRot(), 0, 0.5F * strength, 1F);
            level.addFreshEntity(projectile);
        }

        if (user instanceof Player player) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return true;
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {
        user.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (getFluid(stack).isEmpty()) {
            return Component.translatable(getDescriptionId() + ".empty");
        }

        return Component.translatable(getDescriptionId() + ".quality." + getQuality(stack));
    }

    private int getQuality(ItemStack stack) {
        if (getFluid(stack) instanceof Combustable exploding) {
            float explStr = exploding.getExplosionStrength(stack) * 0.8f;
            float fireStr = exploding.getFireStrength(stack) * 0.6f;

            return Mth.clamp(Mth.floor((fireStr + explStr) + 0.5f), 0, 7);
        }

        return 0;
    }
}
