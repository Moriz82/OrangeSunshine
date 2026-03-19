/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import moriz.orangesunshine.fluid.ConsumableFluid;
import moriz.orangesunshine.fluid.FluidVolumes;
import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.fluid.container.FluidContainer;
import moriz.orangesunshine.block.PlacedDrinksBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * Created by Sollace on Jan 1 2023
 */
public class DrinkableItem extends Item implements FluidContainer {
    public static final int FLUID_PER_DRINKING = FluidVolumes.BUCKET / 4;

    private final int capacity;

    private final int consumptionVolume;
    private final int consumptionTime;
    private final ConsumableFluid.ConsumptionType consumptionType;

    public DrinkableItem(Item.Properties settings, int capacity, int consumptionVolume, int consumptionTime, ConsumableFluid.ConsumptionType consumptionType) {
        super(settings.stacksTo(1));
        this.capacity = capacity;
        this.consumptionVolume = Math.min(capacity, consumptionVolume);
        this.consumptionTime = consumptionTime;
        this.consumptionType = consumptionType;
    }

    @Override
    public int getMaxCapacity() {
        return capacity;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return getFluid(stack).isEmpty() ? ItemUseAnimation.NONE
                : consumptionType == ConsumableFluid.ConsumptionType.DRINK
                ? ItemUseAnimation.DRINK
                : ItemUseAnimation.BOW;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        return ConsumableFluid.consume(stack, entity, consumptionVolume, !(entity instanceof Player p && p.getAbilities().instabuild), consumptionType);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (ConsumableFluid.canConsume(stack, player, consumptionVolume, consumptionType)) {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return PlacedDrinksBlock.tryPlace(context);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return consumptionTime;
    }

    @Override
    public Component getName(ItemStack stack) {
        SimpleFluid fluid = getFluid(stack);

        if (!fluid.isEmpty()) {
            return Component.translatable(getDescriptionId() + ".filled", fluid.getName(stack));
        }

        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.translatable("orangesunshine.drink.levels", getLevel(stack), getMaxCapacity(stack)).withStyle(ChatFormatting.GRAY));

        SimpleFluid fluid = getFluid(stack);
        fluid.appendTooltip(stack, null, tooltip, context);
        if (flag.isAdvanced()) {
            tooltip.add(Component.literal("contents: " + fluid.getId()).withStyle(ChatFormatting.DARK_GRAY));
        }
        tooltip.forEach(consumer);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return !getFluid(stack).isEmpty() && getFillPercentage(stack) < 1;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13 * getFillPercentage(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xAAAAFF;
    }
}
