package moriz.orangesunshine.neoforge;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.OrangeSunshinePlatform;
import moriz.orangesunshine.PSGameRules;
import moriz.orangesunshine.PSTags;
import moriz.orangesunshine.advancement.PSCriteria;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.entity.PSEntities;
import moriz.orangesunshine.fluid.PSFluids;
import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.item.PSItemGroups;
import moriz.orangesunshine.item.PSItems;
import moriz.orangesunshine.item.RiftJarItem;
import moriz.orangesunshine.fluid.ChemicalExtractFluid;
import moriz.orangesunshine.fluid.container.FluidContainer;
import moriz.orangesunshine.network.Channel;
import moriz.orangesunshine.particle.PSParticles;
import moriz.orangesunshine.recipe.PSRecipes;
import moriz.orangesunshine.screen.PSScreenHandlers;
import moriz.orangesunshine.PSSounds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(OrangeSunshinePlatform.MOD_ID)
public final class OrangeSunshineNeoForge {
    public OrangeSunshineNeoForge(IEventBus modEventBus) {
        // Registration happens via @EventBusSubscriber in EarlyRegistration class
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onBuildCreativeTab);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        OrangeSunshinePlatform.LOGGER.info("Initializing {} on NeoForge", OrangeSunshinePlatform.MOD_NAME);
        PSTags.bootstrap();
        Channel.bootstrap();
        PSCriteria.bootstrap();
    }

    private void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> tab = event.getTabKey();

        if (tab.equals(PSItemGroups.GENERAL)) {
            event.accept(PSItems.DRYING_TABLE);
            event.accept(PSItems.IRON_DRYING_TABLE);
            event.accept(PSItems.FLASK);
            event.accept(PSItems.DISTILLERY);
            event.accept(PSItems.BOTTLE_RACK);
            event.accept(PSItems.MASH_TUB);

            event.accept(PSItems.OAK_BARREL);
            event.accept(PSItems.BIRCH_BARREL);
            event.accept(PSItems.SPRUCE_BARREL);
            event.accept(PSItems.ACACIA_BARREL);
            event.accept(PSItems.JUNGLE_BARREL);
            event.accept(PSItems.DARK_OAK_BARREL);

            if (OrangeSunshine.getConfig().balancing.enableRiftJars) {
                event.accept(RiftJarItem.createFilledRiftJar(0.0F, PSItems.RIFT_JAR));
                event.accept(RiftJarItem.createFilledRiftJar(0.25F, PSItems.RIFT_JAR));
                event.accept(RiftJarItem.createFilledRiftJar(0.55F, PSItems.RIFT_JAR));
                event.accept(RiftJarItem.createFilledRiftJar(0.75F, PSItems.RIFT_JAR));
                event.accept(RiftJarItem.createFilledRiftJar(0.9F, PSItems.RIFT_JAR));
            }

            event.accept(PSItems.TRAY);
            event.accept(PSItems.BUNSEN_BURNER);

            event.accept(PSItems.SMOKING_PIPE);
            event.accept(PSItems.CIGARETTE);
            event.accept(PSItems.CIGAR);
            event.accept(PSItems.JOINT);
            event.accept(PSItems.BLOTTER);
            event.accept(PSItems.ROLLING_PAPER);
            event.accept(PSItems.JOLLY_RANCHER);
            event.accept(PSItems.NALOXONE);

            event.accept(PSItems.BONG);
            event.accept(PSItems.SYRINGE);
            event.accept(PSItems.SYRINGE.getDefaultStack(PSFluids.COCAINE));
            event.accept(PSItems.SYRINGE.getDefaultStack(PSFluids.CAFFEINE));
            event.accept(PSItems.SYRINGE.getDefaultStack(PSFluids.BATH_SALTS));
            event.accept(ChemicalExtractFluid.DISTILLATION.set(PSItems.SYRINGE.getDefaultStack(PSFluids.MORNING_GLORY_EXTRACT), 2));

            event.accept(PSItems.COFFEA_CHERRIES);
            event.accept(PSItems.COFFEE_BEANS);

            event.accept(PSItems.TOMATO_LEAF);
            event.accept(PSItems.TOMATO_SEEDS);
            event.accept(PSItems.TOMATO);

            event.accept(PSItems.OBSIDIAN_BOTTLE);
            event.accept(PSItems.OBSIDIAN_DUST);

            event.accept(PSItems.MORNING_GLORY);
            event.accept(PSItems.MORNING_GLORY_SEEDS);
            event.accept(PSItems.ERGOT);
            event.accept(PSItems.ERGOT_POWDER);

            event.accept(PSItems.SULFUR);
            event.accept(PSItems.SULFUR_POWDER);
            event.accept(PSItems.SALT);
            event.accept(PSItems.SALT_POWDER);
            event.accept(PSItems.MANGANESE_DIOXIDE);
            event.accept(PSItems.MANGANESE_DIOXIDE_POWDER);
            event.accept(PSItems.PHOSPHORUS);

            event.accept(PSItems.LSA_BLOTTER);
            event.accept(PSItems.LSD_BLOTTER);
            event.accept(PSItems.ORANGESUNSINE_BLOTTER);

            event.accept(PSItems.ETH_HCL);
            event.accept(PSBlocks.MORTAR_PESTLE);
            event.accept(PSBlocks.MIXING_TABLE);

            event.accept(PSItems.DEFAT_ERGOT);
            event.accept(PSItems.ERGOT_ALKALOIDS);
            event.accept(PSItems.NEUTRAL_ERGOT_ALKALOIDS);
            event.accept(PSItems.ERGOPEPTINES);
            event.accept(PSItems.ERGOPEPTINE_CRYSTALS);
            event.accept(PSItems.DISSOLVED_ERGOPEPTINES);
            event.accept(PSItems.DISSOLVED_ERGOPEPTINES_ACID);

            event.accept(PSItems.LYSERGIC_ACID);

            event.accept(PSItems.LSD25);
            event.accept(PSItems.ALD52);

            event.accept(PSItems.JIMSONWEED_SEEDS);
            event.accept(PSItems.JIMSONWEED_SEED_POD);
            event.accept(PSItems.JIMSONWEED_LEAF);
            event.accept(PSItems.DRIED_JIMSONWEED_LEAF);

            event.accept(PSItems.BELLADONNA_SEEDS);
            event.accept(PSItems.BELLADONNA_BERRIES);
            event.accept(PSItems.BELLADONNA_LEAF);
            event.accept(PSItems.DRIED_BELLADONNA_LEAF);

            event.accept(PSItems.TOBACCO_SEEDS);
            event.accept(PSItems.TOBACCO_LEAVES);
            event.accept(PSItems.DRIED_TOBACCO);

            event.accept(PSItems.COCA_SEEDS);
            event.accept(PSItems.COCA_LEAVES);
            event.accept(PSItems.DRIED_COCA_LEAVES);
            event.accept(PSItems.COCAINE_POWDER);

            event.accept(PSItems.AGAVE_LEAF);
            event.accept(PSItems.PEYOTE);
            event.accept(PSItems.DRIED_PEYOTE);
            event.accept(PSItems.PEYOTE_JOINT);

            event.accept(PSItems.HOP_SEEDS);
            event.accept(PSItems.HOP_CONES);

            event.accept(PSItems.CANNABIS_SEEDS);
            event.accept(PSItems.CANNABIS_LEAF);
            event.accept(PSItems.DRIED_CANNABIS_LEAF);
            event.accept(PSItems.CANNABIS_BUDS);
            event.accept(PSItems.DRIED_CANNABIS_BUDS);

            event.accept(PSItems.HASH_MUFFIN);

            event.accept(PSItems.BROWN_MAGIC_MUSHROOMS);
            event.accept(PSItems.RED_MAGIC_MUSHROOMS);

            event.accept(PSItems.LATTICE);
            event.accept(PSItems.WINE_GRAPES);

            event.accept(PSItems.JUNIPER_LEAVES);
            event.accept(PSItems.FRUITING_JUNIPER_LEAVES);
            event.accept(PSItems.JUNIPER_LOG);
            event.accept(PSItems.JUNIPER_WOOD);
            event.accept(PSItems.STRIPPED_JUNIPER_LOG);
            event.accept(PSItems.STRIPPED_JUNIPER_WOOD);
            event.accept(PSItems.JUNIPER_SAPLING);
            event.accept(PSItems.JUNIPER_BERRIES);

            event.accept(PSItems.JUNIPER_PLANKS);
            event.accept(PSItems.JUNIPER_STAIRS);
            event.accept(PSItems.JUNIPER_SIGN);
            event.accept(PSItems.JUNIPER_DOOR);
            event.accept(PSItems.JUNIPER_HANGING_SIGN);
            event.accept(PSItems.JUNIPER_PRESSURE_PLATE);
            event.accept(PSItems.JUNIPER_FENCE);
            event.accept(PSItems.JUNIPER_TRAPDOOR);
            event.accept(PSItems.JUNIPER_FENCE_GATE);
            event.accept(PSItems.JUNIPER_BUTTON);
            event.accept(PSItems.JUNIPER_SLAB);
            event.accept(PSItems.JUNIPER_BOAT);
            event.accept(PSItems.JUNIPER_CHEST_BOAT);

            event.accept(PSItems.PAPER_BAG);

            if (OrangeSunshine.getConfig().balancing.enableHarmonium) {
                for (DyeColor dye : DyeColor.values()) {
                    ItemStack harmonium = PSItems.HARMONIUM.getDefaultInstance();
                    PSItems.HARMONIUM.setColor(harmonium, dye.getTextureDiffuseColor());
                    event.accept(harmonium);
                }
            }
        } else if (tab.equals(PSItemGroups.DRINKS)) {
            appendAllFluids(PSItems.STONE_CUP, event);
            event.accept(PSItems.SHOT_GLASS);
            PSFluids.AGAVE.getDefaultStacks(PSItems.SHOT_GLASS, event::accept);
            appendAllFluids(PSItems.WOODEN_MUG, event);
            appendAllFluids(PSItems.GLASS_CHALICE, event);
            appendAllFluids(PSItems.BOTTLE, event);
            appendAllFluids(PSItems.FILLED_BUCKET, event);
            appendAllFluids(PSItems.FILLED_BOWL, event);
            appendAllFluids(PSItems.FILLED_GLASS_BOTTLE, event);
        } else if (tab.equals(PSItemGroups.WEAPONS)) {
            if (!OrangeSunshine.getConfig().balancing.disableMolotovs) {
                appendAllFluids(PSItems.MOLOTOV_COCKTAIL, event);
            }
        }
    }

    private static void appendAllFluids(FluidContainer item, BuildCreativeModeTabContentsEvent event) {
        SimpleFluid.all().forEach(fluid -> {
            if (fluid.isSuitableContainer(item)) {
                fluid.getDefaultStacks(item, event::accept);
            }
        });
    }
}
