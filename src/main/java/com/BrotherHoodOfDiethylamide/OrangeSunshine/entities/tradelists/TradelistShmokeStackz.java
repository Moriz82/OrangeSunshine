package com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.tradelists;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.Random;

public class TradelistShmokeStackz implements EntityVillager.ITradeList{

    Item currency = Items.EMERALD;

    @Override
    public void addMerchantRecipe(IMerchant iMerchant, MerchantRecipeList merchantRecipeList, Random random) {
        for (Item item : Item_init.ITEMS) {
            if (item.getUnlocalizedName().contains("crop"))
                continue;
            merchantRecipeList.add(new MerchantRecipe(
                    new ItemStack(currency,1),
                    new ItemStack(item,1)
            ));
            merchantRecipeList.add(new MerchantRecipe(
                    new ItemStack(item,1),
                    new ItemStack(currency,1)
            ));
        }
        /*merchantRecipeList.add(new MerchantRecipe(
                new ItemStack(currency,1),
                new ItemStack(Item_init.LSD,1)
        ));
        merchantRecipeList.add(new MerchantRecipe(
                new ItemStack(currency,1),
                new ItemStack(Item_init.LSDBLODDER,1)
        ));
        merchantRecipeList.add(new MerchantRecipe(
                new ItemStack(currency,1),
                new ItemStack(Item_init.ORANGESUNSHINE,1)
        ));
        merchantRecipeList.add(new MerchantRecipe(
                new ItemStack(currency,1),
                new ItemStack(Item_init.ORANGESUNSHINEBLODDER,1)
        ));
        merchantRecipeList.add(new MerchantRecipe(
                new ItemStack(currency,1),
                new ItemStack(Item_init.WEED,1)
        ));
        merchantRecipeList.add(new MerchantRecipe(
                new ItemStack(currency,1),
                new ItemStack(Item_init.WEEDJOINT,1)
        ));*/
    }
}
