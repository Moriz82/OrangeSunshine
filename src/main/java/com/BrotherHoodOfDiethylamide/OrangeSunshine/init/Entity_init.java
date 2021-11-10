package com.BrotherHoodOfDiethylamide.OrangeSunshine.init;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.EntityShmokeStackz;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;


public class Entity_init {
    public static void registerEntities()
    {
        registerEntity("shmokestackz", EntityShmokeStackz.class, 120, 50, 6209873, 15253836);
    }

    private static void registerEntity(String name, Class<? extends Entity> entity, int id, int range, int color1, int color2)
    {
        EntityRegistry.registerModEntity(new ResourceLocation(OrangeSunshine.MODID + ":" + name), entity, name, id, OrangeSunshine.instance, range, 1, true, color1, color2);
    }
}
