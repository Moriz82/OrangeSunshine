package com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.tradelists;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.Random;

public class TradelistShmokeStackz implements EntityVillager.ITradeList {
    Item currency = Items.EMERALD;

    @Override
    public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
        for (Item item : ModItems.ALL_ITEMS) {
            if (item == null) continue;
            String name = item.getUnlocalizedName();
            if (name == null || name.contains("seed") || name.contains("crop")) continue;
            recipeList.add(new MerchantRecipe(new ItemStack(currency, 1), new ItemStack(item, 1)));
        }
    }
}
