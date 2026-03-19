/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.influence.DrugInfluence;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

/**
 * Created by calebmanley on 4/05/2014.
 *
 * Updated by Sollace on Jan 1 2023
 */
public class BongItem extends Item {
    public final ArrayList<Consumable> consumables = new ArrayList<>();

    public BongItem(Item.Properties settings) {
        super(settings);
    }

    public BongItem consumes(Consumable consumable) {
        consumables.add(consumable);
        return this;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.TOOT_HORN;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        DrugProperties.of(entity).ifPresent(drugProperties -> {
            getUsedConsumable(drugProperties.asEntity()).ifPresent(consumable -> {
                consumable.getFirst().consume(1, entity);
                drugProperties.addAll(consumable.getSecond().drugInfluences().apply(consumable.getFirst()));
                stack.hurtAndBreak(1, drugProperties.asEntity(), entity.getUsedItemHand());
                drugProperties.startBreathingSmoke(10 + level.random.nextInt(10), consumable.getSecond().smokeColor);
            });
        });

        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!DrugProperties.of(player).isBreathingSmoke() && hasUsableConsumable(player)) {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.FAIL;
    }

    public Optional<Pair<ItemStack, Consumable>> getUsedConsumable(LivingEntity entity) {
        if (!(entity instanceof Player)) {
            return Optional.empty();
        }
        if (entity.getOffhandItem().isEmpty()) {
            return Optional.empty();
        }
        for (Consumable consumable : consumables) {
            if (ItemStack.isSameItem(entity.getOffhandItem(), consumable.consumedItem)) {
                return Optional.of(Pair.of(entity.getOffhandItem(), consumable));
            }
        }
        return Optional.empty();
    }

    public boolean hasUsableConsumable(LivingEntity entity) {
        if (!(entity instanceof Player)) {
            return false;
        }
        for (Consumable consumable : consumables) {
            if (ItemStack.isSameItem(entity.getOffhandItem(), consumable.consumedItem)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 30;
    }

    public record Consumable (
            ItemStack consumedItem,
            Function<ItemStack, List<DrugInfluence>> drugInfluences,
            Vector3f smokeColor
    ) {
        public Consumable(ItemStack consumedItem, DrugInfluence...drugInfluences) {
            this(consumedItem, stack -> List.of(drugInfluences), SmokeableItem.WHITE);
        }

        public Consumable(ItemStack consumedItem, Function<ItemStack, DrugInfluence> drugInfluences) {
            this(consumedItem, stack -> List.of(drugInfluences.apply(stack)), SmokeableItem.WHITE);
        }
    }
}
