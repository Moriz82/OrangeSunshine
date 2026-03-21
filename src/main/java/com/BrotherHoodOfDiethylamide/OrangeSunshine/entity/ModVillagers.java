package com.BrotherHoodOfDiethylamide.OrangeSunshine.entity;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.tradelists.TradelistShmokeStackz;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

public class ModVillagers {
    public static VillagerRegistry.VillagerProfession SHMOKE_STACKZ;

    public static void register(RegistryEvent.Register<VillagerRegistry.VillagerProfession> event) {
        SHMOKE_STACKZ = new VillagerRegistry.VillagerProfession(
                OrangeSunshine.MODID + ":shmoke_stackz",
                OrangeSunshine.MODID + ":textures/entity/shmoke_stackz.png",
                "minecraft:textures/entity/zombie_villager/zombie_villager.png"
        );

        VillagerRegistry.VillagerCareer career = new VillagerRegistry.VillagerCareer(SHMOKE_STACKZ, "dealer");
        career.addTrade(1, new TradelistShmokeStackz());

        event.getRegistry().register(SHMOKE_STACKZ);
    }
}
