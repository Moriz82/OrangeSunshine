package com.BrotherHoodOfDiethylamide.OrangeSunshine.utils;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.FurnaceBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Block_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class RegistryHandler {
    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(Item_init.ITEMS.toArray(new Item[0]));
        FurnaceBlocks.registerItemBlocks(event.getRegistry());
        FurnaceBlocks.registerModels();
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(Block_init.BLOCKS.toArray(new Block[0]));
        FurnaceBlocks.register(event.getRegistry());
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        for(Item item : Item_init.ITEMS) {
            if(item instanceof IHasModel) {
                ((IHasModel)item).registerModels();
            }
        }

        for(Block block : Block_init.BLOCKS) {
            if(block instanceof IHasModel) {
                ((IHasModel)block).registerModels();
            }
        }
    }
}