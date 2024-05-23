package moriz.orangesunshine.world.gen;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.PSBlocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.List;

public class ModConfiguredFeatures {

    public static final RegistryKey<ConfiguredFeature<?, ?>> SULFUR_ORE_KEY = registerKey("sulfur_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> SALT_DEPOSIT_KEY = registerKey("salt_deposit");
    public static final RegistryKey<ConfiguredFeature<?, ?>> PYROLUSITE_KEY = registerKey("pyrolusite");
    public static final RegistryKey<ConfiguredFeature<?, ?>> PHOSPHORUS_KEY = registerKey("phosphorus_ore");

    public static void boostrap(Registerable<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplacables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);

        List<OreFeatureConfig.Target> overworldSulfurOres =
                List.of(OreFeatureConfig.createTarget(stoneReplacables, PSBlocks.SULFUR_ORE.getDefaultState()));
        List<OreFeatureConfig.Target> overworldSaltOres =
                List.of(OreFeatureConfig.createTarget(stoneReplacables, PSBlocks.SALT_DEPOSIT.getDefaultState()));
        List<OreFeatureConfig.Target> overworldPyrolusiteOres =
                List.of(OreFeatureConfig.createTarget(stoneReplacables, PSBlocks.PYROLUSITE.getDefaultState()));
        List<OreFeatureConfig.Target> overworldPhosphorusOres =
                List.of(OreFeatureConfig.createTarget(stoneReplacables, PSBlocks.PHOSPHORUS_ORE.getDefaultState()));

        register(context, SULFUR_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldSulfurOres, 12));
        register(context, SALT_DEPOSIT_KEY, Feature.ORE, new OreFeatureConfig(overworldSaltOres, 12));
        register(context, PYROLUSITE_KEY, Feature.ORE, new OreFeatureConfig(overworldPyrolusiteOres, 12));
        register(context, PHOSPHORUS_KEY, Feature.ORE, new OreFeatureConfig(overworldPhosphorusOres, 12));
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, OrangeSunshine.id(name));
    }
    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
