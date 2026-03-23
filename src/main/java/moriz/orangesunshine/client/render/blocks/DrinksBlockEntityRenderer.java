/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render.blocks;

import moriz.orangesunshine.block.PlacedDrinksBlock;
import moriz.orangesunshine.client.render.PlacedDrinksModelProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import com.mojang.math.Axis;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;

/**
 * Migrated to 1.21.11 Mojmap with RenderState
 */
public class DrinksBlockEntityRenderer implements BlockEntityRenderer<PlacedDrinksBlock.Data, DrinksRenderState> {
    private static final VoxelShape FILLED_SLOT_RAY_TRACE_SHAPE = Block.box(-2 + 8, 0, -2 + 8, 2 + 8, 4, 2 + 8);
    private static final VoxelShape EMPTY_SLOT_RAY_TRACE_SHAPE = Block.box(-2 + 8, 0, -2 + 8, 2 + 8, 0.16, 2 + 8);

    public DrinksBlockEntityRenderer(BlockEntityRendererProvider.Context context) { }

    @Override
    public DrinksRenderState createRenderState() {
        return new DrinksRenderState();
    }

    @Override
    public void extractRenderState(PlacedDrinksBlock.Data entity, DrinksRenderState state, float tickDelta, Vec3 offset, CrumblingOverlay crumbling) {
        state.drinks.clear();
        entity.forEachDrink((y, drink) -> {
            float height = PlacedDrinksModelProvider.INSTANCE.get(drink.stack().getItem()).orElse(PlacedDrinksModelProvider.DEFAULT).height();
            state.drinks.add(new DrinksRenderState.DrinkRenderEntry(drink.x(), y, drink.z(), drink.rotation(), drink.stack().copy(), height));
            return height;
        });

        Minecraft client = Minecraft.getInstance();
        state.hitPos = null;
        if (client.player != null && client.hitResult != null && client.hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hit = (BlockHitResult)client.hitResult;
            if (hit.getBlockPos().equals(entity.getBlockPos())) {
                PlacedDrinksBlock.Data.getHitPos(hit).ifPresent(pos -> {
                    state.hitPos = pos;
                    state.hasDrinkAtHitPos = entity.hasDrink(pos);
                });
            }
        }
    }

    @Override
    public void submit(DrinksRenderState state, PoseStack matrices, SubmitNodeCollector collector, CameraRenderState cameraState) {
        for (DrinksRenderState.DrinkRenderEntry drink : state.drinks) {
            matrices.pushPose();
            matrices.translate(drink.x(), drink.y(), drink.z());
            matrices.translate(0.5F, 0, 0.5F);
            matrices.mulPose(Axis.YP.rotationDegrees(drink.rotation()));
            matrices.translate(-0.5F, 0, -0.5F);
            PlacedDrinksModelProvider.INSTANCE.submitDrink(drink.stack(), matrices, collector, state.lightCoords, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
            matrices.popPose();
        }

        if (state.hitPos != null) {
            collector.order(0).submitCustomGeometry(matrices, net.minecraft.client.renderer.rendertype.RenderTypes.lines(), (pose, vertices) -> {
                ShapeRenderer.renderShape(
                        matrices,
                        vertices,
                        state.hasDrinkAtHitPos ? FILLED_SLOT_RAY_TRACE_SHAPE : EMPTY_SLOT_RAY_TRACE_SHAPE,
                        state.hitPos.getX() / 16.0,
                        0.0,
                        state.hitPos.getZ() / 16.0,
                        0xFFFFFFFF,
                        0.4F
                );
            });
        }
    }
}
