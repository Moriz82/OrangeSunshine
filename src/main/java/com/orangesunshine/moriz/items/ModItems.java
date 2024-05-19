package com.orangesunshine.moriz.items;

import com.orangesunshine.moriz.OrangeSunshine;
import com.orangesunshine.moriz.blocks.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final List<RegistryObject<Item>> simpleItems = new ArrayList<>();
    public static final List<RegistryObject<Item>> blockItems = new ArrayList<>();


    ////////////////////////////////////// FOOD ITEMS ///////////////////////////////////////
    public static final RegistryObject<Item> LSD_BLOTTER = createFoodItem("lsd_blotter", 1, 1);
    public static final RegistryObject<Item> ORANGESUNSHINE_BLOTTER = createFoodItem("orangesunshine_blotter", 1, 1);

    ////////////////////////////////////// BLOCK ITEMS //////////////////////////////////////

    //public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = createBlockItem("example_block");

    /////////////////////////////////////////////////////////////////////////////////////////


    public static RegistryObject<Item> createFoodItem(String name, int nutrition, float saturation) {
        RegistryObject<Item> item = OrangeSunshine.ITEMS.register(name, () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                .alwaysEat().nutrition(nutrition).saturationMod(saturation).build())));
        simpleItems.add(item);
        return item;
    }

    public static RegistryObject<Item> createBlockItem(String name) {
        RegistryObject<Item> item = OrangeSunshine.ITEMS.register(name, () -> new BlockItem(ModBlocks.EXAMPLE_BLOCK.get(), new Item.Properties()));
        simpleItems.add(item);
        return item;
    }

    public static void register(){}
}
