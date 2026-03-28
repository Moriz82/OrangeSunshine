package moriz.orangesunshine.neoforge;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.OrangeSunshinePlatform;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.block.entity.FlaskBlockEntity;
import moriz.orangesunshine.client.screen.BarrelScreen;
import moriz.orangesunshine.client.screen.DistilleryScreen;
import moriz.orangesunshine.client.screen.DryingTableScreen;
import moriz.orangesunshine.client.screen.FlaskScreen;
import moriz.orangesunshine.client.screen.MixingTableScreen;
import moriz.orangesunshine.client.screen.MortarPestleScreen;
import moriz.orangesunshine.client.screen.MushTubScreen;
import moriz.orangesunshine.entity.PSEntities;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.screen.PSScreenHandlers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.Optional;

@Mod(value = OrangeSunshinePlatform.MOD_ID, dist = Dist.CLIENT)
public final class OrangeSunshineNeoForgeClient {
    public OrangeSunshineNeoForgeClient(IEventBus modEventBus) {
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::onRegisterRenderers);
        modEventBus.addListener(this::onRegisterMenuScreens);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Wire drug properties and crosshair target suppliers
            OrangeSunshine.globalDrugProperties = () -> DrugProperties.of((net.minecraft.world.entity.Entity) Minecraft.getInstance().player);
            OrangeSunshine.crossHairTarget = () -> Optional.ofNullable(Minecraft.getInstance().hitResult);

            // Block render layers — mirrors PSRenderers.bootstrap()
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.DISTILLERY, ChunkSectionLayer.TRANSLUCENT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.FLASK, ChunkSectionLayer.TRANSLUCENT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.JUNIPER_SAPLING, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.JUNIPER_LEAVES, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.FRUITING_JUNIPER_LEAVES, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.LATTICE, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.WINE_GRAPE_LATTICE, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.MORNING_GLORY_LATTICE, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.CANNABIS, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.HOP, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.TOBACCO, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.COCA, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.COFFEA, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.MORNING_GLORY, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.AGAVE_PLANT, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.JIMSONWEEED, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.BELLADONNA, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.TOMATOES, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.MASH_TUB, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.JUNIPER_DOOR, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.JUNIPER_TRAPDOOR, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.POTTED_CANNABIS, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.POTTED_JUNIPER_SAPLING, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.POTTED_MORNING_GLORY, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.POTTED_HOP, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.POTTED_TOBACCO, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.POTTED_COCA, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.POTTED_COFFEA, ChunkSectionLayer.CUTOUT);
        });
    }

    private void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(PSScreenHandlers.DRYING_TABLE, DryingTableScreen::new);
        event.register(PSScreenHandlers.BARREL, BarrelScreen::new);
        event.register(PSScreenHandlers.DISTILLERY, DistilleryScreen::new);
        event.register(PSScreenHandlers.FLASK, FlaskScreen<FlaskBlockEntity>::new);
        event.register(PSScreenHandlers.MASH_TUB, MushTubScreen::new);
        event.register(PSScreenHandlers.MORTAR_PESTLE, MortarPestleScreen::new);
        event.register(PSScreenHandlers.MIXING_TABLE, MixingTableScreen::new);
    }

    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(PSEntities.MOLOTOV_COCKTAIL, ThrownItemRenderer::new);
    }
}
