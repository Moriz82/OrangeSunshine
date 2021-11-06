package com.BrotherHoodOfDiethylamide.OrangeSunshine.init;


import com.BrotherHoodOfDiethylamide.OrangeSunshine.food.LSD;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItem;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.WeedSeed;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import java.util.ArrayList;

public class Item_init {
    public static final ArrayList<Item> ITEMS = new ArrayList<>();

    public static final Item WEED = new ModItem("weed");
    public static final Item LSD = new LSD("lsd",0,100, true);
    public static final Item WEEDSEED = new WeedSeed("weedseed");
}
