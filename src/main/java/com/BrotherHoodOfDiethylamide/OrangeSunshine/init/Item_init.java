package com.BrotherHoodOfDiethylamide.OrangeSunshine.init;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.seeds.MushroomSeed;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.seeds.RyeSeed;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.compounds.LSD;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItem;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.seeds.WeedSeed;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.compounds.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.blotter.LSDBlotter;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.blotter.OrangeSunshineBlotter;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.edibles.MushroomCubensis;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.smokeable.WeedJoint;
import net.minecraft.item.Item;

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
    //-------------------------------- MISC
    public static final Item PSYCHINGOT = new ModItem("psych_ingot");
}
