/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine;

import moriz.orangesunshine.advancement.PSCriteria;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.command.PSCommands;
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
import moriz.orangesunshine.world.gen.PSWorldGen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;

import java.util.Optional;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrangeSunshine implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();

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
        return new Identifier("orangesunshine", name);
    }

    @Override
    public void onInitialize() {
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            DrugProperties.of(player).sendCapabilities();
        });
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            DrugProperties.of(newPlayer).copyFrom(DrugProperties.of(oldPlayer), alive);
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            DrugProperties.of(handler.player).sendCapabilities();
        });

        PSBlocks.bootstrap();
        PSItems.bootstrap();
        PSTags.bootstrap();
        PSItemGroups.bootstrap();
        PSFluids.bootstrap();
        PSRecipes.bootstrap();
        PSEntities.bootstrap();
        PSWorldGen.bootstrap();
        PSGameRules.bootstrap();
        PSCommands.bootstrap();
        PSSounds.bootstrap();
        PSScreenHandlers.bootstrap();
        Channel.bootstrap();
        PSCriteria.bootstrap();
        PSParticles.bootstrap();
        PSDamageTypes.bootstrap();
    }
}