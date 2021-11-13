package com.BrotherHoodOfDiethylamide.OrangeSunshine.init;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.bases.ArmourModel;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.bases.tools.*;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.seeds.MushroomSeed;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.seeds.RyeSeed;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.compounds.LSD;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.bases.ModItem;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.seeds.WeedSeed;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.compounds.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.blotter.LSDBlotter;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.blotter.OrangeSunshineBlotter;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.edibles.MushroomCubensis;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.smokeable.WeedJoint;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;

import java.util.ArrayList;

public class Item_init {
    public static final ArrayList<Item> ITEMS = new ArrayList<>();

    //-------------------------------- Weed
    public static final Item WEED = new ModItem("weed");
    public static final Item WEEDEXTRACT = new ModItem("weed_extract");
    public static final Item WEEDSEED = new WeedSeed("weed_seed");
    public static final Item ROLLINGPAPER = new ModItem("rolling_paper");
    public static final Item WEEDJOINT = new WeedJoint("weed_joint", 0, 100, true);
    //-------------------------------- LSD
    public static final Item LSD = new LSD("lsd",0,100, true);
    public static final Item ORANGESUNSHINE = new OrangeSunshine("orangesunshine",0,100, true);
    public static final Item BLODDER = new ModItem("blotter");
    public static final Item LSDBLODDER = new LSDBlotter("lsd_blotter",0,100, true);
    public static final Item ORANGESUNSHINEBLODDER = new OrangeSunshineBlotter("orangesunshine_blotter",0,100, true);
    public static final Item RYE = new ModItem("rye");
    public static final Item RYESEED = new RyeSeed("rye_seed");
    public static final Item LYSERGICACID = new ModItem("lysergic_acid");
    //-------------------------------- Shrooms
    public static final Item MUSHROOMCUBENSIS = new MushroomCubensis("mushroom_cubensis", 0, 100, true);
    public static final Item MUSHROOMSEED = new MushroomSeed("mushroom_seed");
    public static final Item PSILOCYBIN = new ModItem("psilocybin");
    //-------------------------------- Armour
    public static final ItemArmor.ArmorMaterial PSYCHARMORMATERIAL = EnumHelper.addArmorMaterial("psych_armour_model", com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine.MODID + ":psych_model", 1000, new int[] {23, 23, 23, 23}, 30, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 100.0f);
    public static final Item PSYCHHELMET = new ArmourModel("psych_helmet", PSYCHARMORMATERIAL, EntityEquipmentSlot.HEAD);
    public static final Item PSYCHCHEST = new ArmourModel("psych_chest", PSYCHARMORMATERIAL, EntityEquipmentSlot.CHEST);
    public static final Item PSYCHLEGS = new ArmourModel("psych_legs", PSYCHARMORMATERIAL, EntityEquipmentSlot.LEGS);
    public static final Item PSYCHBOOTS = new ArmourModel("psych_boots", PSYCHARMORMATERIAL, EntityEquipmentSlot.FEET);
    //-------------------------------- Tools
    public static final Item.ToolMaterial PSYCHTOOLMATERIAL = EnumHelper.addToolMaterial("psych_tool_model", 8, 10000, 20.0f, 30.0f, 30);
    public static final Item PSYCHSWORD = new ToolSwordBase("psych_sword", PSYCHTOOLMATERIAL);
    public static final Item PSYCHPIC = new ToolPicBase("psych_pic", PSYCHTOOLMATERIAL);
    public static final Item PSYCHAXE = new ToolAxeBase("psych_axe", PSYCHTOOLMATERIAL);
    public static final Item PSYCHSHOVEL = new ToolShovelBase("psych_shovel", PSYCHTOOLMATERIAL);
    public static final Item PSYCHHOE = new ToolHoeBase("psych_hoe", PSYCHTOOLMATERIAL);
    //-------------------------------- MISC
    public static final Item PSYCHINGOT = new ModItem("psych_ingot");
}
