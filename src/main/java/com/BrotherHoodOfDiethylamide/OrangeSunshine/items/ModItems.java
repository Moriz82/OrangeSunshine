package com.BrotherHoodOfDiethylamide.OrangeSunshine.items;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ModBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugRegistry;
import net.minecraft.block.Block;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.RegistryKey;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItems {
    //----------------------------------------------Shrooms
    public static final RegistryObject<Item> RED_SHROOMS = registerBlock("red_shrooms", ModBlocks.RED_MUSHROOM_BLOCK);
    public static final RegistryObject<Item> BROWN_SHROOMS = registerBlock("brown_shrooms", ModBlocks.BROWN_MUSHROOM_BLOCK);
    public static final RegistryObject<DrugItem> DRIED_RED_MUSHROOM = registerDrug("dried_red_shrooms", new DrugChain().add(DrugRegistry.RED_SHROOMS, 400, 0.3F, 6900));
    public static final RegistryObject<DrugItem> DRIED_BROWN_MUSHROOM = registerDrug("dried_brown_shrooms", new DrugChain().add(DrugRegistry.BROWN_SHROOMS, 200, 0.3F, 3200));

    //----------------------------------------------Coke
    public static final RegistryObject<Item> COCAINE_ROCK = registerItem("cocaine_rock");
    public static final RegistryObject<DrugItem> COCAINE_POWDER = registerDrug("cocaine_powder", new DrugChain().add(DrugRegistry.COCAINE, 100, 0.3F, 3200), UseAction.BOW, 64);
    public static final RegistryObject<DrugItem> COCAINE_DUST = registerDrug("cocaine_dust", new DrugChain().add(DrugRegistry.COCAINE, 100, 0.05F, 1000), UseAction.BOW, 64);
    public static final RegistryObject<Item> COCA_MULCH = registerItem("coca_mulch");
    public static final RegistryObject<Item> COCA_LEAF = registerItem("coca_leaf");
    //----------------------------------------------Weed
    public static final RegistryObject<Item> HASH_MUFFIN = registerItem("hash_muffin", () -> new DrugItem(new DrugItem.Properties().addDrug(DrugRegistry.WEED, 800, 0.12F, 3200).food(ModFoods.HASH_MUFFIN).tab(OrangeSunshine.TAB)));
    public static final RegistryObject<JointItem> WEED_JOINT = registerJoint("weed_joint", new DrugChain().add(DrugRegistry.WEED, 0, 0.12F, 3200));
    public static final RegistryObject<Item> DRIED_WEED_LEAF = registerItem("dried_weed_leaf");
    public static final RegistryObject<Item> WEED_LEAF = registerItem("weed_leaf");
    public static final RegistryObject<Item> DRIED_WEED_BUD = registerItem("dried_weed_bud");
    public static final RegistryObject<Item> WEED_BUD = registerItem("weed_bud");
    public static final RegistryObject<Item> WEED_EXTRACT = registerItem("weed_extract");
    public static final RegistryObject<JointItem> CAKE_BAR = registerJoint("cake_bar", new DrugChain().add(DrugRegistry.WEED, 0, 0.89f, 3200));
    //----------------------------------------------Psych's
    public static final RegistryObject<Item> LSD_BOTTLE = registerItem("lsd_bottle", () -> new DrugItem(new DrugItem.Properties().addDrug(DrugRegistry.LSD_BOTTLE, 300, 0.70F, 29999).useAction(UseAction.DRINK).food(ModFoods.LSD_BOTTLE).tab(OrangeSunshine.TAB)));
    public static final RegistryObject<Item> LSD_BLOTTER = registerItem("lsd_blotter", () -> new DrugItem(new DrugItem.Properties().addDrug(DrugRegistry.LSD_BLOTTER, 1000, 0.48F, 18888).food(ModFoods.LSD_BLOTTER).tab(OrangeSunshine.TAB)));
    public static final RegistryObject<Item> ORANGESUNSHINE_BOTTLE = registerItem("orangesunshine_bottle", () -> new DrugItem(new DrugItem.Properties().addDrug(DrugRegistry.ORANGESUNSHINE_BOTTLE, 300, 0.6F, 19999).useAction(UseAction.DRINK).food(ModFoods.ORANGESUNSHINE_BOTTLE).tab(OrangeSunshine.TAB)));
    public static final RegistryObject<Item> ORANGESUNSHINE_BLOTTER = registerItem("orangesunshine_blotter", () -> new DrugItem(new DrugItem.Properties().addDrug(DrugRegistry.ORANGESUNSHINE_BLOTTER, 1000, 0.48F, 9999).food(ModFoods.ORANGESUNSHINE_BLOTTER).tab(OrangeSunshine.TAB)));
    public static final RegistryObject<Item> DMT = registerItem("dmt", () -> new DrugItem(new DrugItem.Properties().addDrug(DrugRegistry.DMT, 0, 0.4f, 300).food(ModFoods.DMT).tab(OrangeSunshine.TAB)));
    public static final RegistryObject<Item> DMT_5_MEO = registerItem("dmt_5_meo", () -> new DrugItem(new DrugItem.Properties().addDrug(DrugRegistry.DMT_5_MEO, 0, 0.6f, 300).food(ModFoods.DMT).tab(OrangeSunshine.TAB)));
    public static final RegistryObject<Item> AYAHUASCA = registerBlockNamed("ayahuasca", ModBlocks.AYAHUASCA_BLOCK);
    public static final RegistryObject<Item> PEYOTE = registerItem("peyote", () -> new DrugItem(new DrugItem.Properties().addDrug(DrugRegistry.CACTUS_DRUG, 500, 0.4F, 5000).food(ModFoods.LSD_BLOTTER).tab(OrangeSunshine.TAB)));
    public static final RegistryObject<Item> PEYOTE_SEEDS = registerBlock("peyote_seeds", ModBlocks.PEYOTE_BLOCK);
    public static final RegistryObject<Item> SAN_PEDRO = registerItem("san_pedro", () -> new DrugItem(new DrugItem.Properties().addDrug(DrugRegistry.CACTUS_DRUG, 500, 0.4F, 5000).food(ModFoods.LSD_BLOTTER).tab(OrangeSunshine.TAB)));
    public static final RegistryObject<Item> SAN_PEDRO_SEEDS = registerBlock("san_pedro_seeds", ModBlocks.SAN_PEDRO_BLOCK);
    public static final RegistryObject<Item> MESCALINE = registerItem("mescaline", () -> new DrugItem(new DrugItem.Properties().addDrug(DrugRegistry.CACTUS_DRUG, 90, 0.8F, 8000).useAction(UseAction.DRINK).food(ModFoods.LSD_BLOTTER).tab(OrangeSunshine.TAB)));
    //----------------------------------------------PARTY
    public static final RegistryObject<Item> MDMA = registerItem("mdma", () -> new DrugItem(new DrugItem.Properties().addDrug(DrugRegistry.PARTY, 20, 0.6F, 8000).food(ModFoods.LSD_BLOTTER).tab(OrangeSunshine.TAB)));
    public static final RegistryObject<Item> MDA = registerItem("mda", () -> new DrugItem(new DrugItem.Properties().addDrug(DrugRegistry.PARTY, 20, 0.5F, 7000).food(ModFoods.LSD_BLOTTER).tab(OrangeSunshine.TAB)));
    public static final RegistryObject<Item> PMA = registerItem("pma", () -> new DrugItem(new DrugItem.Properties().addDrug(DrugRegistry.PARTY, 40, 2.0F, 10000).food(ModFoods.LSD_BLOTTER).tab(OrangeSunshine.TAB)));
    public static final RegistryObject<Item> CODEINE = registerItem("codeine", () -> new DrugItem(new DrugItem.Properties().addDrug(DrugRegistry.PARTY, 40, 2.0F, 1000).useAction(UseAction.DRINK).food(ModFoods.LSD_BLOTTER).tab(OrangeSunshine.TAB)));
    //----------------------------------------------NIC
    public static final RegistryObject<Item> TOBACCO = registerItem("tobacco");
    public static final RegistryObject<Item> DRIED_TOBACCO = registerItem("dried_tobacco");
    public static final RegistryObject<Item> NIC = registerItem("nic");
    public static final RegistryObject<Item> TOBACCO_SEEDS = registerBlockNamed("tobacco_seeds", ModBlocks.TOBACCO_BLOCK);
    public static final RegistryObject<JointItem> CIGARETTE = registerJoint("cigarette", new DrugChain().add(DrugRegistry.NIC, 2, 0.12F, 1000));
    public static final RegistryObject<JointItem> CIGAR = registerJoint("cigar", new DrugChain().add(DrugRegistry.NIC, 2, 0.12F, 1000));
    public static final RegistryObject<JointItem> SOURIN_AIR = registerJoint("sourin_air", new DrugChain().add(DrugRegistry.NIC, 2, 0.12F, 1000));
    //----------------------------------------------Opioids
    public static final RegistryObject<Item> MORPHINE_BOTTLE = registerItem("morphine_bottle", () -> new Item(new Item.Properties().stacksTo(16).tab(OrangeSunshine.TAB)) {
        @Override
        public boolean hasContainerItem(ItemStack itemStack) {
            return true;
        }

        @Override
        public ItemStack getContainerItem(ItemStack itemStack) {
            return Items.GLASS_BOTTLE.getDefaultInstance();
        }
    });
    public static final RegistryObject<Item> OPIUM_BOTTLE_0 = registerItem("opium_bottle_0", 16);
    public static final RegistryObject<Item> OPIUM_BOTTLE_1 = registerItem("opium_bottle_1", 16);
    public static final RegistryObject<Item> OPIUM_BOTTLE_2 = registerItem("opium_bottle_2", 16);
    public static final RegistryObject<Item> OPIUM_BOTTLE_3 = registerItem("opium_bottle_3", 16);
    //---------------------------------------------- Block Items
    public static final RegistryObject<Item> COCA_SEEDS = registerBlockNamed("coca_seeds", ModBlocks.COCA_BLOCK);
    public static final RegistryObject<Item> WEED_SEEDS = registerBlockNamed("weed_seeds", ModBlocks.WEED_BLOCK);
    public static final RegistryObject<Item> COKE_CAKE = registerBlock("coke_cake", ModBlocks.COKE_CAKE_BLOCK, 1);
    public static final RegistryObject<Item> DRYING_TABLE = registerBlock("drying_table", ModBlocks.DRYING_TABLE, 1);
    public static final RegistryObject<Item> FRIDGE = registerBlock("fridge", ModBlocks.FRIDGE, 1);
    public static final RegistryObject<Item> COMPOUND_EXTRACTOR = registerBlock("compound_extractor", ModBlocks.COMPOUND_EXTRACTOR, 1);
    public static final RegistryObject<Item> COMPOUND_COMPRESSOR = registerBlock("compound_compressor", ModBlocks.COMPOUND_COMPRESSOR, 1);
    //---------------------------------------------- Syringes / Glassware
    public static final RegistryObject<ClearDrugItem> NALOXONE = registerClearSyringe("naloxone", 0x99cc93);
    public static final RegistryObject<ClearDrugItem> FUROSEMIDE = registerClearSyringe("furosemide", 0xdfe3af);
    public static final RegistryObject<SyringeItem> COCAINE_SYRINGE = registerSyringe("cocaine_syringe", new DrugChain().add(DrugRegistry.COCAINE, 0, 0.47F, 4800), 0xFFFFFFFF);
    public static final RegistryObject<SyringeItem> MORPHINE_SYRINGE = registerSyringe("morphine_syringe", new DrugChain().add(DrugRegistry.MORPHINE, 0, 0.6F, 4800), 0xFF885038);
    public static final RegistryObject<Item> BONG = registerItem("bong", () -> new BongItem(new Item.Properties().durability(32).setNoRepair().tab(OrangeSunshine.TAB)));
    public static final RegistryObject<Item> SMOKING_PIPE = registerItem("smoking_pipe", () -> new BongItem(new Item.Properties().durability(32).setNoRepair().tab(OrangeSunshine.TAB)));
    public static final RegistryObject<Item> RIG = registerItem("rig", () -> new RigItem(new Item.Properties().durability(32).setNoRepair().tab(OrangeSunshine.TAB)));
    //----------------------------------------------Crafting
    public static final RegistryObject<Item> BLOTTER = registerItem("blotter");
    public static final RegistryObject<Item> ROLLING_PAPER = registerItem("rolling_paper");
    public static final RegistryObject<Item> EMPTY_SYRINGE = registerItem("syringe", 16);
    public static final RegistryObject<Item> DE_IONIZED_WATER = registerItem("de_ionized_water");
    public static final RegistryObject<Item> VINEGAR = registerItem("vinegar");
    public static final RegistryObject<Item> ROOT_BARK = registerItem("root_bark");
    public static final RegistryObject<Item> STRAINER = registerItem("strainer");
    public static final RegistryObject<Item> SODIUM_HYDROXIDE = registerItem("sodium_hydroxide");
    public static final RegistryObject<Item> BARK_SOLUTION_1 = registerItem("bark_solution_1");
    public static final RegistryObject<Item> BARK_SOLUTION_2 = registerItem("bark_solution_2");
    public static final RegistryObject<Item> BARK_SOLUTION_3 = registerItem("bark_solution_3");
    public static final RegistryObject<Item> BARK_SOLUTION_4 = registerItem("bark_solution_4");
    public static final RegistryObject<Item> BARK_SOLUTION_5 = registerItem("bark_solution_5");
    public static final RegistryObject<Item> ERGOTAMINE = registerItem("ergotamine");
    public static final RegistryObject<Item> ERGOROT_INFECTED_WHEAT = registerItem("ergorot_infected_wheat");
    public static final RegistryObject<Item> LYSERGIC_ACID = registerItem("lysergic_acid");
    public static final RegistryObject<Item> AMMONIA = registerItem("ammonia");
    public static final RegistryObject<Item> DIETHYLAMINE = registerItem("diethylamine");
    //----------------------------------------------Ore
    public static final RegistryObject<Item> PSYCH_ORE = registerBlock("psych_ore", ModBlocks.PSYCH_ORE, 64);
    public static final RegistryObject<Item> PSYCH_INGOT = registerItem("psych_ingot");
    //----------------------------------------------Tools
    public static final RegistryObject<Item> PSYCH_SWORD = registerItem("psych_sword", () -> new SwordItem(ModItemTier.PSYCH, 25, 15f, new Item.Properties().tab(OrangeSunshine.TAB).fireResistant().defaultDurability(10000)));
    public static final RegistryObject<Item> PSYCH_PIC = registerItem("psych_pic", () -> new PickaxeItem(ModItemTier.PSYCH, 25, 15f, new Item.Properties().tab(OrangeSunshine.TAB).fireResistant().defaultDurability(10000).addToolType(ToolType.PICKAXE, 4)));
    public static final RegistryObject<Item> PSYCH_AXE = registerItem("psych_axe", () -> new AxeItem(ModItemTier.PSYCH, 25, 15f, new Item.Properties().tab(OrangeSunshine.TAB).fireResistant().defaultDurability(10000).addToolType(ToolType.AXE, 4)));
    public static final RegistryObject<Item> PSYCH_SHOVEL = registerItem("psych_shovel", () -> new ShovelItem(ModItemTier.PSYCH, 25, 15f, new Item.Properties().tab(OrangeSunshine.TAB).fireResistant().defaultDurability(10000).addToolType(ToolType.SHOVEL, 4)));
    public static final RegistryObject<Item> PSYCH_HOE = registerItem("psych_hoe", () -> new HoeItem(ModItemTier.PSYCH, 25, 15f, new Item.Properties().tab(OrangeSunshine.TAB).fireResistant().defaultDurability(10000).addToolType(ToolType.HOE, 4)));
    //----------------------------------------------Armour
    public static final RegistryObject<Item> PSYCH_HELMET = registerItem("psych_helmet", () -> new ArmorItem(ModArmorMaterial.PSYCH_ARMOR_MATERIAL, EquipmentSlotType.HEAD, new Item.Properties().tab(OrangeSunshine.TAB).fireResistant().defaultDurability(10000)));
    public static final RegistryObject<Item> PSYCH_CHEST = registerItem("psych_chest", () -> new ArmorItem(ModArmorMaterial.PSYCH_ARMOR_MATERIAL, EquipmentSlotType.CHEST, new Item.Properties().tab(OrangeSunshine.TAB).fireResistant().defaultDurability(10000)));
    public static final RegistryObject<Item> PSYCH_LEGGINGS = registerItem("psych_leggings", () -> new ArmorItem(ModArmorMaterial.PSYCH_ARMOR_MATERIAL, EquipmentSlotType.LEGS, new Item.Properties().tab(OrangeSunshine.TAB).fireResistant().defaultDurability(10000)));
    public static final RegistryObject<Item> PSYCH_BOOTS = registerItem("psych_boots", () -> new ArmorItem(ModArmorMaterial.PSYCH_ARMOR_MATERIAL, EquipmentSlotType.FEET, new Item.Properties().tab(OrangeSunshine.TAB).fireResistant().defaultDurability(10000)));
    //----------------------------------------------Utils
    private static RegistryObject<DrugItem> registerDrug(String name, DrugChain drugChain) {
        return registerDrug(name, drugChain, UseAction.EAT, 64);
    }
    private static RegistryObject<DrugItem> registerDrug(String name, DrugChain drugChain, UseAction useAction, int stackSize) {
        DrugItem.Properties itemProperties = new DrugItem.Properties();
        for (DrugEffectProperty property : drugChain.list) {
            itemProperties.addDrug(property.drug, property.delayTicks, property.potencyPercentage, property.duration);
        }
        return registerItem(name, () -> new DrugItem(itemProperties.useAction(useAction).stacksTo(stackSize).tab(OrangeSunshine.TAB)));
    }
    private static RegistryObject<SyringeItem> registerSyringe(String name, DrugChain drugChain, int color) {
        SyringeItem.Properties itemProperties = new SyringeItem.Properties().color(color);
        for (DrugEffectProperty property : drugChain.list) {
            itemProperties.addDrug(property.drug, property.delayTicks, property.potencyPercentage, property.duration);
        }
        return registerItem(name, () -> new SyringeItem(itemProperties.tab(OrangeSunshine.TAB).stacksTo(1)));
    }
    private static RegistryObject<ClearDrugItem> registerClearSyringe(String name, DrugChain drugChain, int color) {
        ClearDrugItem.Properties itemProperties = new ClearDrugItem.Properties().color(color);
        for (DrugEffectProperty property : drugChain.list) {
            itemProperties.addDrug(property.drug, property.delayTicks, property.potencyPercentage, property.duration);
        }
        return registerItem(name, () -> new ClearDrugItem(itemProperties.tab(OrangeSunshine.TAB).stacksTo(1)));
    }
    private static RegistryObject<ClearDrugItem> registerClearSyringe(String name, int color) {
        ClearDrugItem.Properties itemProperties = new ClearDrugItem.Properties().color(color);
        return registerItem(name, () -> new ClearDrugItem(itemProperties.tab(OrangeSunshine.TAB).stacksTo(1)));
    }
    private static RegistryObject<JointItem> registerJoint(String name, DrugChain drugChain) {
        DrugItem.Properties itemProperties = new DrugItem.Properties();
        for (DrugEffectProperty property : drugChain.list) {
            itemProperties.addDrug(property.drug, property.delayTicks, property.potencyPercentage, property.duration);
        }
        itemProperties.durability(8);
        return registerItem(name, () -> new JointItem(itemProperties.useAction(UseAction.BOW).tab(OrangeSunshine.TAB)));
    }
    private static <T extends Block> RegistryObject<Item> registerBlock(String name, RegistryObject<T> block) {
        return registerBlock(name, block, 64);
    }
    private static <T extends Block> RegistryObject<Item> registerBlock(String name, RegistryObject<T> block, int stackSize) {
        return registerItem(name, () -> new BlockItem(block.get(), new Item.Properties().tab(OrangeSunshine.TAB).stacksTo(stackSize)));
    }
    private static <T extends Block> RegistryObject<Item> registerBlockNamed(String name, RegistryObject<T> block) {
        return registerBlockNamed(name, block, 64);
    }
    private static <T extends Block> RegistryObject<Item> registerBlockNamed(String name, RegistryObject<T> block, int stackSize) {
        return registerItem(name, () -> new BlockNamedItem(block.get(), new Item.Properties().tab(OrangeSunshine.TAB).stacksTo(stackSize)));
    }
    private static RegistryObject<Item> registerItem(String name) {
        return registerItem(name, () -> new Item(new Item.Properties().tab(OrangeSunshine.TAB)));
    }
    private static RegistryObject<Item> registerItem(String name, int stackSize) {
        return registerItem(name, () -> new Item(new Item.Properties().tab(OrangeSunshine.TAB).stacksTo(stackSize)));
    }
    private static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> supplier) {
        return OrangeSunshine.ITEMS.register(name, supplier);
    }
    public static class DrugChain {
        public final List<DrugEffectProperty> list = new ArrayList<>();

        public DrugChain add(RegistryObject<Drug> drug, int delayTicks, float potencyPercentage, int duration) {
            list.add(new DrugEffectProperty(drug, delayTicks, potencyPercentage, duration));
            return this;
        }
    }
    public static class DrugEffectProperty {
        public final RegistryObject<Drug> drug;
        public final int delayTicks;
        public final float potencyPercentage;
        public final int duration;

        public DrugEffectProperty(RegistryObject<Drug> drug, int delayTicks, float potencyPercentage, int duration) {
            this.drug = drug;
            this.delayTicks = delayTicks;
            this.potencyPercentage = potencyPercentage;
            this.duration = duration;
        }
    }
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerItemColors(ColorHandlerEvent.Item event) {
        event.getItemColors().register(new SyringeItem.Color(), COCAINE_SYRINGE.get(), MORPHINE_SYRINGE.get());
    }
    public static void init(IEventBus modBus) {
        modBus.register(ModItems.class);
    }
}