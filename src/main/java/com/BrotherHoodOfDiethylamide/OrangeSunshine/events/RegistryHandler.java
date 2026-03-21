package com.BrotherHoodOfDiethylamide.OrangeSunshine.events;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ModBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity.*;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.EntityShmokeStackz;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entity.ModVillagers;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class RegistryHandler {
    private static int entityId = 0;

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(ModBlocks.ALL_BLOCKS.toArray(new Block[0]));

        // Register tile entities
        GameRegistry.registerTileEntity(TileDryingTable.class, MODID + ":drying_table");
        GameRegistry.registerTileEntity(TileFridge.class, MODID + ":fridge");
        GameRegistry.registerTileEntity(TileCompoundCompressor.class, MODID + ":compound_compressor");
        GameRegistry.registerTileEntity(TileCompoundExtractor.class, MODID + ":compound_extractor");
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(ModItems.ALL_ITEMS.toArray(new Item[0]));

        // Register ItemBlocks for each block
        for (Block block : ModBlocks.ALL_BLOCKS) {
            Item itemBlock = (ItemBlock) new ItemBlock(block).setRegistryName(block.getRegistryName());
            event.getRegistry().register(itemBlock);
        }

        ModItems.initBongables();
    }

    @SubscribeEvent
    public static void onVillagerRegister(RegistryEvent.Register<net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession> event) {
        ModVillagers.register(event);
    }

    public static void registerEntities() {
        EntityRegistry.registerModEntity(
                new net.minecraft.util.ResourceLocation(MODID, "shmoke_stackz"),
                EntityShmokeStackz.class,
                "shmoke_stackz",
                entityId++,
                com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine.instance,
                64, 3, true
        );
    }
}
