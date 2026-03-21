/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render.blocks;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.PeyoteBlock;
import moriz.orangesunshine.block.entity.PeyoteBlockEntity;
import moriz.orangesunshine.client.render.RenderUtil;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.RenderType;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * Migrated to 1.21.11 Mojmap with RenderState
 */
public class PeyoteBlockEntityRenderer implements BlockEntityRenderer<PeyoteBlockEntity, PeyoteRenderState> {
    private static final Identifier[] TEXTURES = IntStream.range(0, 4)
            .mapToObj(i -> OrangeSunshine.id("textures/entity/peyote/peyote_stage" + i + ".png"))
            .toArray(Identifier[]::new);

    private final PeyoteModel[] models;

    public PeyoteBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        models = new PeyoteModel[] {
            new PeyoteModel(PeyoteModel.stage0().bakeRoot()),
            new PeyoteModel(PeyoteModel.stage1().bakeRoot()),
            new PeyoteModel(PeyoteModel.stage2().bakeRoot()),
            new PeyoteModel(PeyoteModel.stage3().bakeRoot())
        };
    }

    @Override
    public PeyoteRenderState createRenderState() {
        return new PeyoteRenderState();
    }

    @Override
    public void extractRenderState(PeyoteBlockEntity entity, PeyoteRenderState state, float tickDelta, Vec3 offset, CrumblingOverlay crumbling) {
        state.age = entity.getBlockState().getValue(PeyoteBlock.AGE) % 4;
        state.seed = entity.getBlockState().getSeed(entity.getPos());
        state.offset = entity.getBlockState().getOffset(entity.getLevel(), entity.getPos());
        state.texture = TEXTURES[state.age];
    }

    @Override
    public void submit(PeyoteRenderState state, PoseStack matrices, SubmitNodeCollector collector, CameraRenderState cameraState) {
        matrices.pushPose();
        matrices.translate(0.5F, 0.5f, 0.5F);
        matrices.translate(0, 1, 0);

        Random rng = RenderUtil.random(state.seed);
        matrices.translate(state.offset.x, state.offset.y, state.offset.z);
        matrices.mulPose(Axis.YP.rotationDegrees(rng.nextInt(4) * 180));
        matrices.mulPose(Axis.XP.rotationDegrees(180));

        PeyoteModel model = models[state.age];
        collector.order(0).submitModel(model, state, matrices, model.renderType(state.texture), state.lightCoords, state.overlayCoords, 0xFFFFFFFF, state.breakProgress);

        matrices.popPose();
    }
}
