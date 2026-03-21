/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render.blocks;

import moriz.orangesunshine.block.BottleRackBlock;
import moriz.orangesunshine.block.entity.BottleRackBlockEntity;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Axis;
import java.util.ArrayList;

/**
 * Migrated to 1.21.11 Mojmap with RenderState
 */
public class BottleRackBlockEntityRenderer implements BlockEntityRenderer<BottleRackBlockEntity, BottleRackRenderState> {

    public BottleRackBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public BottleRackRenderState createRenderState() {
        return new BottleRackRenderState();
    }

    @Override
    public void extractRenderState(BottleRackBlockEntity entity, BottleRackRenderState state, float tickDelta, Vec3 offset, CrumblingOverlay crumbling) {
        state.facing = entity.getBlockState().getValue(BottleRackBlock.FACING);
        state.items.clear();
        for (int i = 0; i < entity.getContainerSize(); i++) {
            state.items.add(entity.getItem(i).copy());
        }
    }

    @Override
    public void submit(BottleRackRenderState state, PoseStack matrices, SubmitNodeCollector collector, CameraRenderState cameraState) {
        matrices.pushPose();
        matrices.translate(0.5F, 0.5F, 0.5F);
        matrices.mulPose(Axis.YP.rotationDegrees(180 - state.facing.toYRot()));
        matrices.translate(-0.5F, -0.5F, -0.5F);

        for (int i = 0; i < state.items.size(); i++) {
            ItemStack stack = state.items.get(i);
            if (!stack.isEmpty()) {
                int x = i % 3;
                int y = i / 3;

                matrices.pushPose();
                matrices.translate(
                        0.5F - (x - 1) * 5F / 16F,
                        0.5F - (y - 1) * 5F / 16F,
                        0.5F
                );
                matrices.scale(0.4F, 0.4F, 0.4F);
                matrices.mulPose(Axis.XP.rotationDegrees(90));
                
                collector.order(0).submitItem(matrices, state.lightCoords, state.overlayCoords, stack, ItemDisplayContext.FIXED);
                matrices.popPose();
            }
        }

        matrices.popPose();
    }
}
