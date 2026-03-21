package com.BrotherHoodOfDiethylamide.OrangeSunshine.items;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.DrugItem.DrugEffectProperties;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final List<Item> ALL_ITEMS = new ArrayList<>();

    // Helper to make drug effect arrays
    private static DrugEffectProperties[] drug(String name, int delay, float potency, int duration) {
        return new DrugEffectProperties[]{new DrugEffectProperties(name, delay, potency, duration)};
    }

    private static DrugEffectProperties[] noDrug() {
        return new DrugEffectProperties[0];
    }

    //----------------------------------------------Shrooms
    public static final Item RED_SHROOMS = simpleItem("red_shrooms");
    public static final Item BROWN_SHROOMS = simpleItem("brown_shrooms");
    public static final DrugItem DRIED_RED_MUSHROOM = drugItem("dried_red_shrooms", drug("red_shrooms", 400, 0.3F, 6900));
    public static final DrugItem DRIED_BROWN_MUSHROOM = drugItem("dried_brown_shrooms", drug("brown_shrooms", 200, 0.3F, 3200));

    //----------------------------------------------Coke
    public static final Item COCAINE_ROCK = simpleItem("cocaine_rock");
    public static final DrugItem COCAINE_POWDER = drugItemAction("cocaine_powder", drug("cocaine", 100, 0.3F, 3200), EnumAction.BOW, 64);
    public static final DrugItem COCAINE_DUST = drugItemAction("cocaine_dust", drug("cocaine", 100, 0.05F, 1000), EnumAction.BOW, 64);
    public static final Item COCA_MULCH = simpleItem("coca_mulch");
    public static final Item COCA_LEAF = simpleItem("coca_leaf");

    //----------------------------------------------Weed
    public static final DrugItem HASH_MUFFIN = new DrugItem("hash_muffin", drug("weed", 800, 0.12F, 3200), 4, 0.3F);
    public static final JointItem WEED_JOINT = jointItem("weed_joint", drug("weed", 0, 0.12F, 3200));
    public static final Item DRIED_WEED_LEAF = simpleItem("dried_weed_leaf");
    public static final Item WEED_LEAF = simpleItem("weed_leaf");
    public static final Item DRIED_WEED_BUD = simpleItem("dried_weed_bud");
    public static final Item WEED_BUD = simpleItem("weed_bud");
    public static final Item WEED_EXTRACT = simpleItem("weed_extract");
    public static final JointItem CAKE_BAR = jointItem("cake_bar", drug("weed", 0, 0.89F, 3200));

    //----------------------------------------------Psychedelics
    public static final DrugItem LSD_BOTTLE = drugItemAction("lsd_bottle", drug("lsd_bottle", 300, 0.70F, 29999), EnumAction.DRINK, 64);
    public static final DrugItem LSD_BLOTTER = drugItem("lsd_blotter", drug("lsd_blotter", 1000, 0.48F, 18888));
    public static final DrugItem ORANGESUNSHINE_BOTTLE = drugItemAction("orangesunshine_bottle", drug("orangesunshine_bottle", 300, 0.6F, 19999), EnumAction.DRINK, 64);
    public static final DrugItem ORANGESUNSHINE_BLOTTER = drugItem("orangesunshine_blotter", drug("orangesunshine_blotter", 1000, 0.48F, 9999));
    public static final DrugItem DMT_ITEM = drugItem("dmt", drug("dmt", 0, 0.4F, 300));
    public static final DrugItem DMT_5_MEO = drugItem("dmt_5_meo", drug("dmt_5_meo", 0, 0.6F, 300));
    public static final DrugItem AYAHUASCA = drugItemAction("ayahuasca", drug("dmt", 200, 0.3F, 1200), EnumAction.DRINK, 64);
    public static final DrugItem PEYOTE = drugItem("peyote", drug("peyote", 500, 0.4F, 5000));
    public static final Item PEYOTE_SEEDS = simpleItem("peyote_seeds");
    public static final DrugItem SAN_PEDRO = drugItem("san_pedro", drug("peyote", 500, 0.4F, 5000));
    public static final Item SAN_PEDRO_SEEDS = simpleItem("san_pedro_seeds");
    public static final DrugItem MESCALINE = drugItemAction("mescaline", drug("peyote", 90, 0.8F, 8000), EnumAction.DRINK, 64);

    //----------------------------------------------Party Drugs
    public static final DrugItem MDMA = drugItem("mdma", drug("mdma", 20, 0.6F, 8000));
    public static final DrugItem MDA = drugItem("mda", drug("mdma", 20, 0.5F, 7000));
    public static final DrugItem PMA = drugItem("pma", drug("mdma", 40, 2.0F, 10000));
    public static final DrugItem CODEINE = drugItemAction("codeine", drug("morphine", 0, 0.2F, 3000), EnumAction.DRINK, 64);

    //----------------------------------------------Nicotine
    public static final Item TOBACCO = simpleItem("tobacco");
    public static final Item DRIED_TOBACCO = simpleItem("dried_tobacco");
    public static final DrugItem NIC = drugItemAction("nic", drug("nic", 0, 0.3F, 2000), EnumAction.DRINK, 64);
    public static final Item TOBACCO_SEEDS = simpleItem("tobacco_seeds");
    public static final JointItem CIGARETTE = jointItem("cigarette", drug("nic", 2, 0.12F, 1000));
    public static final JointItem CIGAR = jointItem("cigar", drug("nic", 2, 0.12F, 1000));
    public static final JointItem SOURIN_AIR = jointItem("sourin_air", drug("nic", 2, 0.12F, 1000));

    //----------------------------------------------Opioids
    public static final DrugItem MORPHINE_BOTTLE = drugItemAction("morphine_bottle", drug("morphine", 0, 0.6F, 4800), EnumAction.DRINK, 16);
    public static final Item OPIUM_BOTTLE_0 = simpleItemStack("opium_bottle_0", 16);
    public static final Item OPIUM_BOTTLE_1 = simpleItemStack("opium_bottle_1", 16);
    public static final Item OPIUM_BOTTLE_2 = simpleItemStack("opium_bottle_2", 16);
    public static final Item OPIUM_BOTTLE_3 = simpleItemStack("opium_bottle_3", 16);

    //----------------------------------------------Block Items (registered separately in ModBlocks)

    //----------------------------------------------Syringes / Glassware
    public static final ClearDrugItem NALOXONE = new ClearDrugItem("naloxone", 0x99cc93);
    public static final ClearDrugItem FUROSEMIDE = new ClearDrugItem("furosemide", 0xdfe3af);
    public static final SyringeItem COCAINE_SYRINGE = syringeItem("cocaine_syringe", drug("cocaine", 0, 0.47F, 4800), 0xFFFFFFFF);
    public static final SyringeItem MORPHINE_SYRINGE = syringeItem("morphine_syringe", drug("morphine", 0, 0.6F, 4800), 0xFF885038);
    public static final BongItem BONG = new BongItem("bong");
    public static final BongItem SMOKING_PIPE = new BongItem("smoking_pipe");
    public static final RigItem RIG = new RigItem("rig");

    //----------------------------------------------Crafting Materials
    public static final Item BLOTTER = simpleItem("blotter");
    public static final Item ROLLING_PAPER = simpleItem("rolling_paper");
    public static final Item EMPTY_SYRINGE = simpleItemStack("syringe", 16);
    public static final Item DE_IONIZED_WATER = simpleItem("de_ionized_water");
    public static final Item VINEGAR = simpleItem("vinegar");
    public static final Item ROOT_BARK = simpleItem("root_bark");
    public static final Item STRAINER = simpleItem("strainer");
    public static final Item SODIUM_HYDROXIDE = simpleItem("sodium_hydroxide");
    public static final Item BARK_SOLUTION_1 = simpleItem("bark_solution_1");
    public static final Item BARK_SOLUTION_2 = simpleItem("bark_solution_2");
    public static final Item BARK_SOLUTION_3 = simpleItem("bark_solution_3");
    public static final Item BARK_SOLUTION_4 = simpleItem("bark_solution_4");
    public static final Item BARK_SOLUTION_5 = simpleItem("bark_solution_5");
    public static final Item ERGOTAMINE = simpleItem("ergotamine");
    public static final Item ERGOROT_INFECTED_WHEAT = simpleItem("ergorot_infected_wheat");
    public static final Item LYSERGIC_ACID = simpleItem("lysergic_acid");
    public static final Item AMMONIA = simpleItem("ammonia");
    public static final Item DIETHYLAMINE = simpleItem("diethylamine");

    //----------------------------------------------Ore
    public static final Item PSYCH_INGOT = simpleItem("psych_ingot");

    //----------------------------------------------Tools
    public static final Item PSYCH_SWORD = register(new ItemSword(ModItemTier.PSYCH_TOOL)
            .setRegistryName(OrangeSunshine.MODID, "psych_sword")
            .setUnlocalizedName(OrangeSunshine.MODID + ".psych_sword")
            .setCreativeTab(OrangeSunshine.CreativeTab)
            .setMaxDamage(10000));
    public static final Item PSYCH_PIC = register(new ItemPickaxe(ModItemTier.PSYCH_TOOL) {}
            .setRegistryName(OrangeSunshine.MODID, "psych_pic")
            .setUnlocalizedName(OrangeSunshine.MODID + ".psych_pic")
            .setCreativeTab(OrangeSunshine.CreativeTab)
            .setMaxDamage(10000));
    public static final Item PSYCH_AXE = register(new ItemAxe(ModItemTier.PSYCH_TOOL, 9.0F, -3.0F) {}
            .setRegistryName(OrangeSunshine.MODID, "psych_axe")
            .setUnlocalizedName(OrangeSunshine.MODID + ".psych_axe")
            .setCreativeTab(OrangeSunshine.CreativeTab)
            .setMaxDamage(10000));
    public static final Item PSYCH_SHOVEL = register(new ItemSpade(ModItemTier.PSYCH_TOOL)
            .setRegistryName(OrangeSunshine.MODID, "psych_shovel")
            .setUnlocalizedName(OrangeSunshine.MODID + ".psych_shovel")
            .setCreativeTab(OrangeSunshine.CreativeTab)
            .setMaxDamage(10000));
    public static final Item PSYCH_HOE = register(new ItemHoe(ModItemTier.PSYCH_TOOL)
            .setRegistryName(OrangeSunshine.MODID, "psych_hoe")
            .setUnlocalizedName(OrangeSunshine.MODID + ".psych_hoe")
            .setCreativeTab(OrangeSunshine.CreativeTab)
            .setMaxDamage(10000));

    //----------------------------------------------Armour
    public static final Item PSYCH_HELMET = register(new ItemArmor(ModArmorMaterial.PSYCH_ARMOR_MATERIAL, 0, EntityEquipmentSlot.HEAD)
            .setRegistryName(OrangeSunshine.MODID, "psych_helmet")
            .setUnlocalizedName(OrangeSunshine.MODID + ".psych_helmet")
            .setCreativeTab(OrangeSunshine.CreativeTab)
            .setMaxDamage(10000));
    public static final Item PSYCH_CHEST = register(new ItemArmor(ModArmorMaterial.PSYCH_ARMOR_MATERIAL, 0, EntityEquipmentSlot.CHEST)
            .setRegistryName(OrangeSunshine.MODID, "psych_chest")
            .setUnlocalizedName(OrangeSunshine.MODID + ".psych_chest")
            .setCreativeTab(OrangeSunshine.CreativeTab)
            .setMaxDamage(10000));
    public static final Item PSYCH_LEGGINGS = register(new ItemArmor(ModArmorMaterial.PSYCH_ARMOR_MATERIAL, 0, EntityEquipmentSlot.LEGS)
            .setRegistryName(OrangeSunshine.MODID, "psych_leggings")
            .setUnlocalizedName(OrangeSunshine.MODID + ".psych_leggings")
            .setCreativeTab(OrangeSunshine.CreativeTab)
            .setMaxDamage(10000));
    public static final Item PSYCH_BOOTS = register(new ItemArmor(ModArmorMaterial.PSYCH_ARMOR_MATERIAL, 0, EntityEquipmentSlot.FEET)
            .setRegistryName(OrangeSunshine.MODID, "psych_boots")
            .setUnlocalizedName(OrangeSunshine.MODID + ".psych_boots")
            .setCreativeTab(OrangeSunshine.CreativeTab)
            .setMaxDamage(10000));

    // ---- Static initializer to register all drug/syringe/bong/rig items ----
    static {
        ALL_ITEMS.add(RED_SHROOMS);
        ALL_ITEMS.add(BROWN_SHROOMS);
        ALL_ITEMS.add(DRIED_RED_MUSHROOM);
        ALL_ITEMS.add(DRIED_BROWN_MUSHROOM);
        ALL_ITEMS.add(COCAINE_ROCK);
        ALL_ITEMS.add(COCAINE_POWDER);
        ALL_ITEMS.add(COCAINE_DUST);
        ALL_ITEMS.add(COCA_MULCH);
        ALL_ITEMS.add(COCA_LEAF);
        ALL_ITEMS.add(HASH_MUFFIN);
        ALL_ITEMS.add(WEED_JOINT);
        ALL_ITEMS.add(DRIED_WEED_LEAF);
        ALL_ITEMS.add(WEED_LEAF);
        ALL_ITEMS.add(DRIED_WEED_BUD);
        ALL_ITEMS.add(WEED_BUD);
        ALL_ITEMS.add(WEED_EXTRACT);
        ALL_ITEMS.add(CAKE_BAR);
        ALL_ITEMS.add(LSD_BOTTLE);
        ALL_ITEMS.add(LSD_BLOTTER);
        ALL_ITEMS.add(ORANGESUNSHINE_BOTTLE);
        ALL_ITEMS.add(ORANGESUNSHINE_BLOTTER);
        ALL_ITEMS.add(DMT_ITEM);
        ALL_ITEMS.add(DMT_5_MEO);
        ALL_ITEMS.add(AYAHUASCA);
        ALL_ITEMS.add(PEYOTE);
        ALL_ITEMS.add(PEYOTE_SEEDS);
        ALL_ITEMS.add(SAN_PEDRO);
        ALL_ITEMS.add(SAN_PEDRO_SEEDS);
        ALL_ITEMS.add(MESCALINE);
        ALL_ITEMS.add(MDMA);
        ALL_ITEMS.add(MDA);
        ALL_ITEMS.add(PMA);
        ALL_ITEMS.add(CODEINE);
        ALL_ITEMS.add(TOBACCO);
        ALL_ITEMS.add(DRIED_TOBACCO);
        ALL_ITEMS.add(NIC);
        ALL_ITEMS.add(TOBACCO_SEEDS);
        ALL_ITEMS.add(CIGARETTE);
        ALL_ITEMS.add(CIGAR);
        ALL_ITEMS.add(SOURIN_AIR);
        ALL_ITEMS.add(MORPHINE_BOTTLE);
        ALL_ITEMS.add(OPIUM_BOTTLE_0);
        ALL_ITEMS.add(OPIUM_BOTTLE_1);
        ALL_ITEMS.add(OPIUM_BOTTLE_2);
        ALL_ITEMS.add(OPIUM_BOTTLE_3);
        ALL_ITEMS.add(NALOXONE);
        ALL_ITEMS.add(FUROSEMIDE);
        ALL_ITEMS.add(COCAINE_SYRINGE);
        ALL_ITEMS.add(MORPHINE_SYRINGE);
        ALL_ITEMS.add(BONG);
        ALL_ITEMS.add(SMOKING_PIPE);
        ALL_ITEMS.add(RIG);
        ALL_ITEMS.add(BLOTTER);
        ALL_ITEMS.add(ROLLING_PAPER);
        ALL_ITEMS.add(EMPTY_SYRINGE);
        ALL_ITEMS.add(DE_IONIZED_WATER);
        ALL_ITEMS.add(VINEGAR);
        ALL_ITEMS.add(ROOT_BARK);
        ALL_ITEMS.add(STRAINER);
        ALL_ITEMS.add(SODIUM_HYDROXIDE);
        ALL_ITEMS.add(BARK_SOLUTION_1);
        ALL_ITEMS.add(BARK_SOLUTION_2);
        ALL_ITEMS.add(BARK_SOLUTION_3);
        ALL_ITEMS.add(BARK_SOLUTION_4);
        ALL_ITEMS.add(BARK_SOLUTION_5);
        ALL_ITEMS.add(ERGOTAMINE);
        ALL_ITEMS.add(ERGOROT_INFECTED_WHEAT);
        ALL_ITEMS.add(LYSERGIC_ACID);
        ALL_ITEMS.add(AMMONIA);
        ALL_ITEMS.add(DIETHYLAMINE);
        ALL_ITEMS.add(PSYCH_INGOT);
        // PSYCH_SWORD through PSYCH_BOOTS are already added via register() above
    }

    public static void initBongables() {
        BongItem.BONGABLES.put(DRIED_WEED_BUD, drug("weed", 0, 0.18F, 3200));
        BongItem.BONGABLES.put(DRIED_WEED_LEAF, drug("weed", 0, 0.08F, 3200));
        BongItem.BONGABLES.put(WEED_EXTRACT, drug("weed", 0, 0.35F, 4000));
        BongItem.BONGABLES.put(DRIED_TOBACCO, drug("nic", 2, 0.12F, 1000));
        BongItem.BONGABLES.put(DMT_ITEM, drug("dmt", 0, 0.4F, 300));
        BongItem.BONGABLES.put(DMT_5_MEO, drug("dmt_5_meo", 0, 0.6F, 300));

        RigItem.RIGABLES.put(WEED_EXTRACT, drug("weed", 0, 0.5F, 4000));
        RigItem.RIGABLES.put(DMT_ITEM, drug("dmt", 0, 0.6F, 400));
        RigItem.RIGABLES.put(DMT_5_MEO, drug("dmt_5_meo", 0, 0.8F, 400));
    }

    // ---- Factory methods ----

    private static Item simpleItem(String name) {
        Item item = new Item()
                .setRegistryName(OrangeSunshine.MODID, name)
                .setUnlocalizedName(OrangeSunshine.MODID + "." + name)
                .setCreativeTab(OrangeSunshine.CreativeTab);
        return item;
    }

    private static Item simpleItemStack(String name, int stackSize) {
        Item item = new Item()
                .setRegistryName(OrangeSunshine.MODID, name)
                .setUnlocalizedName(OrangeSunshine.MODID + "." + name)
                .setCreativeTab(OrangeSunshine.CreativeTab)
                .setMaxStackSize(stackSize);
        return item;
    }

    private static DrugItem drugItem(String name, DrugEffectProperties[] effects) {
        return new DrugItem(name, effects);
    }

    private static DrugItem drugItemAction(String name, DrugEffectProperties[] effects, EnumAction action, int stackSize) {
        return new DrugItem(name, effects, action, stackSize);
    }

    private static JointItem jointItem(String name, DrugEffectProperties[] effects) {
        return new JointItem(name, effects);
    }

    private static SyringeItem syringeItem(String name, DrugEffectProperties[] effects, int color) {
        return new SyringeItem(name, effects, color);
    }

    private static <T extends Item> T register(T item) {
        ALL_ITEMS.add(item);
        return item;
    }
}
