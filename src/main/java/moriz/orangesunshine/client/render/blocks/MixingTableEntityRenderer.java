package moriz.orangesunshine.client.render.blocks;

import moriz.orangesunshine.block.entity.MixingTableBlockEntity;
import moriz.orangesunshine.client.render.ItemSubmitHelper;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.HashCommon;

/**
 * Migrated to 1.21.11 Mojmap with RenderState
 */
public class MixingTableEntityRenderer implements BlockEntityRenderer<MixingTableBlockEntity, MixingTableRenderState> {

    private final ItemModelResolver itemModelResolver;

    public MixingTableEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public MixingTableRenderState createRenderState() {
        return new MixingTableRenderState();
    }

    @Override
    public void extractRenderState(MixingTableBlockEntity entity, MixingTableRenderState state, float tickDelta, Vec3 offset, CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(entity, state, tickDelta, offset, crumbling);
        var stack = entity.getRenderStack();
        if (!stack.isEmpty()) {
            int seed = HashCommon.long2int(entity.getBlockPos().asLong());
            ItemSubmitHelper.updateForBlock(itemModelResolver, state.itemLayer, stack, ItemDisplayContext.FIXED, entity.getLevel(), seed);
        } else {
            state.itemLayer.clear();
        }
    }

    @Override
    public void submit(MixingTableRenderState state, PoseStack matrices, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.itemLayer.isEmpty()) {
            return;
        }
        matrices.pushPose();
        matrices.translate(0.5f, 1.0f, 0.5f);
        matrices.scale(0.5f, 0.5f, 0.5f);
        matrices.mulPose(Axis.XP.rotationDegrees(90));
        ItemSubmitHelper.submit(state.itemLayer, matrices, collector, state.lightCoords);
        matrices.popPose();
    }
}
