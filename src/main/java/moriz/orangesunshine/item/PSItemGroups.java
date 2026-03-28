/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.fluid.ChemicalExtractFluid;
import moriz.orangesunshine.fluid.PSFluids;
import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.fluid.container.FluidContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

/**
 * @author Sollace
 * @since 1 Jan 2023
 */
public interface PSItemGroups {
    ResourceKey<CreativeModeTab> GENERAL = register("general", CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(PSItems.CANNABIS_LEAF::getDefaultInstance)
            .displayItems((context, entries) -> {
                entries.accept(PSItems.DRYING_TABLE);
                entries.accept(PSItems.IRON_DRYING_TABLE);
                entries.accept(PSItems.FLASK);
                entries.accept(PSItems.DISTILLERY);
                entries.accept(PSItems.BOTTLE_RACK);
                entries.accept(PSItems.MASH_TUB);

                entries.accept(PSItems.OAK_BARREL);
                entries.accept(PSItems.BIRCH_BARREL);
                entries.accept(PSItems.SPRUCE_BARREL);
                entries.accept(PSItems.ACACIA_BARREL);
                entries.accept(PSItems.JUNGLE_BARREL);
                entries.accept(PSItems.DARK_OAK_BARREL);

                entries.accept(PSBlocks.FRIDGE);
                entries.accept(PSBlocks.COMPOUND_EXTRACTOR);
                entries.accept(PSBlocks.COMPOUND_COMPRESSOR);
                entries.accept(PSBlocks.COKE_CAKE_BLOCK);
                entries.accept(PSBlocks.CUT_POPPY_BLOCK);

                if (OrangeSunshine.getConfig().balancing.enableRiftJars) {
                    entries.accept(RiftJarItem.createFilledRiftJar(0.0F, PSItems.RIFT_JAR));
                    entries.accept(RiftJarItem.createFilledRiftJar(0.25F, PSItems.RIFT_JAR));
                    entries.accept(RiftJarItem.createFilledRiftJar(0.55F, PSItems.RIFT_JAR));
                    entries.accept(RiftJarItem.createFilledRiftJar(0.75F, PSItems.RIFT_JAR));
                    entries.accept(RiftJarItem.createFilledRiftJar(0.9F, PSItems.RIFT_JAR));
                }

                entries.accept(PSItems.TRAY);
                entries.accept(PSItems.BUNSEN_BURNER);

                entries.accept(PSItems.SMOKING_PIPE);
                entries.accept(PSItems.RIG);
                entries.accept(PSItems.CIGARETTE);
                entries.accept(PSItems.CIGAR);
                entries.accept(PSItems.JOINT);
                entries.accept(PSItems.CAKE_BAR);
                entries.accept(PSItems.BLOTTER);
                entries.accept(PSItems.ROLLING_PAPER);
                entries.accept(PSItems.JOLLY_RANCHER);
                entries.accept(PSItems.NALOXONE);
                entries.accept(PSItems.FUROSEMIDE);

                entries.accept(PSItems.BONG);
                entries.accept(PSItems.SYRINGE);
                entries.accept(PSItems.SYRINGE.getDefaultStack(PSFluids.COCAINE));
                entries.accept(PSItems.SYRINGE.getDefaultStack(PSFluids.CAFFEINE));
                entries.accept(PSItems.SYRINGE.getDefaultStack(PSFluids.BATH_SALTS));
                entries.accept(ChemicalExtractFluid.DISTILLATION.set(PSItems.SYRINGE.getDefaultStack(PSFluids.MORNING_GLORY_EXTRACT), 2));

                entries.accept(PSItems.COFFEA_CHERRIES);
                entries.accept(PSItems.COFFEE_BEANS);

                entries.accept(PSItems.TOMATO_LEAF);
                entries.accept(PSItems.TOMATO_SEEDS);
                entries.accept(PSItems.TOMATO);

                entries.accept(PSItems.OBSIDIAN_BOTTLE);
                entries.accept(PSItems.OBSIDIAN_DUST);

                entries.accept(PSItems.DMT);
                entries.accept(PSItems.DMT_5_MEO);
                entries.accept(PSItems.AYAHUASCA);
                entries.accept(PSItems.SAN_PEDRO);
                entries.accept(PSItems.SAN_PEDRO_SEEDS);

                entries.accept(PSItems.MDMA);
                entries.accept(PSItems.MDA);
                entries.accept(PSItems.PMA);
                entries.accept(PSItems.KETAMINE_POWDER);
                entries.accept(PSItems.KETAMINE_VIAL);
                entries.accept(PSItems.TWO_CB);

                entries.accept(PSItems.CODEINE);

                entries.accept(PSItems.MORPHINE_BOTTLE);
                entries.accept(PSItems.OPIUM_BOTTLE_0);
                entries.accept(PSItems.OPIUM_BOTTLE_1);
                entries.accept(PSItems.OPIUM_BOTTLE_2);
                entries.accept(PSItems.OPIUM_BOTTLE_3);

               // entries.accept(PSItems.KAVA_SEEDS);
               // entries.accept(PSItems.KAVA_ROOT);

                entries.accept(PSItems.MORNING_GLORY);
                entries.accept(PSItems.MORNING_GLORY_SEEDS);
                entries.accept(PSItems.ERGOT);
                entries.accept(PSItems.ERGOT_POWDER);

                entries.accept(PSItems.SULFUR);
                entries.accept(PSItems.SULFUR_POWDER);
                entries.accept(PSItems.SALT);
                entries.accept(PSItems.SALT_POWDER);
                entries.accept(PSItems.MANGANESE_DIOXIDE);
                entries.accept(PSItems.MANGANESE_DIOXIDE_POWDER);
                entries.accept(PSItems.PHOSPHORUS);

                entries.accept(PSItems.LSA_BLOTTER);
                entries.accept(PSItems.LSD_BLOTTER);
                entries.accept(PSItems.ORANGESUNSINE_BLOTTER);
                entries.accept(PSItems.ETH_HCL);
                entries.accept(PSBlocks.MORTAR_PESTLE);
                entries.accept(PSBlocks.MIXING_TABLE);

                entries.accept(PSItems.DEFAT_ERGOT);
                entries.accept(PSItems.ERGOT_ALKALOIDS);
                entries.accept(PSItems.NEUTRAL_ERGOT_ALKALOIDS);
                entries.accept(PSItems.ERGOPEPTINES);
                entries.accept(PSItems.ERGOPEPTINE_CRYSTALS);
                entries.accept(PSItems.DISSOLVED_ERGOPEPTINES);
                entries.accept(PSItems.DISSOLVED_ERGOPEPTINES_ACID);

                entries.accept(PSItems.LYSERGIC_ACID);

                entries.accept(PSItems.LSD25);
                entries.accept(PSItems.ALD52);

                entries.accept(PSItems.JIMSONWEED_SEEDS);
                entries.accept(PSItems.JIMSONWEED_SEED_POD);
                entries.accept(PSItems.JIMSONWEED_LEAF);
                entries.accept(PSItems.DRIED_JIMSONWEED_LEAF);

                entries.accept(PSItems.BELLADONNA_SEEDS);
                entries.accept(PSItems.BELLADONNA_BERRIES);
                entries.accept(PSItems.BELLADONNA_LEAF);
                entries.accept(PSItems.DRIED_BELLADONNA_LEAF);

                entries.accept(PSItems.TOBACCO_SEEDS);
                entries.accept(PSItems.TOBACCO_LEAVES);
                entries.accept(PSItems.DRIED_TOBACCO);

                entries.accept(PSItems.COCA_SEEDS);
                entries.accept(PSItems.COCA_LEAVES);
                entries.accept(PSItems.DRIED_COCA_LEAVES);
                entries.accept(PSItems.COCAINE_POWDER);
                entries.accept(PSItems.COCAINE_DUST);
                entries.accept(PSItems.COCAINE_ROCK);
                entries.accept(PSItems.COCA_MULCH);

                entries.accept(PSItems.SOURIN_AIR);
                entries.accept(PSItems.COCAINE_SYRINGE);
                entries.accept(PSItems.MORPHINE_SYRINGE);

                entries.accept(PSItems.AGAVE_LEAF);
                entries.accept(PSItems.PEYOTE);
                entries.accept(PSItems.DRIED_PEYOTE);
                entries.accept(PSItems.PEYOTE_JOINT);

                entries.accept(PSItems.SALVIA_SEEDS);
                entries.accept(PSItems.SALVIA_LEAVES);
                entries.accept(PSItems.DRIED_SALVIA);
                entries.accept(PSItems.SALVIA_EXTRACT);

                entries.accept(PSItems.KRATOM_SEEDS);
                entries.accept(PSItems.KRATOM_LEAVES);
                entries.accept(PSItems.DRIED_KRATOM);
                entries.accept(PSItems.KRATOM_POWDER);

                entries.accept(PSItems.HOP_SEEDS);
                entries.accept(PSItems.HOP_CONES);

                entries.accept(PSItems.CANNABIS_SEEDS);
                entries.accept(PSItems.CANNABIS_LEAF);
                entries.accept(PSItems.DRIED_CANNABIS_LEAF);
                entries.accept(PSItems.CANNABIS_BUDS);
                entries.accept(PSItems.DRIED_CANNABIS_BUDS);
                entries.accept(PSItems.WEED_EXTRACT);

                entries.accept(PSItems.HASH_MUFFIN);

                entries.accept(PSItems.BROWN_MAGIC_MUSHROOMS);
                entries.accept(PSItems.RED_MAGIC_MUSHROOMS);
                entries.accept(PSItems.DRIED_BROWN_SHROOMS);
                entries.accept(PSItems.DRIED_RED_SHROOMS);

                entries.accept(PSItems.LATTICE);
                entries.accept(PSItems.WINE_GRAPES);

                entries.accept(PSItems.JUNIPER_LEAVES);
                entries.accept(PSItems.FRUITING_JUNIPER_LEAVES);
                entries.accept(PSItems.JUNIPER_LOG);
                entries.accept(PSItems.JUNIPER_WOOD);
                entries.accept(PSItems.STRIPPED_JUNIPER_LOG);
                entries.accept(PSItems.STRIPPED_JUNIPER_WOOD);
                entries.accept(PSItems.JUNIPER_SAPLING);
                entries.accept(PSItems.JUNIPER_BERRIES);

                entries.accept(PSItems.JUNIPER_PLANKS);
                entries.accept(PSItems.JUNIPER_STAIRS);
                entries.accept(PSItems.JUNIPER_SIGN);
                entries.accept(PSItems.JUNIPER_DOOR);
                entries.accept(PSItems.JUNIPER_HANGING_SIGN);
                entries.accept(PSItems.JUNIPER_PRESSURE_PLATE);
                entries.accept(PSItems.JUNIPER_FENCE);
                entries.accept(PSItems.JUNIPER_TRAPDOOR);
                entries.accept(PSItems.JUNIPER_FENCE_GATE);
                entries.accept(PSItems.JUNIPER_BUTTON);
                entries.accept(PSItems.JUNIPER_SLAB);
                entries.accept(PSItems.JUNIPER_BOAT);
                entries.accept(PSItems.JUNIPER_CHEST_BOAT);

                entries.accept(PSItems.PAPER_BAG);

                if (OrangeSunshine.getConfig().balancing.enableHarmonium) {
                    for (DyeColor dye : DyeColor.values()) {
                        ItemStack harmonium = PSItems.HARMONIUM.getDefaultInstance();
                        PSItems.HARMONIUM.setColor(harmonium, dye.getTextureDiffuseColor());
                        entries.accept(harmonium);
                    }
                }
            }));
    ResourceKey<CreativeModeTab> DRINKS = register("drinks", CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
            .icon(PSItems.OAK_BARREL::getDefaultInstance)
            .displayItems((context, entries) -> {
                appendAllFluids(PSItems.STONE_CUP, entries);
                entries.accept(PSItems.SHOT_GLASS);
                PSFluids.AGAVE.getDefaultStacks(PSItems.SHOT_GLASS, entries::accept);
                appendAllFluids(PSItems.WOODEN_MUG, entries);
                appendAllFluids(PSItems.GLASS_CHALICE, entries);
                appendAllFluids(PSItems.BOTTLE, entries);
                appendAllFluids(PSItems.FILLED_BUCKET, entries);
                appendAllFluids(PSItems.FILLED_BOWL, entries);
                appendAllFluids(PSItems.FILLED_GLASS_BOTTLE, entries);
            }));
    ResourceKey<CreativeModeTab> WEAPONS = register("weapons", CreativeModeTab.builder(CreativeModeTab.Row.TOP, 2)
            .icon(PSItems.MOLOTOV_COCKTAIL::getDefaultInstance)
            .displayItems((context, entries) -> {
                if (!OrangeSunshine.getConfig().balancing.disableMolotovs) {
                    appendAllFluids(PSItems.MOLOTOV_COCKTAIL, entries);
                }
            }));

    private static void appendAllFluids(FluidContainer item, CreativeModeTab.Output entries) {
        SimpleFluid.all().forEach(fluid -> {
            if (fluid.isSuitableContainer(item)) {
                fluid.getDefaultStacks(item, entries::accept);
            }
        });
    }

    static ResourceKey<CreativeModeTab> register(String name, CreativeModeTab.Builder builder) {
        ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, OrangeSunshine.id(name));
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key.identifier(), builder
                .title(Component.translatable(Util.makeDescriptionId("itemGroup", key.identifier())))
                .build()
        );
        return key;
    }

    static void bootstrap() { }
}
