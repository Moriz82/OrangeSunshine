/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.world.gen;

import java.util.List;
import java.util.function.Predicate;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.AgavePlantBlock;
import moriz.orangesunshine.block.CannabisPlantBlock;
import moriz.orangesunshine.block.NightshadeBlock;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.block.PeyoteBlock;
import moriz.orangesunshine.block.VineStemBlock;
import moriz.orangesunshine.config.BiomeSelector;
import moriz.orangesunshine.config.PSConfig;
import moriz.orangesunshine.world.gen.structure.MutableStructurePool;
import net.fabricmc.fabric.api.biome.v1.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

/**
 * Created by lukas on 25.04.14.
 * Updated by Sollace on 16 Jan 2023
 */
public class PSWorldGen {
    public static final TilledPatchFeature TILLED_PATCH_FEATURE = Registry.register(BuiltInRegistries.FEATURE, OrangeSunshine.id("tilled_patch"), new TilledPatchFeature());

    public static final ResourceKey<ConfiguredFeature<?, ?>> JUNIPER_TREE_CONFIG = createConfiguredFeature("juniper_tree");
    public static final ResourceKey<PlacedFeature> JUNIPER_TREE_PLACEMENT = createPlacement("juniper_tree_checked");

    public static ResourceKey<ConfiguredFeature<?, ?>> createConfiguredFeature(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, OrangeSunshine.id(name));
    }

    public static ResourceKey<PlacedFeature> createPlacement(String id) {
        return ResourceKey.create(Registries.PLACED_FEATURE, OrangeSunshine.id(id));
    }

    private static Block block(String path) {
        return BuiltInRegistries.BLOCK.getOptional(OrangeSunshine.id(path)).orElse(Blocks.AIR);
    }

    private static void registerTilledPatch(String id, CannabisPlantBlock crop, boolean requireWater, PSConfig.Balancing.Generation.FeatureConfig config) {
        var cannabisPatch = createConfiguredFeature(id + "_tilled_patch");
        FeatureRegistry.registerConfiguredFeature(cannabisPatch, () -> {
            return new ConfiguredFeature<>(TILLED_PATCH_FEATURE, new TilledPatchFeature.Config(requireWater, crop));
        });

        var placement = createPlacement(id + "_tilled_patch_checked");
        FeatureRegistry.registerPlacedFeature(placement, cannabisPatch, feature -> {
            return new PlacedFeature(feature, List.of(
                    RarityFilter.onAverageOnceEvery(160),
                    InSquarePlacement.spread(),
                    PlacementUtils.HEIGHTMAP,
                    BiomeFilter.biome()
            ));
        });

        config.ifEnabled(spawnableBiomes -> {
            BiomeModifications.addFeature(
                    spawnableBiomes.createPredicate(BiomeSelectors.foundInOverworld().and(
                            BiomeSelector.COLD
                            .or(BiomeSelectors.tag(BiomeTags.IS_HILL))
                            .or(BiomeSelectors.tag(BiomeTags.IS_FOREST))
                            .or(ctx -> ctx.getBiomeKey() == Biomes.PLAINS)
                    )),
                    GenerationStep.Decoration.VEGETAL_DECORATION,
                    placement
            );
        });
    }

    private static void registerUnTilledPatch(String id, Block plant, IntegerProperty ageProperty, IntProvider ageRange, Predicate<BiomeSelectionContext> builtinBiomePredicate, PSConfig.Balancing.Generation.FeatureConfig config) {
        var patch = createConfiguredFeature(id + "_patch");

        FeatureRegistry.registerConfiguredFeature(patch, () -> {
            return new ConfiguredFeature<>(Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(
                    Feature.SIMPLE_BLOCK,
                    new SimpleBlockConfiguration(new RandomizedIntStateProvider(BlockStateProvider.simple(plant), ageProperty, ageRange)),
                    List.of(plant),
                    5
            ));
        });

        var placement = createPlacement(id + "_patch_checked");

        FeatureRegistry.registerPlacedFeature(placement, patch, feature -> {
            return new PlacedFeature(feature, List.of(
                    RarityFilter.onAverageOnceEvery(20),
                    InSquarePlacement.spread(),
                    PlacementUtils.HEIGHTMAP,
                    BiomeFilter.biome()));
        });

        FeatureRegistry.registerPlacedFeature(createPlacement(id + "_patch_unchecked"), patch, feature -> {
            return new PlacedFeature(feature, List.of());
        });

        config.ifEnabled(spawnableBiomes -> {
            BiomeModifications.addFeature(
                    spawnableBiomes.createPredicate(builtinBiomePredicate),
                    GenerationStep.Decoration.VEGETAL_DECORATION,
                    placement
            );
        });
    }

    public static void bootstrap() {
        var genConf = OrangeSunshine.getConfig().balancing.worldGeneration;

        FeatureRegistry.registerConfiguredFeature(JUNIPER_TREE_CONFIG, () -> {
            return new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                    BlockStateProvider.simple(block("juniper_log")),
                    new ForkingTrunkPlacer(5, 2, 2),
                    BlockStateProvider.simple(block("juniper_leaves")),
                    new BlobFoliagePlacer(
                            ConstantInt.of(2),
                            ConstantInt.ZERO,
                            3
                    ),
                    new TwoLayersFeatureSize(1, 0, 2))
            .dirt(BlockStateProvider.simple(Blocks.ROOTED_DIRT))
            .forceDirt()
            .build());
        });
        FeatureRegistry.registerPlacedFeature(JUNIPER_TREE_PLACEMENT, JUNIPER_TREE_CONFIG, config -> {
            return new PlacedFeature(config, VegetationPlacements.treePlacement(
                    PlacementUtils.countExtra(1, 0.05F, 2),
                    block("juniper_sapling"))
            );
        });

        genConf.juniper.ifEnabled(spawnableBiomes -> {
            BiomeModifications.addFeature(
                    spawnableBiomes.createPredicate(
                        BiomeSelectors.foundInOverworld().and(BiomeSelector.DRY).and(
                                BiomeSelectors.tag(BiomeTags.IS_HILL)
                            .or(BiomeSelectors.tag(BiomeTags.IS_FOREST))
                        )
                    ),
                    GenerationStep.Decoration.VEGETAL_DECORATION,
                    JUNIPER_TREE_PLACEMENT
            );
        });

        registerTilledPatch("cannabis", PSBlocks.CANNABIS, false, genConf.cannabis);
        registerTilledPatch("hop", PSBlocks.HOP, false, genConf.hop);
        registerTilledPatch("tobacco", PSBlocks.TOBACCO, false, genConf.tobacco);
        registerTilledPatch("coffea", PSBlocks.COFFEA, false, genConf.coffea);
        registerTilledPatch("coca", PSBlocks.COCA, true, genConf.coca);
        registerUnTilledPatch("morning_glory", PSBlocks.MORNING_GLORY, VineStemBlock.AGE, UniformInt.of(0, VineStemBlock.MAX_AGE), BiomeSelectors.includeByKey(
                Biomes.FLOWER_FOREST,
                Biomes.SUNFLOWER_PLAINS,
                Biomes.MEADOW,
                Biomes.LUSH_CAVES
        ), genConf.morningGlories);
        registerUnTilledPatch("belladonna", PSBlocks.BELLADONNA, NightshadeBlock.AGE, UniformInt.of(0, NightshadeBlock.MAX_AGE), BiomeSelectors.includeByKey(Biomes.DARK_FOREST), genConf.belladonna);
        registerUnTilledPatch("jimsonweed", PSBlocks.JIMSONWEEED, NightshadeBlock.AGE, UniformInt.of(0, NightshadeBlock.MAX_AGE), BiomeSelectors.includeByKey(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE), genConf.jimsonweed);
        registerUnTilledPatch("tomato", PSBlocks.TOMATOES, NightshadeBlock.AGE, UniformInt.of(0, NightshadeBlock.MAX_AGE), BiomeSelectors.includeByKey(Biomes.FOREST), genConf.tomato);
        registerUnTilledPatch("peyote", PSBlocks.PEYOTE, PeyoteBlock.AGE, UniformInt.of(0, PeyoteBlock.MAX_AGE), BiomeSelectors.foundInOverworld().and(
                    BiomeSelectors.tag(BiomeTags.IS_SAVANNA)
                .or(BiomeSelectors.tag(BiomeTags.IS_BADLANDS))
                .or(BiomeSelectors.tag(BiomeTags.HAS_DESERT_PYRAMID))
                .or(BiomeSelector.DRY)
        ), genConf.peyote);
        registerUnTilledPatch("agave", PSBlocks.AGAVE_PLANT, AgavePlantBlock.AGE, UniformInt.of(0, AgavePlantBlock.MAX_AGE), BiomeSelectors.foundInOverworld().and(
            BiomeSelectors.tag(BiomeTags.IS_BADLANDS)
            .or(BiomeSelectors.tag(BiomeTags.HAS_DESERT_PYRAMID))
            .or(BiomeSelector.DRY)
        ), genConf.agave);

        registerTilledPatch("salvia", PSBlocks.SALVIA, false, genConf.salvia);
        registerTilledPatch("kratom", PSBlocks.KRATOM, false, genConf.kratom);

        ModOreGeneration.generateOres();

        MutableStructurePool.bootstrap();
    }
}
