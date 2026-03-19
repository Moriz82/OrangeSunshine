package moriz.orangesunshine.world.gen;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.PSBlocks;

import java.util.List;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.core.registries.Registries;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> SULFUR_ORE_KEY = registerKey("sulfur_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SALT_DEPOSIT_KEY = registerKey("salt_deposit");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PYROLUSITE_KEY = registerKey("pyrolusite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PHOSPHORUS_KEY = registerKey("phosphorus_ore");

    public static void boostrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);

        List<OreConfiguration.TargetBlockState> overworldSulfurOres =
                List.of(OreConfiguration.target(stoneReplaceables, PSBlocks.SULFUR_ORE.defaultBlockState()));
        List<OreConfiguration.TargetBlockState> overworldSaltOres =
                List.of(OreConfiguration.target(stoneReplaceables, PSBlocks.SALT_DEPOSIT.defaultBlockState()));
        List<OreConfiguration.TargetBlockState> overworldPyrolusiteOres =
                List.of(OreConfiguration.target(stoneReplaceables, PSBlocks.PYROLUSITE.defaultBlockState()));
        List<OreConfiguration.TargetBlockState> overworldPhosphorusOres =
                List.of(OreConfiguration.target(stoneReplaceables, PSBlocks.PHOSPHORUS_ORE.defaultBlockState()));

        register(context, SULFUR_ORE_KEY, Feature.ORE, new OreConfiguration(overworldSulfurOres, 12));
        register(context, SALT_DEPOSIT_KEY, Feature.ORE, new OreConfiguration(overworldSaltOres, 12));
        register(context, PYROLUSITE_KEY, Feature.ORE, new OreConfiguration(overworldPyrolusiteOres, 12));
        register(context, PHOSPHORUS_KEY, Feature.ORE, new OreConfiguration(overworldPhosphorusOres, 12));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, OrangeSunshine.id(name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
            BootstrapContext<ConfiguredFeature<?, ?>> context,
            ResourceKey<ConfiguredFeature<?, ?>> key,
            F feature,
            FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
