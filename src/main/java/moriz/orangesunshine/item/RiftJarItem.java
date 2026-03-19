/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import moriz.orangesunshine.block.entity.RiftJarBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class RiftJarItem extends BlockItem {
    public RiftJarItem(Block block, Item.Properties settings) {
        super(block, settings);
    }

    public static ItemStack createFilledRiftJar(float riftFraction, Item item) {
        ItemStack stack = item.getDefaultInstance();
        if (riftFraction > 0) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putFloat("riftFraction", riftFraction));
        }
        return stack;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, Player player, ItemStack stack, BlockState state) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof RiftJarBlockEntity jar) {
            jar.currentRiftFraction = getRiftFraction(stack);
        }
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, consumer, flag);
        float fillAmount = getRiftFraction(stack);
        consumer.accept(Component.translatable(getDescriptionId() + "." + getUnlocalizedFractionName(fillAmount)).withStyle(ChatFormatting.GRAY));
    }

    public float getRiftFraction(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.getFloatOr("riftFraction", 0);
    }

    private static String getUnlocalizedFractionName(float fraction) {
        if (fraction <= 0) {
            return "empty";
        }
        if (fraction < 0.4F) {
            return "slightly_filled";
        }
        if (fraction < 0.6F) {
            return "half_filled";
        }
        if (fraction < 0.8F) {
            return "filled";
        }

        return "over_filled";
    }
}
