package com.BrotherHoodOfDiethylamide.OrangeSunshine;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ModBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container.CompoundCompressorContainer;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.screen.CompoundCompressorScreen;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.screen.DryingTableScreen;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container.ModContainers;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.screen.FridgeScreen;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity.ModTileEntities;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.PlayerProperties;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.ShaderRenderer;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.commands.SetDrugCommand;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.BongRegistry;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.CompostRegistry;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.network.PacketHandler;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.sounds.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

@Mod(OrangeSunshine.MOD_ID)
public class OrangeSunshine {
    public static final String MOD_ID = "orangesunshine";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ItemGroup TAB = new ItemGroup("creative_tab") {
        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return ModItems.ORANGESUNSHINE_BLOTTER.get().getDefaultInstance();
        }
    };

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);
    public static final DeferredRegister<Drug> DRUGS = DeferredRegister.create(Drug.class, MOD_ID);

    public OrangeSunshine() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        SOUNDS.register(modEventBus);
        DRUGS.register(modEventBus);
        ModTileEntities.register(modEventBus);
        ModContainers.register(modEventBus);

        ModItems.init(modEventBus);
        ModBlocks.init();
        ModSounds.init();
        PacketHandler.init();

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(this::processIMC);
        modEventBus.addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        PlayerProperties.register();
        CompostRegistry.register();
        BongRegistry.register();
        SetDrugCommand.registerSerializer();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ModBlocks.initRenderTypes();

        ScreenManager.register(ModContainers.DRYING_TABLE_CONTAINER.get(), DryingTableScreen::new);
        ScreenManager.register(ModContainers.FRIDGE_CONTAINER.get(), FridgeScreen::new);
        ScreenManager.register(ModContainers.COMPOUND_COMPRESSOR_CONTAINER.get(), CompoundCompressorScreen::new);

        ShaderRenderer.setup();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
    }

    private void processIMC(final InterModProcessEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        SetDrugCommand.register(event.getServer().getCommands().getDispatcher());
    }
}
