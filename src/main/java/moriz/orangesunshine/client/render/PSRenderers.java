package moriz.orangesunshine.client.render;

import java.util.List;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.block.entity.PSBlockEntities;
import moriz.orangesunshine.client.particle.PSParticleFactories;
import moriz.orangesunshine.client.render.blocks.*;
import moriz.orangesunshine.client.render.shader.PSShaders;
import moriz.orangesunshine.entity.PSEntities;
import moriz.orangesunshine.fluid.container.VariantMarshal;
import moriz.orangesunshine.item.PSItems;
import org.jetbrains.annotations.Nullable;

import com.terraformersmc.terraform.boat.api.client.TerraformBoatClientHelper;

import moriz.orangesunshine.block.entity.*;
import moriz.orangesunshine.client.render.blocks.*;
import moriz.orangesunshine.entity.*;
import moriz.orangesunshine.fluid.SimpleFluid;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

/**
 * @author Sollace
 * @since 1 Jan 2023
 */
public interface PSRenderers {
    static void bootstrap() {
        EntityRendererRegistry.register(PSEntities.MOLOTOV_COCKTAIL, context -> new FlyingItemEntityRenderer<>(context, 1, true));
        EntityRendererRegistry.register(PSEntities.REALITY_RIFT, RealityRiftEntityRenderer::new);

        BlockEntityRendererFactories.register(PSBlockEntities.DISTILLERY, FlaskBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(PSBlockEntities.FLASK, FlaskBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(PSBlockEntities.MASH_TUB, MashTubBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(PSBlockEntities.BARREL, BarrelBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(PSBlockEntities.DRYING_TABLE, DryingTableBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(PSBlockEntities.RIFT_JAR, RiftJarBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(PSBlockEntities.MIXING_TABLE_BLOCK_ENTITY, MixingTableEntityRenderer::new);
        BlockEntityRendererFactories.register(PSBlockEntities.BOTTLE_RACK, BottleRackBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(PSBlockEntities.PEYOTE, PeyoteBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(PSBlockEntities.PLACED_DRINK, DrinksBlockEntityRenderer::new);

        PreparableModelLoadingPlugin.register(PlacedDrinksModelProvider.INSTANCE, PlacedDrinksModelProvider.INSTANCE);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), PSBlocks.DISTILLERY, PSBlocks.FLASK);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(),
                PSBlocks.JUNIPER_SAPLING, PSBlocks.JUNIPER_LEAVES, PSBlocks.FRUITING_JUNIPER_LEAVES, PSBlocks.LATTICE, PSBlocks.WINE_GRAPE_LATTICE, PSBlocks.MORNING_GLORY_LATTICE,
                PSBlocks.CANNABIS, PSBlocks.HOP, PSBlocks.TOBACCO, PSBlocks.COCA, PSBlocks.COFFEA, PSBlocks.MORNING_GLORY,
                PSBlocks.AGAVE_PLANT,
                PSBlocks.JIMSONWEEED, PSBlocks.BELLADONNA, PSBlocks.TOMATOES,
                PSBlocks.MASH_TUB, PSBlocks.JUNIPER_DOOR, PSBlocks.JUNIPER_TRAPDOOR,
                PSBlocks.POTTED_CANNABIS, PSBlocks.POTTED_JUNIPER_SAPLING, PSBlocks.POTTED_MORNING_GLORY,
                PSBlocks.POTTED_HOP, PSBlocks.POTTED_TOBACCO, PSBlocks.POTTED_COCA, PSBlocks.POTTED_COFFEA);

        BuiltinItemRendererRegistry.INSTANCE.register(PSItems.RIFT_JAR, RiftJarBlockEntityRenderer::renderStack);

        SimpleFluid.all().forEach(fluid -> {
            FluidRenderHandlerRegistry.INSTANCE.register(fluid.getPhysical().getStandingFluid(), fluid.getPhysical().getFlowingFluid(), new SimpleFluidRenderHandler(SimpleFluidRenderHandler.WATER_STILL, SimpleFluidRenderHandler.WATER_FLOWING, SimpleFluidRenderHandler.WATER_OVERLAY, 0) {
                @Override
                public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
                    return fluid.getColor(fluid.getDefaultStack());
                }
            });
            FluidVariantRendering.register(fluid.getPhysical().getStandingFluid(), new FluidVariantRenderHandler() {
                @Override
                public int getColor(FluidVariant fluidVariant, @Nullable BlockRenderView view, @Nullable BlockPos pos) {
                    return fluid.getColor(VariantMarshal.unpackFluid(Items.STONE.getDefaultStack(), fluidVariant, 1).asStack());
                }

                @Override
                public void appendTooltip(FluidVariant fluidVariant, List<Text> tooltip, TooltipContext tooltipContext) {
                    fluid.appendTooltip(VariantMarshal.unpackFluid(Items.STONE.getDefaultStack(), fluidVariant, 1).asStack(), null, tooltip, tooltipContext);
                }
            });
        });

        TerraformBoatClientHelper.registerModelLayers(OrangeSunshine.id("juniper"), false);

        PSParticleFactories.bootstrap();
        PSShaders.bootstrap();
    }
}
