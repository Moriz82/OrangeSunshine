/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine;

import moriz.orangesunshine.advancement.PSCriteria;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.config.JsonConfig;
import moriz.orangesunshine.config.PSConfig;
import moriz.orangesunshine.entity.PSEntities;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.fluid.PSFluids;
import moriz.orangesunshine.item.PSItemGroups;
import moriz.orangesunshine.item.PSItems;
import moriz.orangesunshine.network.Channel;
import moriz.orangesunshine.particle.PSParticles;
import moriz.orangesunshine.recipe.PSRecipes;
import moriz.orangesunshine.screen.PSScreenHandlers;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.HitResult;

import java.util.Optional;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrangeSunshine {
    public static final Logger LOGGER = LogManager.getLogger();
    private static boolean initialized;

    private static final Supplier<JsonConfig.Loader<PSConfig>> CONFIG_LOADER = JsonConfig.create("orangesunshine.json", PSConfig::new);

    public static Supplier<Optional<DrugProperties>> globalDrugProperties = Optional::empty;
    public static Supplier<Optional<HitResult>> crossHairTarget = Optional::empty;

    public static Optional<DrugProperties> getGlobalDrugProperties() {
        return globalDrugProperties.get();
    }

    public static Optional<HitResult> getCrossHairTarget() {
        return crossHairTarget.get();
    }

    public static JsonConfig.Loader<PSConfig> getConfigLoader() {
        return CONFIG_LOADER.get();
    }

    public static PSConfig getConfig() {
        return getConfigLoader().getData();
    }

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath("orangesunshine", name);
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        PSBlocks.bootstrap();
        PSItems.bootstrap();
        PSTags.bootstrap();
        PSItemGroups.bootstrap();
        PSFluids.bootstrap();
        PSRecipes.bootstrap();
        PSEntities.bootstrap();
        PSGameRules.bootstrap();
        PSSounds.bootstrap();
        PSScreenHandlers.bootstrap();
        Channel.bootstrap();
        PSCriteria.bootstrap();
        PSParticles.bootstrap();
    }
}
