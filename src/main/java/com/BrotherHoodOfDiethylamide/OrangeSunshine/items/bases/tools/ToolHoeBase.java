package com.BrotherHoodOfDiethylamide.OrangeSunshine.items.bases.tools;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.utils.IHasModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemHoe;

public class ToolHoeBase extends ItemHoe implements IHasModel
{
    public ToolHoeBase(String name, ToolMaterial material)
    {
        super(material);
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(OrangeSunshine.CreativeTab);

        Item_init.ITEMS.add(this);
    }

    @Override
    public void registerModels()
    {
        OrangeSunshine.proxy.registerItemRenderer(this, 0, "inventory");
    }
}