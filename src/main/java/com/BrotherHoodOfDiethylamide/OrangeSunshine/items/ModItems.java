package com.BrotherHoodOfDiethylamide.OrangeSunshine.items;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ModBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItems {
    public static final RegistryObject<DrugItem> RED_SHROOMS = registerDrug("red_shrooms", new DrugChain().add(DrugRegistry.RED_SHROOMS, 400, 0.3F, 6900));
    public static final RegistryObject<Item> DRIED_RED_MUSHROOM = registerItem("dried_red_mushroom");

    public static final RegistryObject<DrugItem> BROWN_SHROOMS = registerDrug("brown_shrooms", new DrugChain().add(DrugRegistry.BROWN_SHROOMS, 200, 0.3F, 3200));
    public static final RegistryObject<Item> DRIED_BROWN_MUSHROOM = registerItem("dried_brown_mushroom");

    public static final RegistryObject<Item> COKE_CAKE = registerBlock("coke_cake", ModBlocks.COKE_CAKE_BLOCK, 1);
    public static final RegistryObject<Item> COCAINE_ROCK = registerItem("cocaine_rock");
    public static final RegistryObject<DrugItem> COCAINE_POWDER = registerDrug("cocaine_powder", new DrugChain().add(DrugRegistry.COCAINE, 100, 0.3F, 3200), UseAction.BOW, 64);
    public static final RegistryObject<DrugItem> COCAINE_DUST = registerDrug("cocaine_dust", new DrugChain().add(DrugRegistry.COCAINE, 100, 0.05F, 1000), UseAction.BOW, 64);
    public static final RegistryObject<Item> COCA_MULCH = registerItem("coca_mulch");
    public static final RegistryObject<Item> COCA_LEAF = registerItem("coca_leaf");
    public static final RegistryObject<Item> COCA_SEEDS = registerBlockNamed("coca_seeds", ModBlocks.COCA_BLOCK);

    public static final RegistryObject<Item> HASH_MUFFIN = registerItem("hash_muffin", () -> new DrugItem(new DrugItem.Properties().addDrug(DrugRegistry.WEED, 800, 0.12F, 3200).food(ModFoods.HASH_MUFFIN).tab(ItemGroup.TAB_FOOD)));
    public static final RegistryObject<JointItem> WEED_JOINT = registerJoint("weed_joint", new DrugChain().add(DrugRegistry.WEED, 0, 0.12F, 3200));
    public static final RegistryObject<Item> DRIED_WEED_LEAF = registerItem("dried_weed_leaf");
    public static final RegistryObject<Item> WEED_LEAF = registerItem("weed_leaf");
    public static final RegistryObject<Item> DRIED_WEED_BUD = registerItem("dried_weed_bud");
    public static final RegistryObject<Item> WEED_SEEDS = registerBlockNamed("weed_seeds", ModBlocks.WEED_BLOCK);
    public static final RegistryObject<Item> WEED_BUD = registerItem("weed_bud");


    public static final RegistryObject<Item> LSD_BOTTLE = registerItem("lsd_bottle", () -> new DrugItem(new DrugItem.Properties().addDrug(DrugRegistry.LSD_BOTTLE, 800, 1.0F, 9999).food(ModFoods.LSD_BOTTLE).tab(ItemGroup.TAB_FOOD)));


    public static final RegistryObject<Item> BLOTTER = registerItem("blotter");
    public static final RegistryObject<Item> ROLLING_PAPER = registerItem("rolling_paper");

    public static final RegistryObject<JointItem> CIGARETTE = registerJoint("cigarette", new DrugChain());

    public static final RegistryObject<Item> EMPTY_SYRINGE = registerItem("syringe", 16);
    public static final RegistryObject<SyringeItem> COCAINE_SYRINGE = registerSyringe("cocaine_syringe", new DrugChain().add(DrugRegistry.COCAINE, 0, 0.47F, 4800), 0xFFFFFFFF);
    public static final RegistryObject<SyringeItem> MORPHINE_SYRINGE = registerSyringe("morphine_syringe", new DrugChain().add(DrugRegistry.MORPHINE, 0, 0.6F, 4800), 0xFF885038);

    public static final RegistryObject<Item> BONG = registerItem("bong", () -> new BongItem(new Item.Properties().durability(8).setNoRepair().tab(OrangeSunshine.TAB)));

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

    private static RegistryObject<JointItem> registerJoint(String name, DrugChain drugChain) {
        DrugItem.Properties itemProperties = new DrugItem.Properties();
        for (DrugEffectProperty property : drugChain.list) {
            itemProperties.addDrug(property.drug, property.delayTicks, property.potencyPercentage, property.duration);
        }
        return registerItem(name, () -> new JointItem(itemProperties.useAction(UseAction.BOW).stacksTo(16).tab(OrangeSunshine.TAB)));
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
