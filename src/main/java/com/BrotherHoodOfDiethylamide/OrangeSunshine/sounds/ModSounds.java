package com.BrotherHoodOfDiethylamide.OrangeSunshine.sounds;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class ModSounds {
    public static final RegistryObject<SoundEvent> BONG_HIT = register("item.bong_hit", "item.bong_hit");


    private static RegistryObject<SoundEvent> register(String name, String path) {
        return register(name, () -> new SoundEvent(new ResourceLocation(OrangeSunshine.MOD_ID, path)));
    }

    private static RegistryObject<SoundEvent> register(String name, Supplier<SoundEvent> supplier) {
        return OrangeSunshine.SOUNDS.register(name, supplier);
    }

    public static void init() {
    }
}
