package com.BrotherHoodOfDiethylamide.OrangeSunshine.items.bases;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.utils.IHasModel;
import net.minecraft.item.Item;

public class ModItem extends Item implements IHasModel {

    public ModItem(String name) {
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(OrangeSunshine.CreativeTab);

        Item_init.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        OrangeSunshine.proxy.registerItemRenderer(this, 0, "inventory");
    }
}
