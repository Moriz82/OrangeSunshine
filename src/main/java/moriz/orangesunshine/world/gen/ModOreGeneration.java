package moriz.orangesunshine.world.gen;

import moriz.orangesunshine.block.PSBlocks;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class ModOreGeneration {
    public static void generateOres() {
        var stoneTest = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);

        // Register configured features
        FeatureRegistry.registerConfiguredFeature(ModConfiguredFeatures.SULFUR_ORE_KEY, () ->
                new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(
                        List.of(OreConfiguration.target(stoneTest, PSBlocks.SULFUR_ORE.defaultBlockState())), 12)));
        FeatureRegistry.registerConfiguredFeature(ModConfiguredFeatures.SALT_DEPOSIT_KEY, () ->
                new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(
                        List.of(OreConfiguration.target(stoneTest, PSBlocks.SALT_DEPOSIT.defaultBlockState())), 12)));
        FeatureRegistry.registerConfiguredFeature(ModConfiguredFeatures.PYROLUSITE_KEY, () ->
                new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(
                        List.of(OreConfiguration.target(stoneTest, PSBlocks.PYROLUSITE.defaultBlockState())), 12)));
        FeatureRegistry.registerConfiguredFeature(ModConfiguredFeatures.PHOSPHORUS_KEY, () ->
                new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(
                        List.of(OreConfiguration.target(stoneTest, PSBlocks.PHOSPHORUS_ORE.defaultBlockState())), 12)));

        // Register placed features
        var uniformHeight = HeightRangePlacement.uniform(VerticalAnchor.absolute(-120), VerticalAnchor.absolute(120));
        FeatureRegistry.registerPlacedFeature(ModPlacedFeatures.SULFUR_ORE_PLACED_KEY, ModConfiguredFeatures.SULFUR_ORE_KEY,
                feature -> new PlacedFeature(feature, ModOrePlacement.modifiersWithCount(12, uniformHeight)));
        FeatureRegistry.registerPlacedFeature(ModPlacedFeatures.SALT_DEPOSIT_PLACED_KEY, ModConfiguredFeatures.SALT_DEPOSIT_KEY,
                feature -> new PlacedFeature(feature, ModOrePlacement.modifiersWithCount(12, uniformHeight)));
        FeatureRegistry.registerPlacedFeature(ModPlacedFeatures.PYROLUSITE_PLACED_KEY, ModConfiguredFeatures.PYROLUSITE_KEY,
                feature -> new PlacedFeature(feature, ModOrePlacement.modifiersWithCount(12, uniformHeight)));
        FeatureRegistry.registerPlacedFeature(ModPlacedFeatures.PHOSPHORUS_PLACED_KEY, ModConfiguredFeatures.PHOSPHORUS_KEY,
                feature -> new PlacedFeature(feature, ModOrePlacement.modifiersWithCount(12, uniformHeight)));

        // Add to biomes
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.SULFUR_ORE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.SALT_DEPOSIT_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.PYROLUSITE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.PHOSPHORUS_PLACED_KEY);
    }
}
