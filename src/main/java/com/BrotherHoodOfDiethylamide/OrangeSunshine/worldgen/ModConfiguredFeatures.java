package com.BrotherHoodOfDiethylamide.OrangeSunshine.worldgen;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ModBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.TallCropsBlock;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.TobaccoBlock;
import net.minecraft.item.Items;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException;

public class ModConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> COCA = key("coca");
    public static final RegistryKey<ConfiguredFeature<?, ?>> WEED = key("weed");
    public static final RegistryKey<ConfiguredFeature<?, ?>> TOBACCO = key("tobacco");
    public static final RegistryKey<ConfiguredFeature<?, ?>> PEYOTE = key("peyote");
    public static final RegistryKey<ConfiguredFeature<?, ?>> SAN_PEDRO = key("san_pedro");
    public static final RegistryKey<ConfiguredFeature<?, ?>> BROWN_SHROOMS_BLOCK = key("brown_shrooms");
    public static final RegistryKey<ConfiguredFeature<?, ?>> RED_SHROOMS_BLOCK = key("red_shrooms");
    public static final RegistryKey<ConfiguredFeature<?, ?>> AYAHUASCA = key("ayahuasca");

    private static RegistryKey<ConfiguredFeature<?, ?>> key(String name) {
        return RegistryKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, new ResourceLocation(OrangeSunshine.MOD_ID, name));
    }

    @Mod.EventBusSubscriber(modid = OrangeSunshine.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent(priority = EventPriority.LOW)
        public static void register(RegistryEvent.Register<Feature<?>> event) {
            register(COCA, Feature.RANDOM_PATCH.configured(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.COCA_BLOCK.get().defaultBlockState().setValue(TallCropsBlock.AGE, 3).setValue(TallCropsBlock.CROP, false)), SimpleBlockPlacer.INSTANCE).tries(16).build()).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).chance(12));
            register(WEED, Feature.RANDOM_PATCH.configured(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.WEED_BLOCK.get().defaultBlockState().setValue(TallCropsBlock.AGE, 3).setValue(TallCropsBlock.CROP, false)), SimpleBlockPlacer.INSTANCE).tries(16).build()).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).chance(12));
            register(TOBACCO, Feature.RANDOM_PATCH.configured(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.TOBACCO_BLOCK.get().defaultBlockState().setValue(TallCropsBlock.AGE, 3).setValue(TallCropsBlock.CROP, false)), SimpleBlockPlacer.INSTANCE).tries(16).build()).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).chance(12));

            register(PEYOTE, Feature.RANDOM_PATCH.configured(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.PEYOTE_BLOCK.get().defaultBlockState()), SimpleBlockPlacer.INSTANCE).tries(16).build()).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).chance(12));
            register(SAN_PEDRO, Feature.RANDOM_PATCH.configured(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.SAN_PEDRO_BLOCK.get().defaultBlockState()), SimpleBlockPlacer.INSTANCE).tries(16).build()).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).chance(12));

            register(BROWN_SHROOMS_BLOCK, Feature.RANDOM_PATCH.configured(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.BROWN_MUSHROOM_BLOCK.get().defaultBlockState()), SimpleBlockPlacer.INSTANCE).tries(16).build()).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).chance(12));
            register(RED_SHROOMS_BLOCK, Feature.RANDOM_PATCH.configured(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.RED_MUSHROOM_BLOCK.get().defaultBlockState()), SimpleBlockPlacer.INSTANCE).tries(16).build()).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).chance(12));

            register(AYAHUASCA, Feature.RANDOM_PATCH.configured(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.AYAHUASCA_BLOCK.get().defaultBlockState().setValue(TallCropsBlock.AGE, 3).setValue(TallCropsBlock.CROP, false)), SimpleBlockPlacer.INSTANCE).tries(23).build()).decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).chance(32));
        }

        private static void register(RegistryKey<ConfiguredFeature<?, ?>> key, ConfiguredFeature<?, ?> configuredFeature) {
            Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, key.location(), configuredFeature);
        }
    }
}
