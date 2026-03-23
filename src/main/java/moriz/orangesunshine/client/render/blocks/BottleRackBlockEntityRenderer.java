/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render.blocks;

import moriz.orangesunshine.block.BottleRackBlock;
import moriz.orangesunshine.block.entity.BottleRackBlockEntity;
import moriz.orangesunshine.client.render.ItemSubmitHelper;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.HashCommon;

/**
 * Migrated to 1.21.11 Mojmap with RenderState (see vanilla {@code ShelfRenderer} / {@code CampfireRenderer}).
 */
public class BottleRackBlockEntityRenderer implements BlockEntityRenderer<BottleRackBlockEntity, BottleRackRenderState> {

    private final ItemModelResolver itemModelResolver;

    public BottleRackBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public BottleRackRenderState createRenderState() {
        return new BottleRackRenderState();
    }

    @Override
    public void extractRenderState(BottleRackBlockEntity entity, BottleRackRenderState state, float tickDelta, Vec3 offset, CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(entity, state, tickDelta, offset, crumbling);
        state.facing = entity.getBlockState().getValue(BottleRackBlock.FACING);
        int seedBase = HashCommon.long2int(entity.getBlockPos().asLong());
        for (int i = 0; i < state.itemLayers.length; i++) {
            ItemStack stack = entity.getItem(i);
            if (!stack.isEmpty()) {
                ItemSubmitHelper.updateForBlock(itemModelResolver, state.itemLayers[i], stack, ItemDisplayContext.FIXED, entity.getLevel(), seedBase + i);
            } else {
                state.itemLayers[i].clear();
            }
        }
    }

    @Override
    public void submit(BottleRackRenderState state, PoseStack matrices, SubmitNodeCollector collector, CameraRenderState cameraState) {
        matrices.pushPose();
        matrices.translate(0.5F, 0.5F, 0.5F);
        matrices.mulPose(Axis.YP.rotationDegrees(180 - state.facing.toYRot()));
        matrices.translate(-0.5F, -0.5F, -0.5F);

        for (int i = 0; i < state.itemLayers.length; i++) {
            if (state.itemLayers[i].isEmpty()) {
                continue;
            }
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

            ItemSubmitHelper.submit(state.itemLayers[i], matrices, collector, state.lightCoords);
            matrices.popPose();
        }

        matrices.popPose();
    }
}
