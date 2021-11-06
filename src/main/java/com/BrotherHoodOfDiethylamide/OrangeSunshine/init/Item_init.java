package com.BrotherHoodOfDiethylamide.OrangeSunshine.init;


import com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.LSD;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItem;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.seeds.WeedSeed;
import net.minecraft.item.Item;

import java.util.ArrayList;

public class Item_init {
    public static final ArrayList<Item> ITEMS = new ArrayList<>();

    //-------------------------------- Weed
    public static final Item WEED = new ModItem("weed");
    public static final Item WEEDSEED = new WeedSeed("weedseed");
    //-------------------------------- LSD
    public static final Item LSD = new LSD("lsd",0,100, true);
    public static final Item RYE = new ModItem("rye.json");
    public static final Item RYESEED = new WeedSeed("ryeseed");
}
