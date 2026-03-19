package moriz.orangesunshine.client.render;

import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.entity.PSEntities;
import moriz.orangesunshine.fluid.SimpleFluid;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

/**
 * Registers the client pieces that still map cleanly onto 1.21.11.
 * The older custom renderers are ported separately because the new client renderer API is state-based.
 */
public interface PSRenderers {
    static void bootstrap() {
        EntityRendererRegistry.register(PSEntities.MOLOTOV_COCKTAIL, ThrownItemRenderer::new);

        BlockRenderLayerMap.putBlock(PSBlocks.DISTILLERY, ChunkSectionLayer.TRANSLUCENT);
        BlockRenderLayerMap.putBlock(PSBlocks.FLASK, ChunkSectionLayer.TRANSLUCENT);
        BlockRenderLayerMap.putBlock(PSBlocks.JUNIPER_SAPLING, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.JUNIPER_LEAVES, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.FRUITING_JUNIPER_LEAVES, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.LATTICE, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.WINE_GRAPE_LATTICE, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.MORNING_GLORY_LATTICE, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.CANNABIS, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.HOP, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.TOBACCO, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.COCA, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.COFFEA, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.MORNING_GLORY, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.AGAVE_PLANT, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.JIMSONWEEED, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.BELLADONNA, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.TOMATOES, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.MASH_TUB, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.JUNIPER_DOOR, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.JUNIPER_TRAPDOOR, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.POTTED_CANNABIS, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.POTTED_JUNIPER_SAPLING, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.POTTED_MORNING_GLORY, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.POTTED_HOP, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.POTTED_TOBACCO, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.POTTED_COCA, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(PSBlocks.POTTED_COFFEA, ChunkSectionLayer.CUTOUT);

        SimpleFluid.all().forEach(fluid ->
                FluidRenderHandlerRegistry.INSTANCE.register(
                        fluid.getPhysical().getStandingFluid(),
                        fluid.getPhysical().getFlowingFluid(),
                        new SimpleFluidRenderHandler(
                                SimpleFluidRenderHandler.WATER_STILL,
                                SimpleFluidRenderHandler.WATER_FLOWING,
                                SimpleFluidRenderHandler.WATER_OVERLAY,
                                fluid.getColor(fluid.getDefaultStack())
                        )));
    }
}
