package com.BrotherHoodOfDiethylamide.OrangeSunshine.worldgen;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OrangeSunshine.MOD_ID)
public class WorldGen {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        Biome.Category category = event.getCategory();

        if (category == Biome.Category.JUNGLE) {
            event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, getFeature(ModConfiguredFeatures.COCA));
        } else if (category == Biome.Category.SAVANNA) {
            event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, getFeature(ModConfiguredFeatures.WEED));
        }
    }

    private static ConfiguredFeature<?, ?> getFeature(RegistryKey<ConfiguredFeature<?, ?>> key) {
        return WorldGenRegistries.CONFIGURED_FEATURE.getOrThrow(key);
    }
}
