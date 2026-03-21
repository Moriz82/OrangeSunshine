package com.BrotherHoodOfDiethylamide.OrangeSunshine.sounds;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = OrangeSunshine.MODID)
public class ModSounds {
    public static SoundEvent BONG_HIT;
    public static SoundEvent JOINT_INHALE;
    public static SoundEvent JOINT_EXHALE;

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        BONG_HIT = registerSound(event, "item.bong_hit");
        JOINT_INHALE = registerSound(event, "item.joint_inhale");
        JOINT_EXHALE = registerSound(event, "item.joint_exhale");
    }

    private static SoundEvent registerSound(RegistryEvent.Register<SoundEvent> event, String name) {
        ResourceLocation rl = new ResourceLocation(OrangeSunshine.MODID, name);
        SoundEvent soundEvent = new SoundEvent(rl);
        soundEvent.setRegistryName(rl);
        event.getRegistry().register(soundEvent);
        return soundEvent;
    }
}
