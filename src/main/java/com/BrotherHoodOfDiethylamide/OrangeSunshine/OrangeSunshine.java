package com.BrotherHoodOfDiethylamide.OrangeSunshine;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.TileEntitys.TileEntityCompoundExtractor;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.EntityShmokeStackz;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.tradelists.TradelistShmokeStackz;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Entity_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.proxy.CommonProxy;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Recipe_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.utils.RenderHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mod(modid = OrangeSunshine.MODID, name = OrangeSunshine.NAME, version = OrangeSunshine.VERSION)
@Mod.EventBusSubscriber
public class OrangeSunshine
{
    public static final String MODID = "orangesunshine";
    public static final String NAME = "Orange Sunshine";
    public static final String VERSION = "1.0";
    public static final String ClientProxyClass = "com.BrotherHoodOfDiethylamide.OrangeSunshine.proxy.ClientProxy";
    public static final String CommonProxyClass = "com.BrotherHoodOfDiethylamide.OrangeSunshine.proxy.CommonProxy";
    public static final CreativeTabs CreativeTab = new OrangeSunshineTab(CreativeTabs.getNextID(), NAME);
    public static Logger logger;

    @Mod.Instance
    public static OrangeSunshine instance;

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
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        setBiomeSpawns();
        Entity_init.registerEntities();
    }

    @EventHandler
    @SideOnly(Side.CLIENT)
    public void clientInit(FMLInitializationEvent event)
    {
        RenderHandler.registerEntityRenders();
        addSmeltables();
    }

    public void setBiomeSpawns(){
        for (Biome biome:ForgeRegistries.BIOMES.getValues()) {
            EntityRegistry.addSpawn(EntityShmokeStackz.class, 8, 3, 10,EnumCreatureType.CREATURE, biome);
        }
    }

    public void addSeeds(){
        MinecraftForge.addGrassSeed(new ItemStack(Item_init.WEEDSEED), 6);
        MinecraftForge.addGrassSeed(new ItemStack(Item_init.RYESEED), 6);
        MinecraftForge.addGrassSeed(new ItemStack(Item_init.MUSHROOMSEED), 6);
    }

    public void registerTileEntities(){
        GameRegistry.registerTileEntity(TileEntityCompoundExtractor.class, "orangesunshine:compound_extractor");
    }

    public void addSmeltables(){
        TileEntityCompoundExtractor.smeltables.add(Item_init.RYE);
        TileEntityCompoundExtractor.smeltables.add(Item_init.LYSERGICACID);
        TileEntityCompoundExtractor.smeltables.add(Item_init.MUSHROOMCUBENSIS);
        TileEntityCompoundExtractor.smeltables.add(Item_init.WEED);
        // input and output
        TileEntityCompoundExtractor.smeltingOutput.put(Item_init.RYE, Item_init.LSD.getDefaultInstance());
        TileEntityCompoundExtractor.smeltingOutput.put(Item_init.LYSERGICACID, Item_init.ORANGESUNSHINE.getDefaultInstance());
        TileEntityCompoundExtractor.smeltingOutput.put(Item_init.MUSHROOMCUBENSIS, Item_init.PSILOCYBIN.getDefaultInstance());
        TileEntityCompoundExtractor.smeltingOutput.put(Item_init.WEED, Item_init.WEEDEXTRACT.getDefaultInstance());
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
        return new ItemStack(Item_init.ORANGESUNSHINEBLODDER, 1, 0);
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
