/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.entity.RealityRiftEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;

import org.joml.*;

import com.mojang.blaze3d.systems.RenderSystem;

import java.util.Random;

/**
 * Migrated to 1.21.11 Mojmap with RenderState
 */
public class RealityRiftEntityRenderer extends EntityRenderer<RealityRiftEntity, RealityRiftRenderState> {
    public static final Identifier CENTER_TEXTURE = OrangeSunshine.id("textures/entity/reality_rift/zero_center.png");
    private static final Random RANDOM = new Random(432L);

    public RealityRiftEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public RealityRiftRenderState createRenderState() {
        return new RealityRiftRenderState();
    }

    @Override
    public void extractRenderState(RealityRiftEntity entity, RealityRiftRenderState state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.visualRiftSize = entity.visualRiftSize;
        state.instability = entity.getInstability();
        state.ticks = entity.tickCount + tickDelta;
        state.bbHeight = entity.getBbHeight();
    }

    @Override
    public void submit(RealityRiftRenderState state, PoseStack matrices, SubmitNodeCollector collector, CameraRenderState cameraState) {
        matrices.pushPose();
        matrices.translate(0, state.bbHeight * 0.5, 0);

        float visualRiftSize = state.visualRiftSize < 0.01f
                ? (state.visualRiftSize * 10.0f)
                : (0.1f + (state.visualRiftSize - 0.01f) * 0.1f);

        matrices.scale(visualRiftSize, visualRiftSize, visualRiftSize);

        float riftTicks = state.ticks + (state.instability * state.instability * 3000);
        
        ZeroScreen.render(riftTicks, (layer, u, v) -> {
            collector.order(0).submitCustomGeometry(matrices, layer, (pose, vertices) -> {
                renderLightsScreen(pose, vertices, u, v, riftTicks, 1, 0xffffffff, 20);
            });
        });

        collector.order(0).submitCustomGeometry(matrices, RenderType.entityTranslucentEmissive(CENTER_TEXTURE), (pose, vertices) -> {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            
            matrices.pushPose();
            matrices.scale(5F, 5F, 5F);
            Matrix4f positionMatrix = matrices.last().pose();

            float size = 1;
            int light = 0xF000F0;

            Quaternionf cameraRotation = cameraState.orientation;
            matrices.mulPose(cameraRotation);
            matrices.translate(-size * 0.5F, -size * 0.5F, 0);

            Vector4f vector = new Vector4f(0, 0, 0, 1);
            Vector4f pos = positionMatrix.transform(vector);
            vertices.vertex(pos.x, pos.y, pos.z, 1, 1, 1, 1, 0, 0, 0, light, 0, 1, 1);

            vector.set(size, 0, 0, 1);
            pos = positionMatrix.transform(vector);
            vertices.vertex(pos.x, pos.y, pos.z, 1, 1, 1, 1, 1, 0, 0, light, 0, 1, 1);

            vector.set(size, size, 0, 1);
            pos = positionMatrix.transform(vector);
            vertices.vertex(pos.x, pos.y, pos.z, 1, 1, 1, 1, 1, 1, 0, light, 0, 1, 1);

            vector.set(0, size, 0, 1);
            pos = positionMatrix.transform(vector);
            vertices.vertex(pos.x, pos.y, pos.z, 1, 1, 1, 1, 0, 1, 0, light, 0, 1, 1);

            matrices.popPose();
            RenderSystem.disableBlend();
        });

        matrices.popPose();
    }

    public static void renderLightsScreen(PoseStack matrices, VertexConsumer vertices, float u, float v, float ticks, float alpha, int color, int number) {
        RANDOM.setSeed(432L);
        matrices.pushPose();

        float width = 2.5F;
        float rotation = ticks / 200F;

        Matrix4f positionMatrix = matrices.last().pose();
        int light = 0xF000F0;

        Vector4f vector = new Vector4f(0, 0, 0, 1);

        for (int i = 0; i < number; ++i) {
            float xLogFunc = (((float) i / number * 28493.0f + ticks) / 10F) % 20F;
            if (xLogFunc > 10) {
                xLogFunc = 20 - xLogFunc;
            }

            float lightAlpha = 1F / (1 + (float) Math.pow(2.71828f, -0.8F * xLogFunc) * ((1F / 0.01F) - 1));

            if (lightAlpha > 0.01F) {
                matrices.mulPose(new Quaternionf().rotateXYZ(
                        RANDOM.nextFloat() * Mth.TAU,
                        RANDOM.nextFloat() * Mth.TAU,
                        RANDOM.nextFloat() * Mth.TAU
                ));
                matrices.mulPose(new Quaternionf().rotateXYZ(
                        RANDOM.nextFloat() * Mth.TAU,
                        RANDOM.nextFloat() * Mth.TAU,
                        RANDOM.nextFloat() * Mth.TAU + rotation * Mth.HALF_PI * 0.5F
                ));

                float var8 = RANDOM.nextFloat() * 20 + 5;
                float var9 = RANDOM.nextFloat() * 2 + 1;

                vector.set(0, 0, 0, 1);
                Vector4f pos = positionMatrix.transform(vector);
                float centerAlpha = alpha * lightAlpha;

                vertices.vertex(pos.x, pos.y, pos.z, 1, 1, 1, centerAlpha, 0, 0, 0, light, 0, 1, 1);

                vector.set(-width * var9, var8, -0.5F * var9, 1);
                pos = positionMatrix.transform(vector);
                vertices.vertex(pos.x, pos.y, pos.z, 1, 1, 1, 0, 1, 0, 0, light, 0, 1, 1);

                vector.set(width * var9, var8, -0.5F * var9, 1);
                pos = positionMatrix.transform(vector);
                vertices.vertex(pos.x, pos.y, pos.z, 1, 1, 1, 0, 0, 1, 0, light, 0, 1, 1);

                vector.set(0, var8, var9, 1);
                pos = positionMatrix.transform(vector);
                vertices.vertex(pos.x, pos.y, pos.z, 1, 1, 1, 0, 1, 1, 0, light, 0, 1, 1);

                vector.set(-width * var9, var8, -0.5F * var9, 1);
                pos = positionMatrix.transform(vector);
                vertices.vertex(pos.x, pos.y, pos.z, 1, 1, 1, 0, 1, 1, 0, light, 0, 1, 1);
            }
        }

        matrices.popPose();
    }
}
