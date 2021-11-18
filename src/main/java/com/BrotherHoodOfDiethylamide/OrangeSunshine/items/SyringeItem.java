package com.BrotherHoodOfDiethylamide.OrangeSunshine.items;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SyringeItem extends DrugItem {
    private final int color;

    public SyringeItem(Item.Properties properties) {
        super(properties);
        this.color = ((Properties)properties).color;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, World world, LivingEntity entity) {
        ItemStack itemStack1 = super.finishUsingItem(itemStack, world, entity);

        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) entity;

            if (!playerEntity.abilities.instabuild) {
                if (itemStack1.isEmpty()) return ModItems.EMPTY_SYRINGE.get().getDefaultInstance();

                playerEntity.inventory.add(ModItems.EMPTY_SYRINGE.get().getDefaultInstance());
            }
        }

        return itemStack1;
    }

    @Override
    public UseAction getUseAnimation(ItemStack itemStack) {
        return UseAction.BOW;
    }

    public int getColor() {
        return color;
    }

    public static class Properties extends DrugItem.Properties {
        private int color = -1;

        public Properties color(int color) {
            this.color = color;
            return this;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Color implements IItemColor {
        @Override
        public int getColor(ItemStack itemStack, int tintIndex) {
            return tintIndex == 0 ? ((SyringeItem)itemStack.getItem()).getColor() : -1;
        }
    }
}
