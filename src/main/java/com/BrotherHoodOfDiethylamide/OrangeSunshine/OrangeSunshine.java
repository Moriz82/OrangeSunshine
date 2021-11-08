package com.BrotherHoodOfDiethylamide.OrangeSunshine;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.FurnaceBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.TileEntitys.TileEntityCompoundExtractor;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.proxy.CommonProxy;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Recipe_init;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

@Mod(modid = OrangeSunshine.MODID, name = OrangeSunshine.NAME, version = OrangeSunshine.VERSION)
public class OrangeSunshine
{
    public static final String MODID = "orangesunshine";
    public static final String NAME = "Orange Sunshine";
    public static final String VERSION = "1.0";
    public static final String ClientProxyClass = "com.BrotherHoodOfDiethylamide.OrangeSunshine.proxy.ClientProxy";
    public static final String CommonProxyClass = "com.BrotherHoodOfDiethylamide.OrangeSunshine.proxy.CommonProxy";
    public static final CreativeTabs CreativeTab = new OrangeSunshineTab(CreativeTabs.getNextID(), NAME);
    private static Logger logger;

    @SidedProxy(clientSide = ClientProxyClass, serverSide = CommonProxyClass)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        registerTileEntities();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Recipe_init.init();
        addSeeds();
        addSmeltables();
    }

    public void addSeeds(){
        MinecraftForge.addGrassSeed(new ItemStack(Item_init.WEEDSEED), 4);
        MinecraftForge.addGrassSeed(new ItemStack(Item_init.RYESEED), 6);
    }

    public void registerTileEntities(){
        GameRegistry.registerTileEntity(TileEntityCompoundExtractor.class, "orangesunshine:compoundextractor");
    }

    public void addSmeltables(){
        TileEntityCompoundExtractor.smeltables.add(Item_init.RYE);
        TileEntityCompoundExtractor.smeltables.add(Item_init.LYSERGICACID);
        TileEntityCompoundExtractor.smeltingOutput.put(Item_init.RYE, Item_init.LSD.getDefaultInstance());
        TileEntityCompoundExtractor.smeltingOutput.put(Item_init.LYSERGICACID, Item_init.ORANGESUNSHINE.getDefaultInstance());
    }
}

class OrangeSunshineTab extends CreativeTabs {
    private final String name;

    public OrangeSunshineTab(int id, String name) {
        super(id, name);
        this.name = name;
    }
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getIconItemStack() {
        return new ItemStack(Item_init.ORANGESUNSHINE, 1, 0);
    }
    @Override
    public String getTranslatedTabLabel(){
        return name;
    }
    @Override
    public ItemStack getTabIconItem() {
        return null;
    }
}
