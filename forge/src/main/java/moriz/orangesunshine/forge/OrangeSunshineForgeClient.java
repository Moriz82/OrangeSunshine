package moriz.orangesunshine.forge;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.OrangeSunshinePlatform;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.block.entity.PSBlockEntities;
import moriz.orangesunshine.client.render.blocks.*;
import moriz.orangesunshine.client.screen.PSScreens;
import moriz.orangesunshine.entity.PSEntities;
import moriz.orangesunshine.entity.drug.DrugProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Optional;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = OrangeSunshinePlatform.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class OrangeSunshineForgeClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Wire drug properties and crosshair target suppliers
            OrangeSunshine.globalDrugProperties = () -> DrugProperties.of((net.minecraft.world.entity.Entity) Minecraft.getInstance().player);
            OrangeSunshine.crossHairTarget = () -> Optional.ofNullable(Minecraft.getInstance().hitResult);

            // Block render layers
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

            // Screen factories
            PSScreens.bootstrap();
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Entity renderers
        event.registerEntityRenderer(PSEntities.MOLOTOV_COCKTAIL, ThrownItemRenderer::new);

        // Block entity renderers
        event.registerBlockEntityRenderer(PSBlockEntities.BARREL, BarrelBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(PSBlockEntities.MASH_TUB, MashTubBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(PSBlockEntities.MIXING_TABLE_BLOCK_ENTITY, MixingTableEntityRenderer::new);
        event.registerBlockEntityRenderer(PSBlockEntities.DRYING_TABLE, DryingTableBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(PSBlockEntities.FLASK, FlaskBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(PSBlockEntities.PEYOTE, PeyoteBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(PSBlockEntities.RIFT_JAR, RiftJarBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(PSBlockEntities.BOTTLE_RACK, BottleRackBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(PSBlockEntities.PLACED_DRINK, DrinksBlockEntityRenderer::new);
    }
}
