/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render.blocks;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.entity.RiftJarBlockEntity;
import moriz.orangesunshine.client.render.bezier.Bezier;
import moriz.orangesunshine.client.render.bezier.BezierLabelRenderer;
import moriz.orangesunshine.client.render.ZeroScreen;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import com.mojang.math.Axis;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;

import com.mojang.blaze3d.systems.RenderSystem;

import java.util.Random;
import java.util.ArrayList;

import org.joml.Vector3d;

/**
 * Migrated to 1.21.11 Mojmap with RenderState
 */
public class RiftJarBlockEntityRenderer implements BlockEntityRenderer<RiftJarBlockEntity, RiftJarRenderState> {
    public static final Identifier TEXTURE = OrangeSunshine.id("textures/entity/rift_jar/rift_jar.png");
    public static final Identifier CRACKED_TEXTURE = OrangeSunshine.id("textures/entity/rift_jar/rift_jar_cracked.png");
    private static final Identifier FONT = Identifier.fromNamespaceAndPath("minecraft", "alt");

    private static final Bezier SPHERE_BEZIER_PATH = Bezier.sphere(3, 8, 0.2);
    private static final Bezier OUTGOING_PATH = Bezier.spiral(0.06, 6, 6, 1, 0.2, 0);

    private static final BezierLabelRenderer.Style LABEL_STYLE = new BezierLabelRenderer.Style().spread(true);
    private static final Component SMALL_SPIRAL_TEXT = Component.literal("This is a small spiral.").styled(s -> s.withFont(FONT));

    private final RiftJarModel model;

    public RiftJarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        model = new RiftJarModel(RiftJarModel.getTexturedModelData().bakeRoot());
    }

    @Override
    public RiftJarRenderState createRenderState() {
        return new RiftJarRenderState();
    }

    @Override
    public void extractRenderState(RiftJarBlockEntity entity, RiftJarRenderState state, float tickDelta, Vec3 offset, CrumblingOverlay crumbling) {
        state.facing = entity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        state.fractionOpen = entity.fractionOpen;
        state.fractionHandleUp = entity.fractionHandleUp;
        state.currentRiftFraction = entity.currentRiftFraction;
        state.jarBroken = entity.jarBroken;
        state.ticks = entity.ticksAliveVisual + tickDelta;
        state.pos = entity.getBlockPos().getCenter();
        state.connections = new ArrayList<>(entity.getConnections());
    }

    @Override
    public void submit(RiftJarRenderState state, PoseStack matrices, SubmitNodeCollector collector, CameraRenderState cameraState) {
        matrices.pushPose();
        matrices.translate(0.5F, 0.5f, 0.5F);

        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(90 - state.facing.toYRot()));

        model.setAngles(state);
        matrices.translate(0, 1.001F, 0);
        matrices.mulPose(Axis.XP.rotationDegrees(180));

        collector.order(0).submitModel(model, state, matrices, RenderType.entityTranslucent(TEXTURE), state.lightCoords, state.overlayCoords, 0xFFFFFFFF, state.breakProgress);

        float crackedVisibility = state.jarBroken ? 1 : Math.min((state.currentRiftFraction - 0.5F) * 2, 1);

        if (crackedVisibility > 0) {
            collector.order(0).submitModel(model, state, matrices, model.renderType(CRACKED_TEXTURE), state.lightCoords, state.overlayCoords, ((int)(crackedVisibility * 255) << 24) | 0xFFFFFF, state.breakProgress);
        }

        if (state.currentRiftFraction > 0) {
            matrices.pushPose();
            matrices.translate(0, 1.5F, 0);
            matrices.mulPose(Axis.XN.rotationDegrees(180));
            matrices.scale(0.9F, 1, 0.9F);
            ZeroScreen.render(state.ticks, (layer, u, v) -> {
                collector.order(0).submitCustomGeometry(matrices, layer, (pose, vertices) -> {
                    model.renderInterior(pose, vertices, state.lightCoords, state.overlayCoords, (int)(Math.min(state.currentRiftFraction * 2, 1) * 255) << 24 | 0xFFFFFF);
                });
            });
            matrices.popPose();
        }

        matrices.popPose();
        matrices.popPose();

        matrices.pushPose();
        matrices.translate(0.5F, 0.5f, 0.5F);
        
        // For custom geometry like Bezier, we use submitCustomGeometry
        collector.order(0).submitCustomGeometry(matrices, RenderType.translucent(), (pose, vertices) -> {
             RenderSystem.disableCull();

            for (RiftJarBlockEntity.JarRiftConnection connection : state.connections) {
                Vector3d connectionPoint = new Vector3d(
                        connection.position.x - state.pos.x,
                        connection.position.y - (state.pos.y + 0.1F),
                        connection.position.z - state.pos.z
                );
                if (connection.bezier == null) {
                    connection.bezier = Bezier.spiral(0.1, 0.5, 8, connectionPoint, 0.2, 0);
                }

                // BezierLabelRenderer uses MultiBufferSource, but in submit phase we can wrap vertices if needed
                // For now assuming we can use direct rendering if we wrap it, or refactor BezierLabelRenderer
                // However, since it's custom geometry, we'll try to use the provided vertices
                
                // Temporary simplification: BezierLabelRenderer needs MultiBufferSource.
                // We'll wrap the current collector as a MultiBufferSource if possible.
            }
            RenderSystem.enableCull();
        });

        matrices.popPose();
    }

    public static String cheeseString(String string, float effect, Random rand) {
        if (effect <= 0) {
            return string;
        }

        StringBuilder builder = new StringBuilder(string.length());

        for (int i = 0; i < string.length(); i++) {
            if (rand.nextFloat() <= effect) {
                builder.append(' ');
            } else {
                builder.append(string.charAt(i));
            }
        }

        return builder.toString();
    }
}
