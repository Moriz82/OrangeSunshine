/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import java.util.List;

import org.joml.Vector3f;

import moriz.orangesunshine.block.PlacedDrinksBlock;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.influence.DrugInfluence;
import moriz.orangesunshine.particle.ExhaledSmokeParticleEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.phys.AABB;

public class SmokeableItem extends Item {
    public static Vector3f WHITE = new Vector3f(1, 1, 1);

    private final List<DrugInfluence> drugEffects;

    private final Vector3f smokeColor;

    private final int useStages;

    public SmokeableItem(Item.Properties settings, int useStages, Vector3f smokeColor, DrugInfluence... drugEffects) {
        super(settings);
        this.smokeColor = smokeColor;
        this.useStages = useStages;
        this.drugEffects = List.of(drugEffects);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 25;
    }

    public int getStages() {
        return useStages;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.TOOT_HORN;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        DrugProperties.of(entity).ifPresent(drugProperties -> {
            drugProperties.addAll(drugEffects);
            drugProperties.startBreathingSmoke(10 + level.random.nextInt(10), smokeColor);
        });

        if (!(entity instanceof Player player && player.getAbilities().instabuild)) {
            if (!stack.isDamageableItem()) {
                stack.consume(1, entity);
            } else {
                stack.hurtAndBreak(1, entity, entity.getUsedItemHand());
            }
        }

        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return PlacedDrinksBlock.tryPlace(context);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!DrugProperties.of(player).isBreathingSmoke()) {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.FAIL;
    }

    public void onIncinerated(ItemStack stack, Level level, BlockPos pos, AbstractFurnaceBlockEntity furnace) {
        level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(3), EntitySelector.NO_SPECTATORS).forEach(player -> {
            DrugProperties.of(player).addAll(drugEffects);
        });

        var effect = new ExhaledSmokeParticleEffect(smokeColor, 1);
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        for (int i = 0; i < 30; i++) {
            serverLevel.sendParticles(effect,
                    level.random.nextGaussian() * 0.3 + pos.getX() + 0.5,
                    pos.getY() + 1,
                    level.random.nextGaussian() * 0.3 + pos.getZ() + 0.5,
                    1,
                    level.random.nextGaussian() * 0.3,
                    level.random.nextGaussian() * 0.3 + 0.8,
                    level.random.nextGaussian() * 0.3,
                    0.001F);
        }
    }
}
