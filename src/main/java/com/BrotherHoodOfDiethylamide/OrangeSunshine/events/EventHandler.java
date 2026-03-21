package com.BrotherHoodOfDiethylamide.OrangeSunshine.events;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.CutPoppyBlock;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ModBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.RecipeInit;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class EventHandler {
    private static final Random rand = new Random();

    public EventHandler() {
        RecipeInit.registerAll();
    }

    /** Shears on a red poppy -> replace with CutPoppyBlock at age 0 */
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        EntityPlayer player = event.getEntityPlayer();
        ItemStack held = player.getHeldItemMainhand();
        IBlockState state = world.getBlockState(pos);

        // Cut poppy with shears
        if (held.getItem() instanceof ItemShears && state.getBlock() == Blocks.RED_FLOWER) {
            if (!world.isRemote) {
                world.setBlockState(pos, ModBlocks.CUT_POPPY_BLOCK.getDefaultState());
                held.damageItem(1, player);
                event.setCanceled(true);
                event.setResult(net.minecraftforge.fml.common.eventhandler.Event.Result.DENY);
            }
            return;
        }

        // Collect opium from CutPoppyBlock with glass bottle
        if (state.getBlock() == ModBlocks.CUT_POPPY_BLOCK && held.getItem() == Items.GLASS_BOTTLE) {
            if (!world.isRemote) {
                int age = state.getValue(CutPoppyBlock.AGE);
                if (age >= 3) {
                    Item bottleItem = getOpiumBottle(0);
                    if (bottleItem != null) {
                        ItemStack bottle = new ItemStack(bottleItem);
                        if (!player.inventory.addItemStackToInventory(bottle)) {
                            player.dropItem(bottle, false);
                        }
                        held.shrink(1);
                        world.setBlockState(pos, state.withProperty(CutPoppyBlock.AGE, 0));
                    }
                    event.setCanceled(true);
                }
            }
            return;
        }

        // Shovel on wood/log -> root bark
        if (held.getItem() instanceof ItemSpade && state.getBlock() instanceof BlockLog) {
            if (!world.isRemote) {
                if (rand.nextFloat() < 0.3f) {
                    ItemStack bark = new ItemStack(ModItems.ROOT_BARK);
                    if (!player.inventory.addItemStackToInventory(bark)) {
                        player.dropItem(bark, false);
                    }
                    held.damageItem(1, player);
                }
                event.setCanceled(true);
            }
        }
    }

    /** Tall grass drops weed/coca seeds randomly */
    @SubscribeEvent
    public void onBlockHarvest(BlockEvent.HarvestDropsEvent event) {
        IBlockState state = event.getState();
        if (state.getBlock() instanceof BlockTallGrass) {
            if (rand.nextFloat() < 0.05f) {
                event.getDrops().add(new ItemStack(ModItems.WEED_LEAF));
            }
        }
        // Wheat broken -> ergot infected wheat randomly
        if (state.getBlock() == Blocks.WHEAT && state.getValue(net.minecraft.block.BlockCrops.AGE) == 7) {
            if (rand.nextFloat() < 0.08f) {
                event.getDrops().add(new ItemStack(ModItems.ERGOROT_INFECTED_WHEAT));
            }
        }
    }

    private Item getOpiumBottle(int stage) {
        switch (stage) {
            case 0: return ModItems.OPIUM_BOTTLE_0;
            case 1: return ModItems.OPIUM_BOTTLE_1;
            case 2: return ModItems.OPIUM_BOTTLE_2;
            case 3: return ModItems.OPIUM_BOTTLE_3;
            default: return null;
        }
    }
}
