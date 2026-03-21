package moriz.orangesunshine.client.render.blocks;

import moriz.orangesunshine.block.entity.MixingTableBlockEntity;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Axis;

/**
 * Migrated to 1.21.11 Mojmap with RenderState
 */
public class MixingTableEntityRenderer implements BlockEntityRenderer<MixingTableBlockEntity, MixingTableRenderState> {

    public MixingTableEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public MixingTableRenderState createRenderState() {
        return new MixingTableRenderState();
    }

    @Override
    public void extractRenderState(MixingTableBlockEntity entity, MixingTableRenderState state, float tickDelta, Vec3 offset, CrumblingOverlay crumbling) {
        state.item = entity.getRenderStack().copy();
    }

    @Override
    public void submit(MixingTableRenderState state, PoseStack matrices, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (!state.item.isEmpty()) {
            matrices.pushPose();
            matrices.translate(0.5f, 1.0f, 0.5f);
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.mulPose(Axis.XP.rotationDegrees(90));
            collector.order(0).submitItem(matrices, state.lightCoords, state.overlayCoords, state.item, ItemDisplayContext.FIXED);
            matrices.popPose();
        }
    }
}
