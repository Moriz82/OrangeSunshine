package com.BrotherHoodOfDiethylamide.OrangeSunshine;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.gui.GuiHandler;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.PlayerProperties;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.commands.SetDrugCommand;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.commands.TestCommand;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugRegistry;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.events.EventHandler;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.events.RegistryHandler;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.network.PacketHandler;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.*;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.proxy.CommonProxy;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.world.WorldGenCustomOres;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

@Mod(modid = OrangeSunshine.MODID, name = OrangeSunshine.NAME, version = OrangeSunshine.VERSION)
@Mod.EventBusSubscriber
public class OrangeSunshine {
    public static float dofFocalPointNear;
    public static float dofFocalBlurNear;
    public static float dofFocalPointFar;
    public static float dofFocalBlurFar;
    public static double pauseMenuBlur;
    public static final String MODID = "orangesunshine";
    public static final String NAME = "Orange Sunshine";
    public static final String filePathTextures = "textures/mod/";
    public static final String filePathShaders = "shaders/";
    public static final String VERSION = "2.0";
    public static SimpleNetworkWrapper network;
    public static Logger logger;

    public static final String ClientProxyClass = "com.BrotherHoodOfDiethylamide.OrangeSunshine.proxy.ClientProxy";
    public static final String CommonProxyClass = "com.BrotherHoodOfDiethylamide.OrangeSunshine.proxy.CommonProxy";

    @Mod.Instance
    public static OrangeSunshine instance;

    @SidedProxy(clientSide = ClientProxyClass, serverSide = CommonProxyClass)
    public static CommonProxy proxy;

    public static final CreativeTabs CreativeTab = new CreativeTabs(NAME) {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(ModItems.ORANGESUNSHINE_BLOTTER);
        }

        @Override
        public String getTranslatedTabLabel() {
            return NAME;
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        DrugRegistry.registerAll();

        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        PacketHandler.init();

        PlayerProperties.register();

        MinecraftForge.EVENT_BUS.register(new EventHandler());

        GameRegistry.registerWorldGenerator(new WorldGenCustomOres(), 0);
        RegistryHandler.registerEntities();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new SetDrugCommand());
        event.registerServerCommand(new TestCommand());
    }

    @SideOnly(Side.CLIENT)
    public void createDrugRenderer(DrugProperties drugProperties) {
        drugProperties.renderer = new DrugRenderer();
        PSRenderStates.setShader3DEnabled(true);
        PSRenderStates.setShader2DEnabled(true);
        PSRenderStates.sunFlareIntensity = 0.25f;
        PSRenderStates.doHeatDistortion = true;
        PSRenderStates.doWaterDistortion = true;
        PSRenderStates.doMotionBlur = true;
        PSRenderStates.doShadows = true;

        dofFocalPointNear = 0.2f;
        dofFocalPointFar = 128f;
        dofFocalBlurNear = 0f;
        dofFocalBlurFar = 0f;
        DrugProperties.waterOverlayEnabled = true;
        DrugProperties.hurtOverlayEnabled = true;
        DrugProperties.digitalEffectPixelRescale = new float[]{0.05f, 0.05f};
        PSRenderStates.disableDepthBuffer = false;
        PSRenderStates.bypassPingPongBuffer = false;
        PSRenderStates.renderFakeSkybox = true;
        pauseMenuBlur = 5f;
    }
}
