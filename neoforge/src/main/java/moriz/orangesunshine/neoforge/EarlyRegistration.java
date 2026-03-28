package moriz.orangesunshine.neoforge;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.PSGameRules;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.entity.PSEntities;
import moriz.orangesunshine.fluid.PSFluids;
import moriz.orangesunshine.item.PSItemGroups;
import moriz.orangesunshine.item.PSItems;
import moriz.orangesunshine.particle.PSParticles;
import moriz.orangesunshine.recipe.PSRecipes;
import moriz.orangesunshine.screen.PSScreenHandlers;
import moriz.orangesunshine.PSSounds;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = "orangesunshine")
public class EarlyRegistration {
    private static boolean gameRulesRegistered;
    
    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        // This fires BEFORE mod construction, allowing us to register before freezing
        if (!gameRulesRegistered) {
            OrangeSunshine.LOGGER.info("NeoForge early registration: Game rules");
            PSGameRules.bootstrap();
            gameRulesRegistered = true;
        }
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            OrangeSunshine.LOGGER.info("NeoForge early registration: Blocks");
            PSBlocks.bootstrap();
        } else if (event.getRegistryKey().equals(Registries.ITEM)) {
            OrangeSunshine.LOGGER.info("NeoForge early registration: Items");
            PSItems.bootstrap();
        } else if (event.getRegistryKey().equals(Registries.FLUID)) {
            OrangeSunshine.LOGGER.info("NeoForge early registration: Fluids");
            PSFluids.bootstrap();
        } else if (event.getRegistryKey().equals(Registries.ENTITY_TYPE)) {
            OrangeSunshine.LOGGER.info("NeoForge early registration: Entities");
            PSEntities.bootstrap();
        } else if (event.getRegistryKey().equals(Registries.PARTICLE_TYPE)) {
            OrangeSunshine.LOGGER.info("NeoForge early registration: Particles");
            PSParticles.bootstrap();
        } else if (event.getRegistryKey().equals(Registries.RECIPE_SERIALIZER)) {
            OrangeSunshine.LOGGER.info("NeoForge early registration: Recipes");
            PSRecipes.bootstrap();
        } else if (event.getRegistryKey().equals(Registries.MENU)) {
            OrangeSunshine.LOGGER.info("NeoForge early registration: Screen handlers");
            PSScreenHandlers.bootstrap();
        } else if (event.getRegistryKey().equals(Registries.SOUND_EVENT)) {
            OrangeSunshine.LOGGER.info("NeoForge early registration: Sounds");
            PSSounds.bootstrap();
        } else if (event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
            OrangeSunshine.LOGGER.info("NeoForge early registration: Creative tabs");
            PSItemGroups.bootstrap();
        }
    }
}
