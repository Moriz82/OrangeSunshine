package com.BrotherHoodOfDiethylamide.OrangeSunshine.utils;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ComplexBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.effects.particles.Smoke;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.registry.StackzProfession;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.tradelists.TradelistShmokeStackz;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Block_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
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
        VillagerRegistry.VillagerCareer career = new VillagerRegistry.VillagerCareer(shmokestackz, "Shmoke_Stackz");
        career.addTrade(1, new TradelistShmokeStackz());
        shmokestackz.getId();
    }

    public static boolean isSmoke = false;
    public static EntityLivingBase smokePlayer = null;
    static int currTick = 0;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if (isSmoke && smokePlayer != null){
            Smoke.doEffect(smokePlayer, smokePlayer.world);
            currTick++;
            if (currTick >= 100){
                isSmoke = false;
                currTick = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(Item_init.ITEMS.toArray(new Item[0]));
        ComplexBlocks.registerItemBlocks(event.getRegistry());
        ComplexBlocks.registerModels();
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(Block_init.BLOCKS.toArray(new Block[0]));
        ComplexBlocks.register(event.getRegistry());
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