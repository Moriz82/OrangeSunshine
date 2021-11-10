package com.BrotherHoodOfDiethylamide.OrangeSunshine.utils;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.FurnaceBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.EntityShmokeStackz;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.registry.StackzProfession;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.tradelists.TradelistShmokeStackz;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Block_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Entity_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

@Mod.EventBusSubscriber
@GameRegistry.ObjectHolder(OrangeSunshine.MODID)
public class RegistryHandler {
    public static StackzProfession shmokestackz = new StackzProfession(OrangeSunshine.MODID+":shmoke_stackz", OrangeSunshine.MODID+":textures/entity/shmoke_stackz.png", OrangeSunshine.MODID+":textures/entity/shmoke_stackz.png");
//make multiple profession to override old ones
    @SubscribeEvent
    public static void addProfessions(final RegistryEvent.Register<VillagerRegistry.VillagerProfession> villagerProfessionRegister){
        villagerProfessionRegister.getRegistry().register(shmokestackz);
        VillagerRegistry.VillagerCareer career = new VillagerRegistry.VillagerCareer(shmokestackz, "shmoke_stackz_career");
        career.addTrade(1, new TradelistShmokeStackz());
        shmokestackz.getId();
    }

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