/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render.blocks;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.phys.Vec3;

import moriz.orangesunshine.block.entity.DryingTableBlockEntity;

import java.util.Random;

/**
 * Migrated to 1.21.11 Mojmap with RenderState
 */
public class DryingTableBlockEntityRenderer implements BlockEntityRenderer<DryingTableBlockEntity, DryingTableRenderState> {
    public DryingTableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public DryingTableRenderState createRenderState() {
        return new DryingTableRenderState();
    }

    @Override
    public void extractRenderState(DryingTableBlockEntity entity, DryingTableRenderState state, float tickDelta, Vec3 offset, CrumblingOverlay crumbling) {
        state.items.clear();
        state.seed = entity.getPos().asLong() + 1;
        Random random = new Random(state.seed);

        for (int i = 0; i < entity.size(); i++) {
            ItemStack stack = entity.getItem(i);
            if (!stack.isEmpty()) {
                DryingTableRenderState.ItemState item = new DryingTableRenderState.ItemState();
                item.stack = stack.copy();
                item.result = i == 0;
                item.x = item.result ? 0.5F : (0.35F + random.nextFloat() * 0.3F);
                item.z = item.result ? 0.5F : (0.35F + random.nextFloat() * 0.3F);
                item.rotation = random.nextFloat() * 360.0f;
                state.items.add(item);
            }
        }
    }

    @Override
    public void submit(DryingTableRenderState state, PoseStack matrices, SubmitNodeCollector collector, CameraRenderState cameraState) {
        for (int i = 0; i < state.items.size(); i++) {
            DryingTableRenderState.ItemState item = state.items.get(i);
            matrices.pushPose();
            matrices.translate(item.x, 0.75f + (i / 500F), item.z);
            matrices.mulPose(Axis.YP.rotationDegrees(item.rotation));
            matrices.scale(0.5f, 0.5f, 0.5f);
            if (item.result) {
                matrices.scale(1.5F, 1.5F, 1.5F);
            }
            matrices.translate(0, 0, -0.2F);
            matrices.mulPose(Axis.XP.rotationDegrees(90));
            matrices.mulPose(Axis.ZN.rotationDegrees(-50));

            collector.order(0).submitItem(matrices, item.stack, state.lightCoords, state.overlayCoords, ItemDisplayContext.FIXED);
            matrices.popPose();
        }
    }
}
