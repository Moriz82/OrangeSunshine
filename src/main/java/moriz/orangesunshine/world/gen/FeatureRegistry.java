package moriz.orangesunshine.world.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

class FeatureRegistry {
    private static final List<ConfiguredEntry> CONFIGURED_FEATURES = new ArrayList<>();
    private static final List<PlacedEntry> PLACED_FEATURES = new ArrayList<>();

    static {
        DynamicRegistrySetupCallback.EVENT.register(registries -> {
            registries.getOptional(Registries.CONFIGURED_FEATURE).ifPresent(registry -> {
                CONFIGURED_FEATURES.forEach(entry -> {
                    Registry.register(registry, entry.key(), entry.factory().get());
                });
            });
            registries.getOptional(Registries.PLACED_FEATURE).ifPresent(registry -> {
                var lookup = registries.getOptional(Registries.CONFIGURED_FEATURE).orElseThrow();
                PLACED_FEATURES.forEach(entry -> {
                    Registry.register(registry, entry.key(), entry.factory().apply(lookup.get(entry.configuration()).orElseThrow()));
                });
            });
        });
    }

    public static void registerConfiguredFeature(
            ResourceKey<ConfiguredFeature<?, ?>> featureKey,
            Supplier<ConfiguredFeature<?, ?>> factory) {
        CONFIGURED_FEATURES.add(new ConfiguredEntry(featureKey, factory));
    }

    public static void registerPlacedFeature(
            ResourceKey<PlacedFeature> key,
            ResourceKey<ConfiguredFeature<?, ?>> configuration,
            Function<Holder<ConfiguredFeature<?, ?>>, PlacedFeature> factory) {
        PLACED_FEATURES.add(new PlacedEntry(key, configuration, factory));
    }

    record ConfiguredEntry (
        ResourceKey<ConfiguredFeature<?, ?>> key,
        Supplier<ConfiguredFeature<?, ?>> factory
    ) {}

    record PlacedEntry (
            ResourceKey<PlacedFeature> key,
            ResourceKey<ConfiguredFeature<?, ?>> configuration,
            Function<Holder<ConfiguredFeature<?, ?>>, PlacedFeature> factory
        ) {}
}
