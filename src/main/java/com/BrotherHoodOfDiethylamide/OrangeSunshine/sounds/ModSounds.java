package com.BrotherHoodOfDiethylamide.OrangeSunshine.sounds;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class ModSounds {
    public static final RegistryObject<SoundEvent> BONG_HIT = register("item.bong_hit", "item.bong_hit");
    public static final RegistryObject<SoundEvent> JOINT_INHALE = register("item.joint_inhale", "item.joint_inhale");
    public static final RegistryObject<SoundEvent> JOINT_EXHALE = register("item.joint_exhale", "item.joint_exhale");

    private static RegistryObject<SoundEvent> register(String name, String path) {
        return register(name, () -> new SoundEvent(new ResourceLocation(OrangeSunshine.MOD_ID, path)));
    }

    private static RegistryObject<SoundEvent> register(String name, Supplier<SoundEvent> supplier) {
        return OrangeSunshine.SOUNDS.register(name, supplier);
    }

    public static void init() {
    }
}
