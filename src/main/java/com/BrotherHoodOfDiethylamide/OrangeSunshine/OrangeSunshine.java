package com.BrotherHoodOfDiethylamide.OrangeSunshine;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.Proxy.CommonProxy;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Recipe_init;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

@Mod(modid = OrangeSunshine.MODID, name = OrangeSunshine.NAME, version = OrangeSunshine.VERSION)
public class OrangeSunshine
{
    public static final String MODID = "orangesunshine";
    public static final String NAME = "OrangeSunshine";
    public static final String VERSION = "1.0";
    public static final String ClientProxyClass = "com.BrotherHoodOfDiethylamide.OrangeSunshine.proxy.ClientProxy";
    public static final String CommonProxyClass = "com.BrotherHoodOfDiethylamide.OrangeSunshine.proxy.CommonProxy";
    public static final CreativeTabs CreativeTab = new OrangeSunshineTab(CreativeTabs.getNextID(), MODID);
    private static Logger logger;

    @SidedProxy(clientSide = ClientProxyClass, serverSide = CommonProxyClass)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Recipe_init.init();
        addSeeds();
    }

    public void addSeeds(){
        MinecraftForge.addGrassSeed(new ItemStack(Item_init.WEEDSEED), 6);
    }
}

class OrangeSunshineTab extends CreativeTabs {
    private final String MODID;

    public OrangeSunshineTab(int id, String name) {
        super(id, name);
        MODID = name;
    }
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getIconItemStack() {
        return new ItemStack(Item_init.LSD, 1, 0);
    }
    @Override
    public String getTranslatedTabLabel(){
        return MODID;
    }
    @Override
    public ItemStack getTabIconItem() {
        return null;
    }
}
