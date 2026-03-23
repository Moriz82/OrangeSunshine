/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.entity.RealityRiftEntity;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;

import org.joml.*;

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

        collector.order(0).submitCustomGeometry(matrices, RenderTypes.entityTranslucentEmissive(CENTER_TEXTURE), (pose, vertices) -> {
            matrices.pushPose();
            matrices.scale(5F, 5F, 5F);
            matrices.mulPose(cameraState.orientation);
            float size = 1;
            matrices.translate(-size * 0.5F, -size * 0.5F, 0);
            Matrix4f positionMatrix = matrices.last().pose();
            int light = 0xF000F0;
            Vector4f vector = new Vector4f();
            emitQuad(vertices, positionMatrix, vector, light, 0, 0, size, size);
            matrices.popPose();
        });

        matrices.popPose();
    }

    public static void renderLightsScreen(PoseStack.Pose pose, VertexConsumer vertices, float u, float v, float ticks, float alpha, int color, int number) {
        RANDOM.setSeed(432L);
        float width = 2.5F;
        float rotation = ticks / 200F;
        int light = 0xF000F0;
        Vector4f vector = new Vector4f();

        for (int i = 0; i < number; ++i) {
            float xLogFunc = (((float) i / number * 28493.0f + ticks) / 10F) % 20F;
            if (xLogFunc > 10) {
                xLogFunc = 20 - xLogFunc;
            }

            float lightAlpha = 1F / (1 + (float) java.lang.Math.pow(2.71828f, -0.8F * xLogFunc) * ((1F / 0.01F) - 1));

            if (lightAlpha > 0.01F) {
                Matrix4f m = new Matrix4f(pose.pose());
                m.rotate(new Quaternionf().rotateXYZ(
                        RANDOM.nextFloat() * Mth.TWO_PI,
                        RANDOM.nextFloat() * Mth.TWO_PI,
                        RANDOM.nextFloat() * Mth.TWO_PI
                ));
                m.rotate(new Quaternionf().rotateXYZ(
                        RANDOM.nextFloat() * Mth.TWO_PI,
                        RANDOM.nextFloat() * Mth.TWO_PI,
                        RANDOM.nextFloat() * Mth.TWO_PI + rotation * Mth.HALF_PI * 0.5F
                ));

                float var8 = RANDOM.nextFloat() * 20 + 5;
                float var9 = RANDOM.nextFloat() * 2 + 1;
                int a = Mth.clamp((int) (alpha * lightAlpha * 255), 0, 255);

                vector.set(0, 0, 0, 1);
                m.transform(vector);
                putVertex(vertices, vector, 255, 255, 255, a, 0, 0, light);

                vector.set(-width * var9, var8, -0.5F * var9, 1);
                m.transform(vector);
                putVertex(vertices, vector, 255, 255, 255, 255, 1, 0, light);

                vector.set(width * var9, var8, -0.5F * var9, 1);
                m.transform(vector);
                putVertex(vertices, vector, 255, 255, 255, 255, 0, 1, light);

                vector.set(0, var8, var9, 1);
                m.transform(vector);
                putVertex(vertices, vector, 255, 255, 255, 255, 1, 1, light);

                vector.set(-width * var9, var8, -0.5F * var9, 1);
                m.transform(vector);
                putVertex(vertices, vector, 255, 255, 255, 255, 1, 1, light);
            }
        }
    }

    private static void emitQuad(VertexConsumer vertices, Matrix4f matrix, Vector4f scratch, int light, float x0, float y0, float x1, float y1) {
        putVertex(vertices, matrix, scratch, x0, y0, 0, 255, 255, 255, 255, 0, 0, light);
        putVertex(vertices, matrix, scratch, x1, y0, 0, 255, 255, 255, 255, 1, 0, light);
        putVertex(vertices, matrix, scratch, x1, y1, 0, 255, 255, 255, 255, 1, 1, light);
        putVertex(vertices, matrix, scratch, x0, y1, 0, 255, 255, 255, 255, 0, 1, light);
    }

    private static void putVertex(VertexConsumer vertices, Matrix4f matrix, Vector4f scratch, float lx, float ly, float lz, int r, int g, int b, int a, float u, float v, int light) {
        scratch.set(lx, ly, lz, 1);
        matrix.transform(scratch);
        vertices.addVertex(scratch.x, scratch.y, scratch.z).setColor(r, g, b, a).setUv(u, v).setLight(light);
    }

    private static void putVertex(VertexConsumer vertices, Vector4f pos, int r, int g, int b, int a, float u, float v, int light) {
        vertices.addVertex(pos.x, pos.y, pos.z).setColor(r, g, b, a).setUv(u, v).setLight(light);
    }
}
