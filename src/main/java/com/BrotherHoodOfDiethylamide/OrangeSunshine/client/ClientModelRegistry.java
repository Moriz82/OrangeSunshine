package com.BrotherHoodOfDiethylamide.OrangeSunshine.client;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ModBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = OrangeSunshine.MODID)
public class ClientModelRegistry {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        // Register item models for all mod items
        for (Item item : ModItems.ALL_ITEMS) {
            if (item != null && item.getRegistryName() != null) {
                ModelLoader.setCustomModelResourceLocation(item, 0,
                        new ModelResourceLocation(item.getRegistryName(), "inventory"));
            }
        }

        // Register item models for block items
        for (Block block : ModBlocks.ALL_BLOCKS) {
            Item blockItem = Item.getItemFromBlock(block);
            if (blockItem != Items.AIR && blockItem.getRegistryName() != null) {
                ModelLoader.setCustomModelResourceLocation(blockItem, 0,
                        new ModelResourceLocation(blockItem.getRegistryName(), "inventory"));
            }
        }
    }
}
