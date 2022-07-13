package com.BrotherHoodOfDiethylamide.OrangeSunshine.events;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ModBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = OrangeSunshine.MOD_ID)
public class EventHandler {
    @SubscribeEvent
    public static void cutPoppy(PlayerInteractEvent.RightClickBlock event) {
        PlayerEntity player = event.getPlayer();
        Hand hand = event.getHand();
        World world = player.level;
        if (world.isClientSide) return;
        ItemStack itemStack = player.getItemInHand(hand).getStack();
        BlockPos blockPos = event.getHitVec().getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);

        if (blockState.getBlock().is(Blocks.POPPY)) {
            if (itemStack.getItem() == Items.SHEARS) {
                world.playSound(null, blockPos, SoundEvents.SHEEP_SHEAR, SoundCategory.PLAYERS, 1F, 1F);
                world.setBlock(blockPos, ModBlocks.CUT_POPPY_BLOCK.get().defaultBlockState(), 2);
                itemStack.hurtAndBreak(1, player, playerEntity -> playerEntity.broadcastBreakEvent(hand));
                player.swing(hand, true);
            }
        } else if (blockState.getBlock().is(ModBlocks.CUT_POPPY_BLOCK.get()) && blockState.getValue(ModBlocks.CUT_POPPY_BLOCK.get().getAgeProperty()) == 2) {
            if (fillOpiumBottle(player, itemStack)) {
                world.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundCategory.PLAYERS, 1F, 1F);
                world.destroyBlock(blockPos, false, player);
                player.swing(hand, true);
            }
        }
    }

    private static boolean fillOpiumBottle(PlayerEntity player, ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item.equals(Items.GLASS_BOTTLE)) {
            itemStack.shrink(1);
            player.inventory.add(ModItems.OPIUM_BOTTLE_0.get().getDefaultInstance());
            return true;
        } else if (item.equals(ModItems.OPIUM_BOTTLE_0.get())) {
            itemStack.shrink(1);
            player.inventory.add(ModItems.OPIUM_BOTTLE_1.get().getDefaultInstance());
            return true;
        } else if (item.equals(ModItems.OPIUM_BOTTLE_1.get())) {
            itemStack.shrink(1);
            player.inventory.add(ModItems.OPIUM_BOTTLE_2.get().getDefaultInstance());
            return true;
        } else if (item.equals(ModItems.OPIUM_BOTTLE_2.get())) {
            itemStack.shrink(1);
            player.inventory.add(ModItems.OPIUM_BOTTLE_3.get().getDefaultInstance());
            return true;
        }
        return false;
    }

}
