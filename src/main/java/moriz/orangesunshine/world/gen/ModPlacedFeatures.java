package moriz.orangesunshine.world.gen;

import moriz.orangesunshine.OrangeSunshine;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.core.registries.Registries;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> SULFUR_ORE_PLACED_KEY = registerKey("sulfur_ore_placed");
    public static final ResourceKey<PlacedFeature> SALT_DEPOSIT_PLACED_KEY = registerKey("salt_deposit_placed");
    public static final ResourceKey<PlacedFeature> PYROLUSITE_PLACED_KEY = registerKey("pyrolusite_placed");
    public static final ResourceKey<PlacedFeature> PHOSPHORUS_PLACED_KEY = registerKey("phosphorus_ore_placed");

    public static void boostrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatureLookup = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, SULFUR_ORE_PLACED_KEY, configuredFeatureLookup.getOrThrow(ModConfiguredFeatures.SULFUR_ORE_KEY),
                ModOrePlacement.modifiersWithCount(12, // Veins per Chunk
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-120), VerticalAnchor.absolute(120))));
        register(context, SALT_DEPOSIT_PLACED_KEY, configuredFeatureLookup.getOrThrow(ModConfiguredFeatures.SALT_DEPOSIT_KEY),
                ModOrePlacement.modifiersWithCount(12, // Veins per Chunk
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-120), VerticalAnchor.absolute(120))));
        register(context, PYROLUSITE_PLACED_KEY, configuredFeatureLookup.getOrThrow(ModConfiguredFeatures.PYROLUSITE_KEY),
                ModOrePlacement.modifiersWithCount(12, // Veins per Chunk
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-120), VerticalAnchor.absolute(120))));
        register(context, PHOSPHORUS_PLACED_KEY, configuredFeatureLookup.getOrThrow(ModConfiguredFeatures.PHOSPHORUS_KEY),
                ModOrePlacement.modifiersWithCount(12, // Veins per Chunk
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-120), VerticalAnchor.absolute(120))));
    }

    public static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, OrangeSunshine.id(name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
