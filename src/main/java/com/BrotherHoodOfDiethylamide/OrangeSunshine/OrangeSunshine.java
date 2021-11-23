package com.BrotherHoodOfDiethylamide.OrangeSunshine;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ComplexBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.TileEntitys.TileEntityCompoundExtractor;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.EntityShmokeStackz;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.tradelists.TradelistShmokeStackz;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Entity_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.*;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.proxy.CommonProxy;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Recipe_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.utils.RenderHandler;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.world.WorldGenCustomOres;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.item.Item;
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
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
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

import static com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ComplexBlocks.psychOre;

@Mod(modid = OrangeSunshine.MODID, name = OrangeSunshine.NAME, version = OrangeSunshine.VERSION)
@Mod.EventBusSubscriber
public class OrangeSunshine
{
    public static float dofFocalPointNear;
    public static float dofFocalBlurNear;
    public static float dofFocalPointFar;
    public static float dofFocalBlurFar;
    public static double pauseMenuBlur;
    public static final String MODID = "orangesunshine";
    public static final String NAME = "Orange Sunshine";
    public static final String filePathTextures = "textures/mod/";
    public static final String filePathShaders = "shaders/";
    public static final String VERSION = "1.0";
    public static SimpleNetworkWrapper network;
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
        worldGen();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Recipe_init.init();
        addSeeds();
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        DrugRegistry.registerInfluence(DrugInfluence.class, "default");
        Item_init.init();
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
    @SideOnly(Side.CLIENT)
    public void createDrugRenderer(DrugProperties drugProperties)
    {
        drugProperties.renderer = new DrugRenderer();
        PSRenderStates.setShader3DEnabled(true);
        PSRenderStates.setShader2DEnabled(true);
        PSRenderStates.sunFlareIntensity = (0.25f);
        PSRenderStates.doHeatDistortion = (true);
        PSRenderStates.doWaterDistortion = (true);
        PSRenderStates.doMotionBlur = (true);
//        DrugShaderHelper.doShadows = config.get(CATEGORY_VISUAL, "doShadows", true).getBoolean(true);
        PSRenderStates.doShadows = true;

        dofFocalPointNear = 0.2f;
        dofFocalPointFar = 128f;
        dofFocalBlurNear = 0f;
        dofFocalBlurFar = 0f;
        DrugProperties.waterOverlayEnabled = (true);
        DrugProperties.hurtOverlayEnabled = (true);
        DrugProperties.digitalEffectPixelRescale = new float[]{0.05f, 0.05f};
        PSRenderStates.disableDepthBuffer = false;
        PSRenderStates.bypassPingPongBuffer = false;
        PSRenderStates.renderFakeSkybox = true;
        pauseMenuBlur = 5f;
    }

    public static void worldGen(){
        GameRegistry.registerWorldGenerator(new WorldGenCustomOres(), 0);
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
