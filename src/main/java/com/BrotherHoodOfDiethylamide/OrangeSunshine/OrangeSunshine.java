package com.BrotherHoodOfDiethylamide.OrangeSunshine;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ModBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container.CompoundCompressorContainer;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.ModRecipeTypes;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.screen.CompoundCompressorScreen;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.screen.CompoundExtractorScreen;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.screen.DryingTableScreen;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container.ModContainers;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.screen.FridgeScreen;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity.ModTileEntities;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.PlayerProperties;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.ShaderRenderer;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.commands.SetDrugCommand;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entity.ModVillagers;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.events.EventHandler;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.BongRegistry;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.CompostRegistry;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.network.PacketHandler;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.sounds.ModSounds;
import com.google.common.eventbus.Subscribe;
import net.minecraft.block.*;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

import java.util.Random;

import static com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine.MOD_ID;

@Mod(MOD_ID)
public class OrangeSunshine {
    public static final String MOD_ID = "orangesunshine";
    public static final Logger LOGGER = LogManager.getLogger(OrangeSunshine.MOD_ID);
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
        ModVillagers.VILLAGER_PROFESSIONS.register(modEventBus);
        ModVillagers.POINT_OF_INTEREST_TYPES.register(modEventBus);

        ModItems.init(modEventBus);
        ModBlocks.init();
        ModSounds.init();
        PacketHandler.init();

        ModRecipeTypes.register(modEventBus);

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
        ModVillagers.fillShmokeStackzTrades();
        ModVillagers.fillDrFleurTrades();
        /*event.enqueueWork(() -> {

        });*/
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ModBlocks.initRenderTypes();

        ScreenManager.register(ModContainers.DRYING_TABLE_CONTAINER.get(), DryingTableScreen::new);
        ScreenManager.register(ModContainers.FRIDGE_CONTAINER.get(), FridgeScreen::new);
        ScreenManager.register(ModContainers.COMPOUND_COMPRESSOR_CONTAINER.get(), CompoundCompressorScreen::new);
        ScreenManager.register(ModContainers.COMPOUND_EXTRACTOR_CONTAINER.get(), CompoundExtractorScreen::new);

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

    @SubscribeEvent
    public void blockBreakEvent(BlockEvent.BreakEvent event) {
        Random random = new Random();
        int num = random.nextInt(20);
        if (event.getState().getBlock() instanceof TallGrassBlock) {
            switch (num) {
                case 1:
                    ItemEntity entity = new ItemEntity((World) event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), new ItemStack(ModItems.WEED_SEEDS.get(), 1));
                    event.getWorld().addFreshEntity(entity);
                    break;
                case 2:
                    ItemEntity entity1 = new ItemEntity((World) event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), new ItemStack(ModItems.COCA_SEEDS.get(), 1));
                    event.getWorld().addFreshEntity(entity1);
                    break;
            }
        } else if (event.getState().getBlock() instanceof CropsBlock) {
            if (num == 1){
                ItemEntity entity = new ItemEntity((World) event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), new ItemStack(ModItems.ERGOROT_INFECTED_WHEAT.get(), 2));
                event.getWorld().addFreshEntity(entity);
            }
        }
    }
    @SubscribeEvent
    public void toolInteractEvent(BlockEvent.BlockToolInteractEvent event){
        if (event.getToolType().equals(ToolType.SHOVEL) && event.getState().getBlock().getName().getString().contains("Log")) {
            ItemEntity entity = new ItemEntity((World) event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), new ItemStack(ModItems.ROOT_BARK.get(), 1));
            event.getWorld().addFreshEntity(entity);
        }
    }

}

