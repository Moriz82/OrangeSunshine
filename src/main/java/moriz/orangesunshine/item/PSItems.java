/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.block.entity.DistilleryBlockEntity;
import moriz.orangesunshine.block.entity.FlaskBlockEntity;
import moriz.orangesunshine.chemistry.CompoundItem;
import moriz.orangesunshine.chemistry.MatterState;
import moriz.orangesunshine.chemistry.MixtureItem;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.fluid.ConsumableFluid;
import moriz.orangesunshine.fluid.FluidVolumes;
import moriz.orangesunshine.util.MathUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

import org.joml.Vector3f;
import moriz.orangesunshine.entity.drug.influence.DrugInfluence;
import moriz.orangesunshine.entity.drug.influence.HarmoniumDrugInfluence;

import java.util.Map;

/**
 * Created by lukas on 25.04.14.
 * Updated by Sollace on 1 Jan 2023
 * Updated by Moriz starting 5/19/2024
 */
public interface PSItems {
    
    //<editor-fold desc="PLANTS">
    ////////////////////////////////////////////// PLANTS //////////////////////////////////////////////

    ////////////////////////// ALCOHOL /////////////////////////////
    Item WINE_GRAPES = register("wine_grapes", new WineGrapesItem(new Item.Properties().setId(itemKey("wine_grapes")).food(
            new FoodProperties.Builder().nutrition(1).saturationModifier(0.5F).build()
    ), 15));

    ////////////////////////// COFFEE /////////////////////////////
    Item COFFEA_CHERRIES = register("coffea_cherries", new BlockItem(PSBlocks.COFFEA, new Item.Properties().setId(itemKey("coffea_cherries"))));
    Item COFFEE_BEANS = register("coffee_beans");

    ////////////////////////// PEYOTE /////////////////////////////
    Item PEYOTE = register("peyote", PSBlocks.PEYOTE);
    Item DRIED_PEYOTE = register("dried_peyote", new EdibleItem(
            new Item.Properties().setId(itemKey("dried_peyote")).food(EdibleItem.NON_FILLING_EDIBLE),
            new DrugInfluence(DrugType.PEYOTE, DrugInfluence.DelayType.INGESTED, 0.005, 0.003, 0.5f)
    ));

    ////////////////////////// WEED /////////////////////////////
    Item CANNABIS_SEEDS = register("cannabis_seeds", new BlockItem(PSBlocks.CANNABIS, new Item.Properties().setId(itemKey("cannabis_seeds"))));
    Item CANNABIS_LEAF = register("cannabis_leaf");
    Item CANNABIS_BUDS = register("cannabis_buds");
    Item DRIED_CANNABIS_LEAF = register("dried_cannabis_leaf");
    Item DRIED_CANNABIS_BUDS = register("dried_cannabis_buds");

    ////////////////////////// LSD / LSA /////////////////////////////
    Item MORNING_GLORY = register("morning_glory");
    Item ERGOT = register("ergot");
    Item ERGOT_POWDER = register("ergot_powder");
    Item MORNING_GLORY_SEEDS = register("morning_glory_seeds", new BlockItem(PSBlocks.MORNING_GLORY, new Item.Properties().setId(itemKey("morning_glory_seeds"))));

    ////////////////////////// HOP /////////////////////////////
    Item HOP_CONES = register("hop_cones");
    Item HOP_SEEDS = register("hop_seeds", new BlockItem(PSBlocks.HOP, new Item.Properties().setId(itemKey("hop_seeds"))));

    ////////////////////////// COKE /////////////////////////////
    Item COCA_SEEDS = register("coca_seeds", new BlockItem(PSBlocks.COCA, new Item.Properties().setId(itemKey("coca_seeds"))));
    Item COCA_LEAVES = register("coca_leaves");
    Item DRIED_COCA_LEAVES = register("dried_coca_leaves");

    ////////////////////////// JIMSONWEED /////////////////////////////
    Item JIMSONWEED_SEEDS = register("jimsonweed_seeds", new BlockItem(PSBlocks.JIMSONWEEED, new Item.Properties().setId(itemKey("jimsonweed_seeds"))));
    Item JIMSONWEED_SEED_POD = register("jimsonweed_seed_pod");
    Item JIMSONWEED_LEAF = register("jimsonweed_leaf");
    Item DRIED_JIMSONWEED_LEAF = register("dried_jimsonweed_leaf");
    ////////////////////////// TOMATO /////////////////////////////
    Item TOMATO_SEEDS = register("tomato_seeds", new BlockItem(PSBlocks.TOMATOES, new Item.Properties().setId(itemKey("tomato_seeds"))));
    Item TOMATO = register("tomato", new Item(new Item.Properties().setId(itemKey("tomato")).food(EdibleItem.TOMATO)));
    Item TOMATO_LEAF = register("tomato_leaf");
    ////////////////////////// BELLADONNA /////////////////////////////
    Item BELLADONNA_SEEDS = register("belladonna_seeds", new BlockItem(PSBlocks.BELLADONNA, new Item.Properties().setId(itemKey("belladonna_seeds"))));
    Item BELLADONNA_LEAF = register("belladonna_leaf");
    Item DRIED_BELLADONNA_LEAF = register("dried_belladonna_leaf");
    Item BELLADONNA_BERRIES = register("belladonna_berries", new EdibleItem(
            new Item.Properties().setId(itemKey("belladonna_berries")).food(new FoodProperties.Builder().nutrition(1).saturationModifier(1.5F).alwaysEdible().build()),
            new DrugInfluence(DrugType.ATROPINE, DrugInfluence.DelayType.INGESTED, 0.005, 0.003, 0.5f)
    ));
    ////////////////////////// AGAVE /////////////////////////////
    Item AGAVE_LEAF = register("agave_leaf", new BlockItem(PSBlocks.AGAVE_PLANT, new Item.Properties().setId(itemKey("agave_leaf"))));

    ////////////////////////// PSYCHEDELICS /////////////////////////////
    Item DMT = register("dmt");
    Item AYAHUASCA = register("ayahuasca");
    Item SAN_PEDRO = register("san_pedro");
    Item SAN_PEDRO_SEEDS = register("san_pedro_seeds", new BlockItem(PSBlocks.SAN_PEDRO_BLOCK, new Item.Properties().setId(itemKey("san_pedro_seeds"))));
    //</editor-fold>
    
    //<editor-fold desc="CRAFT-ABLE DRUGS">
    ////////////////////////////////////////////// CRAFT-ABLE DRUGS //////////////////////////////////////////////

    ////////////////////////// WEED /////////////////////////////
    Item HASH_MUFFIN = register("hash_muffin", new EdibleItem(
            new Item.Properties().setId(itemKey("hash_muffin")).food(EdibleItem.HAS_MUFFIN),
            new DrugInfluence(DrugType.CANNABIS, DrugInfluence.DelayType.METABOLISED, 0.004, 0.002, 0.7f)
    ));

    ////////////////////////// MUSHROOMS /////////////////////////////

    Item BROWN_MAGIC_MUSHROOMS = register("brown_magic_mushrooms", new EdibleItem(
            new Item.Properties().setId(itemKey("brown_magic_mushrooms")).food(EdibleItem.NON_FILLING_EDIBLE),
            new DrugInfluence(DrugType.BROWN_SHROOMS, DrugInfluence.DelayType.INGESTED, 0.005, 0.003, 0.5f)
    ));
    Item RED_MAGIC_MUSHROOMS = register("red_magic_mushrooms", new EdibleItem(
            new Item.Properties().setId(itemKey("red_magic_mushrooms")).food(EdibleItem.NON_FILLING_EDIBLE),
            new DrugInfluence(DrugType.RED_SHROOMS, DrugInfluence.DelayType.INGESTED, 0.005, 0.003, 0.5f)
    ));

    ////////////////////////// TOBACCO /////////////////////////////

    Item TOBACCO_LEAVES = register("tobacco");
    Item TOBACCO_SEEDS = register("tobacco_seeds", new BlockItem(PSBlocks.TOBACCO, new Item.Properties().setId(itemKey("tobacco_seeds"))));
    Item DRIED_TOBACCO = register("dried_tobacco");

    ////////////////////////// COKE /////////////////////////////

    Item COCAINE_POWDER = register("cocaine_powder", new CocainePowderItem(
            new Item.Properties().setId(itemKey("cocaine_powder")).food(EdibleItem.NON_FILLING_EDIBLE),
            new DrugInfluence(DrugType.COCAINE, DrugInfluence.DelayType.IMMEDIATE, 0.002, 0.003, 0.35f)
    ));
    Item COCAINE_ROCK = register("cocaine_rock");
    Item COCA_MULCH = register("coca_mulch");

    ////////////////////////// AMPHETAMINES /////////////////////////////
    Item MDMA = register("mdma");
    Item MDA = register("mda");
    Item PMA = register("pma");

    ////////////////////////// OPIATES /////////////////////////////
    Item CODEINE = register("codeine");
    Item MORPHINE_BOTTLE = register("morphine_bottle");
    Item OPIUM_BOTTLE_0 = register("opium_bottle_0");
    Item OPIUM_BOTTLE_1 = register("opium_bottle_1");
    Item OPIUM_BOTTLE_2 = register("opium_bottle_2");
    Item OPIUM_BOTTLE_3 = register("opium_bottle_3");

    ////////////////////////// LATTICE /////////////////////////////

    Item LATTICE = register("lattice", PSBlocks.LATTICE);
    Item WINE_GRAPE_LATTICE = register("wine_grape_lattice", PSBlocks.WINE_GRAPE_LATTICE);
    Item MORNING_GLORY_LATTICE = register("morning_glory_lattice", PSBlocks.MORNING_GLORY_LATTICE);

    ////////////////////////// OTHER /////////////////////////////
    HarmoniumItem HARMONIUM = register("harmonium", new HarmoniumItem(new Item.Properties().setId(itemKey("harmonium"))));
    Item BLOTTER = register("blotter");
    Item ROLLING_PAPER = register("rolling_paper");
    Item JOLLY_RANCHER = register("jolly_rancher", new EdibleItem(
            new Item.Properties().setId(itemKey("jolly_rancher")).food(EdibleItem.NON_FILLING_EDIBLE),
            new DrugInfluence(DrugType.SUGAR, DrugInfluence.DelayType.INGESTED, 0.005, 0.003, 0.05f)
    ));

    Item NALOXONE = register("naloxone", new DrugClearItem(new Item.Properties().setId(itemKey("naloxone")).food(EdibleItem.NON_FILLING_EDIBLE), ItemUseAnimation.BOW, 1));

    //</editor-fold>

    //<editor-fold desc="LSD / LSA">
    Item LSA_BLOTTER = register("lsa_blotter", new EdibleItem(
            new Item.Properties().setId(itemKey("lsa_blotter")).food(EdibleItem.NON_FILLING_EDIBLE),
            new DrugInfluence(DrugType.LSD, DrugInfluence.DelayType.CONTACT, 0.05, 0.003, 0.1F)
    ));
    Item LSD_BLOTTER = register("lsd_blotter", new EdibleItem(
            new Item.Properties().setId(itemKey("lsd_blotter")).food(EdibleItem.NON_FILLING_EDIBLE),
            new DrugInfluence(DrugType.LSD, DrugInfluence.DelayType.CONTACT, 0.06, 0.004, 0.25F)
    ));
    Item ORANGESUNSINE_BLOTTER = register("orangesunshine_blotter", new EdibleItem(
            new Item.Properties().setId(itemKey("orangesunshine_blotter")).food(EdibleItem.NON_FILLING_EDIBLE),
            new DrugInfluence(DrugType.LSD, DrugInfluence.DelayType.CONTACT, 0.06, 0.006, 0.4F)
    ));

    //</editor-fold>

    //<editor-fold desc="CHEMISTRY COMPOUNDS / SOLUTIONS">

    CompoundItem LSD25 = registerCompound(new CompoundItem("lsd25", MatterState.VIAL,
            Map.of(
                    "C",20,
                    "H", 25,
                    "N",3,
                    "O",1
            ),
            "FFA021BF"));
    CompoundItem ALD52 = registerCompound(new CompoundItem("ald52", MatterState.VIAL,
            Map.of(
                    "C",22,
                    "H", 27,
                    "N",3,
                    "O",2
            ),
            "fa8405"));
    CompoundItem LYSERGIC_ACID = registerCompound(new CompoundItem("lysergic_acid", MatterState.VIAL,
            Map.of(
                    "C",16,
                    "H", 16,
                    "N",2,
                    "O",2
            ),
            "e802e0"));

    MixtureItem ETH_HCL = registerMixture(new MixtureItem("eth_hcl", MatterState.BEAKER,
            Map.of(
                    "Ethanol",1,
                    "HCL", 1
            ), "9c938a"));
    MixtureItem DEFAT_ERGOT = registerMixture(new MixtureItem("defat_ergot", MatterState.BEAKER,
            Map.of(
                    "Ergot",1,
                    "Ethanol HCL", 1
            ), "524940"));
    MixtureItem ERGOT_ALKALOIDS = registerMixture(new MixtureItem("ergot_alkaloids", MatterState.BEAKER,
            Map.of(
                    "Ergot Alkaloids",1,
                    "Ethanol HCL", 1
            ),
            "453627"));
    MixtureItem NEUTRAL_ERGOT_ALKALOIDS = registerMixture(new MixtureItem("neutral_ergot_alkaloids", MatterState.BEAKER,
            Map.of(
                    "Ergot Alkaloids",1,
                    "Ethanol HCL", 1,
                    "NaOH", 1
            ),
            "99a140"));
    MixtureItem ERGOPEPTINES = registerMixture(new MixtureItem("ergopeptines", MatterState.BEAKER,
            Map.of(
                    "Ergopeptines",1,
                    "Ethanol HCL", 1,
                    "NaOH", 1
            ),
            "f5df87"));
    Item ERGOPEPTINE_CRYSTALS = register("ergopeptine_crystals");
    MixtureItem DISSOLVED_ERGOPEPTINES = registerMixture(new MixtureItem("dissolved_ergopeptines", MatterState.BEAKER,
            Map.of(
                    "Ergopeptines",1,
                    "water", 1,
                    "Sulfuric Acid", 1
            ),
            "806a13"));
    MixtureItem DISSOLVED_ERGOPEPTINES_ACID = registerMixture(new MixtureItem("dissolved_ergopeptines_acid", MatterState.BEAKER,
            Map.of(
                    "Ergopeptines",1,
                    "Sulfuric Acid", 1
            ),
            "e8ba02"));
    //</editor-fold>

    //<editor-fold desc="DRINKING CONTAINERS">
    ////////////////////////////////////////////// DRINKING CONTAINERS //////////////////////////////////////////////

    DrinkableItem WOODEN_MUG = register("wooden_mug", new DrinkableItem(new Item.Properties().setId(itemKey("wooden_mug")), FluidVolumes.MUG, DrinkableItem.FLUID_PER_DRINKING, 32, ConsumableFluid.ConsumptionType.DRINK));
    DrinkableItem STONE_CUP = register("stone_cup", new DrinkableItem(new Item.Properties().setId(itemKey("stone_cup")), FluidVolumes.CUP, DrinkableItem.FLUID_PER_DRINKING, 32, ConsumableFluid.ConsumptionType.DRINK));
    DrinkableItem GLASS_CHALICE = register("glass_chalice", new DrinkableItem(new Item.Properties().setId(itemKey("glass_chalice")), FluidVolumes.CHALLICE, DrinkableItem.FLUID_PER_DRINKING, 32, ConsumableFluid.ConsumptionType.DRINK));
    DrinkableItem SHOT_GLASS = register("shot_glass", new DrinkableItem(new Item.Properties().setId(itemKey("shot_glass")), FluidVolumes.SHOT, DrinkableItem.FLUID_PER_DRINKING, 8, ConsumableFluid.ConsumptionType.DRINK));
    DrinkableItem BOTTLE = register("bottle", new BottleItem(new Item.Properties().setId(itemKey("bottle")), FluidVolumes.BOTTLE, DrinkableItem.FLUID_PER_DRINKING, ConsumableFluid.ConsumptionType.DRINK));
    //</editor-fold>

    //<editor-fold desc="ALC CONTAINERS">
    ////////////////////////////////////////////// ALCOHOL CONTAINERS //////////////////////////////////////////////

    FlaskItem OAK_BARREL = register("oak_barrel", new FlaskItem(PSBlocks.OAK_BARREL, new Item.Properties().setId(itemKey("oak_barrel")).stacksTo(16), FluidVolumes.BARREL));
    FlaskItem SPRUCE_BARREL = register("spruce_barrel", new FlaskItem(PSBlocks.SPRUCE_BARREL, new Item.Properties().setId(itemKey("spruce_barrel")).stacksTo(16), FluidVolumes.BARREL));
    FlaskItem BIRCH_BARREL = register("birch_barrel", new FlaskItem(PSBlocks.BIRCH_BARREL, new Item.Properties().setId(itemKey("birch_barrel")).stacksTo(16), FluidVolumes.BARREL));
    FlaskItem JUNGLE_BARREL = register("jungle_barrel", new FlaskItem(PSBlocks.JUNGLE_BARREL, new Item.Properties().setId(itemKey("jungle_barrel")).stacksTo(16), FluidVolumes.BARREL));
    FlaskItem ACACIA_BARREL = register("acacia_barrel", new FlaskItem(PSBlocks.ACACIA_BARREL, new Item.Properties().setId(itemKey("acacia_barrel")).stacksTo(16), FluidVolumes.BARREL));
    FlaskItem DARK_OAK_BARREL = register("dark_oak_barrel", new FlaskItem(PSBlocks.DARK_OAK_BARREL, new Item.Properties().setId(itemKey("dark_oak_barrel")).stacksTo(16), FluidVolumes.BARREL));
    //</editor-fold>

    //<editor-fold desc="WOOD ITEMS">
    ////////////////////////////////////////////// WOOD ITEMS //////////////////////////////////////////////
    Item JUNIPER_LEAVES = register("juniper_leaves", PSBlocks.JUNIPER_LEAVES);
    Item FRUITING_JUNIPER_LEAVES = register("fruiting_juniper_leaves", PSBlocks.FRUITING_JUNIPER_LEAVES);
    Item JUNIPER_LOG = register("juniper_log", PSBlocks.JUNIPER_LOG);
    Item JUNIPER_WOOD = register("juniper_wood", PSBlocks.JUNIPER_WOOD);
    Item STRIPPED_JUNIPER_LOG = register("stripped_juniper_log", PSBlocks.STRIPPED_JUNIPER_LOG);
    Item STRIPPED_JUNIPER_WOOD = register("stripped_juniper_wood", PSBlocks.STRIPPED_JUNIPER_WOOD);
    Item JUNIPER_BERRIES = register("juniper_berries", new SpecialFoodItem(
            new Item.Properties().setId(itemKey("juniper_berries")).food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.5F).build()), 15
    ));
    Item JUNIPER_SAPLING = register("juniper_sapling", PSBlocks.JUNIPER_SAPLING);
    Item JUNIPER_PLANKS = register("juniper_planks", PSBlocks.JUNIPER_PLANKS);
    Item JUNIPER_STAIRS = register("juniper_stairs", PSBlocks.JUNIPER_STAIRS);
    Item JUNIPER_SIGN = register("juniper_sign", new SignItem(PSBlocks.JUNIPER_SIGN, PSBlocks.JUNIPER_WALL_SIGN, new Item.Properties().setId(itemKey("juniper_sign")).stacksTo(16)));
    Item JUNIPER_DOOR = register("juniper_door", PSBlocks.JUNIPER_DOOR);
    Item JUNIPER_HANGING_SIGN = register("juniper_hanging_sign", new HangingSignItem(PSBlocks.JUNIPER_HANGING_SIGN, PSBlocks.JUNIPER_WALL_HANGING_SIGN, new Item.Properties().setId(itemKey("juniper_hanging_sign")).stacksTo(16)));
    Item JUNIPER_PRESSURE_PLATE = register("juniper_pressure_plate", PSBlocks.JUNIPER_PRESSURE_PLATE);
    Item JUNIPER_FENCE = register("juniper_fence", PSBlocks.JUNIPER_FENCE);
    Item JUNIPER_TRAPDOOR = register("juniper_trapdoor", PSBlocks.JUNIPER_TRAPDOOR);
    Item JUNIPER_FENCE_GATE = register("juniper_fence_gate", PSBlocks.JUNIPER_FENCE_GATE);
    Item JUNIPER_BUTTON = register("juniper_button", PSBlocks.JUNIPER_BUTTON);
    Item JUNIPER_SLAB = register("juniper_slab", PSBlocks.JUNIPER_SLAB);
    Item JUNIPER_BOAT = register("juniper_boat", new Item(new Item.Properties().setId(itemKey("juniper_boat")).stacksTo(1)));
    Item JUNIPER_CHEST_BOAT = register("juniper_chest_boat", new Item(new Item.Properties().setId(itemKey("juniper_chest_boat")).stacksTo(1)));
    //</editor-fold>

    //<editor-fold desc="PROCESSING TABLES">
    ////////////////////////////////////////////// PROCESSING TABLES //////////////////////////////////////////////

    ////////////////////////// ALCOHOL /////////////////////////////
    FlaskItem MASH_TUB = register("mash_tub", new MashTubItem(PSBlocks.MASH_TUB, new Item.Properties().setId(itemKey("mash_tub")).stacksTo(16), FluidVolumes.VAT));
    Item BOTTLE_RACK = register("bottle_rack", PSBlocks.BOTTLE_RACK);
    FlaskItem FLASK = register("flask", new FlaskItem(PSBlocks.FLASK, new Item.Properties().setId(itemKey("flask")).stacksTo(16), FlaskBlockEntity.FLASK_CAPACITY));
    FlaskItem DISTILLERY = register("distillery", new FlaskItem(PSBlocks.DISTILLERY, new Item.Properties().setId(itemKey("distillery")).stacksTo(16), DistilleryBlockEntity.DISTILLERY_CAPACITY));

    ////////////////////////// DRYING /////////////////////////////
    Item DRYING_TABLE = register("drying_table", PSBlocks.DRYING_TABLE);
    Item IRON_DRYING_TABLE = register("iron_drying_table", PSBlocks.IRON_DRYING_TABLE);

    ////////////////////////// MISC /////////////////////////////
    Item TRAY = register("tray", PSBlocks.TRAY);
    Item BUNSEN_BURNER = register("bunsen_burner", PSBlocks.BUNSEN_BURNER);

    Item OBSIDIAN_BOTTLE = register("obsidian_bottle", new Item(new Item.Properties().setId(itemKey("obsidian_bottle")).stacksTo(16)));
    Item OBSIDIAN_DUST = register("obsidian_dust", new CocainePowderItem(
            new Item.Properties().setId(itemKey("obsidian_dust")).food(EdibleItem.NON_FILLING_EDIBLE),
            new DrugInfluence(DrugType.BATH_SALTS, DrugInfluence.DelayType.IMMEDIATE, 0.002, 0.003, 0.35f)
    ));
    //</editor-fold>

    //<editor-fold desc="SMOKE-ABLES">
    ////////////////////////////////////////////// SMOKE-ABLES //////////////////////////////////////////////

    ////////////////////////// UNIVERSAL /////////////////////////////

    BongItem SMOKING_PIPE = register("smoking_pipe", new BongItem(new Item.Properties().setId(itemKey("smoking_pipe")).durability(50)))
            .consumes(new BongItem.Consumable(DRIED_CANNABIS_BUDS.getDefaultInstance(), new DrugInfluence(DrugType.CANNABIS, DrugInfluence.DelayType.INHALED, 0.002, 0.001, 0.25F)))
            .consumes(new BongItem.Consumable(DRIED_TOBACCO.getDefaultInstance(), new DrugInfluence(DrugType.TOBACCO, DrugInfluence.DelayType.INHALED, 0.1, 0.02, 0.8F)))
            .consumes(new BongItem.Consumable(DRIED_BELLADONNA_LEAF.getDefaultInstance(), new DrugInfluence(DrugType.ATROPINE, DrugInfluence.DelayType.INHALED, 0.4, 0.1, 0.9F)))
            .consumes(new BongItem.Consumable(DRIED_JIMSONWEED_LEAF.getDefaultInstance(), new DrugInfluence(DrugType.ATROPINE, DrugInfluence.DelayType.INHALED, 0.5, 0.1, 0.2F)))
            .consumes(new BongItem.Consumable(HARMONIUM.getDefaultInstance(), stack -> new HarmoniumDrugInfluence(DrugInfluence.DelayType.INHALED, 0.04, 0.01, 0.65F, MathUtils.unpackRgb(HARMONIUM.getColor(stack)))));
    // TODO: Play around with the bongs benefits
    BongItem BONG = register("bong", new BongItem(new Item.Properties().setId(itemKey("bong")).durability(128)))
            .consumes(new BongItem.Consumable(DRIED_CANNABIS_BUDS.getDefaultInstance(), new DrugInfluence(DrugType.CANNABIS, DrugInfluence.DelayType.IMMEDIATE, 0.002, 0.001, 0.2F)))
            .consumes(new BongItem.Consumable(DRIED_TOBACCO.getDefaultInstance(), new DrugInfluence(DrugType.TOBACCO, DrugInfluence.DelayType.IMMEDIATE, 0.1, 0.02, 0.6F)))
            .consumes(new BongItem.Consumable(DRIED_BELLADONNA_LEAF.getDefaultInstance(), new DrugInfluence(DrugType.ATROPINE, DrugInfluence.DelayType.IMMEDIATE, 0.4, 0.1, 0.4F)))
            .consumes(new BongItem.Consumable(DRIED_JIMSONWEED_LEAF.getDefaultInstance(), new DrugInfluence(DrugType.ATROPINE, DrugInfluence.DelayType.IMMEDIATE, 0.5, 0.1, 0.1F)))
            .consumes(new BongItem.Consumable(HARMONIUM.getDefaultInstance(), stack -> new HarmoniumDrugInfluence(DrugInfluence.DelayType.IMMEDIATE, 0.04, 0.01, 0.9F, MathUtils.unpackRgb(HARMONIUM.getColor(stack)))));

    ////////////////////////// TOBACCO /////////////////////////////
    SmokeableItem CIGARETTE = register("cigarette", new SmokeableItem(
            new Item.Properties().setId(itemKey("cigarette")).durability(1), 2, SmokeableItem.WHITE,
            new DrugInfluence(DrugType.TOBACCO, DrugInfluence.DelayType.IMMEDIATE, 0.1, 0.02, 0.7f)
    ));
    SmokeableItem CIGAR = register("cigar", new SmokeableItem(
            new Item.Properties().setId(itemKey("cigar")).durability(3), 4, new Vector3f(0.6F, 0.6F, 0.5F),
            new DrugInfluence(DrugType.TOBACCO, DrugInfluence.DelayType.IMMEDIATE, 0.1, 0.02, 0.7f)
    ));

    ////////////////////////// WEED /////////////////////////////
    SmokeableItem JOINT = register("joint", new SmokeableItem(
            new Item.Properties().setId(itemKey("joint")).durability(2), 2, new Vector3f(0.9F, 0.9F, 0.9F),
            new DrugInfluence(DrugType.CANNABIS, DrugInfluence.DelayType.INHALED, 0.002, 0.001, 0.20f)
    ));

    ////////////////////////// PSYCHE MISC /////////////////////////////
    Item PEYOTE_JOINT = register("peyote_joint", new SmokeableItem(
            new Item.Properties().setId(itemKey("peyote_joint")).durability(2), 2, new Vector3f(0.5F, 0.9F, 0.4F),
            new DrugInfluence(DrugType.PEYOTE, DrugInfluence.DelayType.INHALED, 0.003, 0.0015, 0.4f),
            new DrugInfluence(DrugType.TOBACCO, DrugInfluence.DelayType.IMMEDIATE, 0.1, 0.02, 0.1f)
    ));
    //</editor-fold>

    //<editor-fold desc="INJECTABLES">
    ////////////////////////////////////////////// INJECTABLES //////////////////////////////////////////////
    InjectableItem SYRINGE = register("syringe", new InjectableItem(new Item.Properties().setId(itemKey("syringe")), FluidVolumes.SYRINGE));
    //</editor-fold>

    //<editor-fold desc="MISC">

    Item SULFUR = register("sulfur");
    Item SULFUR_POWDER = register("sulfur_powder");
    Item SALT = register("salt");
    Item SALT_POWDER = register("salt_powder");
    Item MANGANESE_DIOXIDE = register("manganese_dioxide");
    Item MANGANESE_DIOXIDE_POWDER = register("manganese_dioxide_powder");
    Item PHOSPHORUS = register("phosphorus");
    RiftJarItem RIFT_JAR = register("rift_jar", new RiftJarItem(PSBlocks.RIFT_JAR, new Item.Properties().setId(itemKey("rift_jar"))));

    DrinkableItem FILLED_GLASS_BOTTLE = register("filled_glass_bottle", new ProxyDrinkableItem(Items.GLASS_BOTTLE, new Item.Properties().setId(itemKey("filled_glass_bottle")), FluidVolumes.GLASS_BOTTLE, ConsumableFluid.ConsumptionType.DRINK));
    FilledBucketItem FILLED_BUCKET = register("filled_bucket", new FilledBucketItem(new Item.Properties().setId(itemKey("filled_bucket")).stacksTo(1)));
    DrinkableItem FILLED_BOWL = register("filled_bowl", new ProxyDrinkableItem(Items.BOWL, new Item.Properties().setId(itemKey("filled_bowl")), FluidVolumes.BOWL, ConsumableFluid.ConsumptionType.DRINK));

    MolotovCocktailItem MOLOTOV_COCKTAIL = register("molotov_cocktail", new MolotovCocktailItem(new Item.Properties().setId(itemKey("molotov_cocktail")).stacksTo(16), FluidVolumes.BOTTLE));
    Item VOMIT = register("vomit", new Item(new Item.Properties().setId(itemKey("vomit"))));
    Item PAPER_BAG = register("paper_bag", new PaperBagItem(new Item.Properties().setId(itemKey("paper_bag"))));
    Item BAG_O_VOMIT = register("bag_o_vomit", new SuspiciousItem(new Item.Properties().setId(itemKey("bag_o_vomit"))
            .food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.8f).alwaysEdible().build()
            ), SuspiciousItem.createForms(Items.COOKIE, Items.MUSHROOM_STEW, Items.GOLDEN_APPLE, Items.COOKED_BEEF, Items.COOKED_CHICKEN)));

    ////////////////////////// OTHER /////////////////////////////
    SmokeableItem SOURIN_AIR = register("sourin_air", new SmokeableItem(
            new Item.Properties().setId(itemKey("sourin_air")).durability(20), 1, new Vector3f(0.9F, 0.9F, 1.0F),
            new DrugInfluence(DrugType.TOBACCO, DrugInfluence.DelayType.IMMEDIATE, 0.15, 0.03, 0.5f),
            new DrugInfluence(DrugType.CAFFEINE, DrugInfluence.DelayType.IMMEDIATE, 0.05, 0.01, 0.15f)
    ));
    Item COCAINE_SYRINGE = register("cocaine_syringe", new InjectableItem(new Item.Properties().setId(itemKey("cocaine_syringe")).stacksTo(1), FluidVolumes.SYRINGE));
    Item MORPHINE_SYRINGE = register("morphine_syringe", new InjectableItem(new Item.Properties().setId(itemKey("morphine_syringe")).stacksTo(1), FluidVolumes.SYRINGE));
    //</editor-fold>

    static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, OrangeSunshine.id(name));
    }

    static Item register(String name, Block block) {
        return register(name, new BlockItem(block, new Item.Properties().setId(itemKey(name))));
    }

    static Item register(String name) {
        return register(name, new Item(new Item.Properties().setId(itemKey(name))));
    }

    static <T extends Item> T register(String name, T item) {
        return Registry.register(BuiltInRegistries.ITEM, OrangeSunshine.id(name), item);
    }

    static CompoundItem registerCompound(CompoundItem item){
        return register(item.getChemicalName(), item);
    }

    static MixtureItem registerMixture(MixtureItem item){
        return register(item.getChemicalName(), item);
    }

    static void bootstrap() { }
}
