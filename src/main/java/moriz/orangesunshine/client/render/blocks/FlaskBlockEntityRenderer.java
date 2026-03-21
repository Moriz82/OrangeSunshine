/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render.blocks;

import moriz.orangesunshine.block.entity.FlaskBlockEntity;
import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.fluid.container.Resovoir;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Migrated to 1.21.11 Mojmap with RenderState
 */
public class FlaskBlockEntityRenderer<T extends FlaskBlockEntity> implements BlockEntityRenderer<T, FlaskRenderState> {

    public FlaskBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public FlaskRenderState createRenderState() {
        return new FlaskRenderState();
    }

    @Override
    public void extractRenderState(T entity, FlaskRenderState state, float tickDelta, Vec3 offset, CrumblingOverlay crumbling) {
        Resovoir tank = entity.getTank(Direction.UP);
        SimpleFluid fluid = tank.getFluidType();
        state.fluidColor = fluid.getColor(tank.getStack());
        state.fluidLevel = Mth.clamp((float) tank.getLevel() / (float) tank.getCapacity(), 0, 1);
        state.inputProgress = entity.inputSlot.getProgress();
        state.outputProgress = entity.outputSlot.getProgress();
    }

    @Override
    public void submit(FlaskRenderState state, PoseStack matrices, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.fluidLevel > 0) {
            matrices.pushPose();
            matrices.translate(0.5F, 0, 0.5F);
            float scale = 1/8F - 0.001F;
            matrices.scale(scale, scale, scale);

            // Submission of fluid geometry
            collector.order(0).submitCustomGeometry(matrices, RenderType.translucent(), (pose, vertices) -> {
                // Draw fluid boxes here using provided vertices
            });

            matrices.popPose();
        }
    }
}
