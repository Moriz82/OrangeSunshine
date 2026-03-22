/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.client.OrangeSunshineClient;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.client.render.effect.*;
import moriz.orangesunshine.client.render.shader.PostEffectRenderer;
import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.hallucination.DriftingCamera;
import moriz.orangesunshine.entity.drug.hallucination.Hallucination;
import moriz.orangesunshine.entity.drug.hallucination.HallucinationManager;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Quaternionf;

import java.util.Optional;

/**
 * Handles client-side drug visual effects (screen distortion, hand tremor, model posing).
 * Heavier post-processing / overlay effects live in effect/ and shader/ and are wired
 * up separately once those subsystems are fully ported.
 */
public class DrugRenderer {
    public static final DrugRenderer INSTANCE = new DrugRenderer();

    private final EnvironmentalScreenEffect environmentalEffects = new EnvironmentalScreenEffect();
    private final ScreenEffect screenEffects = CompoundScreenEffect.of(
            new LensFlareScreenEffect(),
            new WarmthOverlayScreenEffect(),
            new AlcoholOverlayScreenEffect(),
            new PowerOverlayScreenEffect(),
            environmentalEffects,
            new TirednessScreenEffect(),
            new MotionBlurScreenEffect()
    );

    private final PostEffectRenderer postEffects = new PostEffectRenderer();

    private float screenBackgroundBlur;

    public ScreenEffect getScreenEffects() {
        return screenEffects;
    }

    public PostEffectRenderer getPostEffects() {
        return postEffects;
    }

    public EnvironmentalScreenEffect getEnvironmentalEffects() {
        return environmentalEffects;
    }

    public float getMenuBlur() {
        return OrangeSunshineClient.getConfig().visual.pauseMenuBlur * screenBackgroundBlur * screenBackgroundBlur * screenBackgroundBlur;
    }

    public void update(DrugProperties drugProperties, LivingEntity entity) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.isPaused()) {
            screenBackgroundBlur = Math.min(1, screenBackgroundBlur + 0.25F);
        } else {
            screenBackgroundBlur = Math.max(0, screenBackgroundBlur - 0.25F);
        }

        screenEffects.update(mc.getDeltaTracker().getGameTimeDeltaTicks());

        if (OrangeSunshineClient.getConfig().visual.visualDebugLogging
                && entity.tickCount % 40 == 0
                && entity.level().isClientSide()) {
            float max = 0;
            DrugType maxType = DrugType.ALCOHOL;
            java.util.StringJoiner allDrugs = new java.util.StringJoiner(", ");
            for (DrugType t : DrugType.REGISTRY) {
                float v = drugProperties.getDrugValue(t);
                if (v > 0.001f) {
                    allDrugs.add(t.id().getPath() + "=" + String.format("%.3f", v));
                }
                if (v > max) {
                    max = v;
                    maxType = t;
                }
            }
            OrangeSunshine.LOGGER.info(
                    "[OrangeSunshine] drug client tick — max={} ({}) wobble={} tremble={} | all=[{}]",
                    String.format("%.3f", max),
                    maxType.id().getPath(),
                    String.format("%.3f", drugProperties.getModifier(Drug.VIEW_WOBBLYNESS)),
                    String.format("%.3f", drugProperties.getModifier(Drug.VIEW_TREMBLE_STRENGTH)),
                    allDrugs
            );
        }
    }

    // ── screen / camera distortion ────────────────────────────────────────────

    public void distortScreen(PoseStack matrices, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        DrugProperties properties = DrugProperties.of(mc.player);
        if (properties == null) return;

        float wobblyness = Math.min(1, properties.getModifier(Drug.VIEW_WOBBLYNESS));
        float tick = mc.player.tickCount + tickDelta;

        Camera camera = mc.gameRenderer.getMainCamera();
        DriftingCamera driftingCam = properties.getHallucinations().getCamera();

        matrices.mulPose(Axis.XP.rotationDegrees(camera.xRot()));
        matrices.mulPose(Axis.YP.rotationDegrees(camera.yRot() + 180.0f));

        Vec3 cameraOffset = driftingCam.getPosition();
        Vec3 prevCameraOffset = driftingCam.getPrevPosition();
        matrices.translate(
            Mth.lerp(tickDelta, (float) prevCameraOffset.x, (float) cameraOffset.x),
            Mth.lerp(tickDelta, (float) prevCameraOffset.y, (float) cameraOffset.y),
            Mth.lerp(tickDelta, (float) prevCameraOffset.z, (float) cameraOffset.z)
        );

        Vec3 cameraRoll = driftingCam.getRotation();
        Vec3 prevCameraRoll = driftingCam.getPrevRotation();
        matrices.mulPose(new Quaternionf().rotateXYZ(
            (float) Mth.lerp(tickDelta, prevCameraRoll.x, cameraRoll.x),
            (float) Mth.lerp(tickDelta, prevCameraRoll.y, cameraRoll.y),
            (float) Mth.lerp(tickDelta, prevCameraRoll.z, cameraRoll.z)
        ));

        matrices.mulPose(Axis.YN.rotationDegrees(camera.yRot() + 180.0f));
        matrices.mulPose(Axis.XN.rotationDegrees(camera.xRot()));

        if (wobblyness > 0) {
            float f4 = Mth.square(5F / (wobblyness * wobblyness + 5F) - wobblyness * 0.04F);
            float sin1 = Mth.sin(tick / 150 * (float) Math.PI);
            float sin2 = Mth.sin(tick / 170 * (float) Math.PI);
            float sin3 = Mth.sin(tick / 190 * (float) Math.PI);

            float yz = tick * 3F * (float) (Math.PI / 180);
            Quaternionf rotation = new Quaternionf().rotateXYZ(0, yz, yz);
            matrices.mulPose(rotation);
            matrices.scale(
                1F / (f4 + (wobblyness * sin1) / 2),
                1F / (f4 + (wobblyness * sin2) / 2),
                1F / (f4 + (wobblyness * sin3) / 2)
            );
            matrices.mulPose(rotation.invert());
        }

        matrices.translate(
            DrugEffectInterpreter.getCameraShiftX(properties, tick),
            DrugEffectInterpreter.getCameraShiftY(properties, tick),
            0
        );
    }

    // ── held-item / hand distortion ──────────────────────────────────────────

    public void distortHand(PoseStack matrices, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        DrugProperties properties = DrugProperties.of(mc.player);
        if (properties == null) return;

        float ticks = mc.player.tickCount + tickDelta;
        matrices.translate(
            DrugEffectInterpreter.getHandShiftX(properties, ticks),
            DrugEffectInterpreter.getHandShiftY(properties, ticks),
            0
        );
    }

    // ── entity model posing ──────────────────────────────────────────────────

    public void poseModel(Player player, HumanoidModel<?> model, float tickDelta) {
        DrugProperties properties = DrugProperties.of(player);
        if (properties == null) return;

        float tick = player.tickCount + tickDelta;

        ModelPart head     = model.head;
        ModelPart leftArm  = model.leftArm;
        ModelPart rightArm = model.rightArm;

        float shiftX = DrugEffectInterpreter.getHandShiftX(properties, tick) * 2;
        leftArm.xRot  += shiftX;
        rightArm.xRot -= shiftX;

        float shiftY = DrugEffectInterpreter.getHandShiftY(properties, tick);
        leftArm.zRot  += shiftY;
        rightArm.zRot -= shiftY;

        head.xRot += DrugEffectInterpreter.getCameraShiftX(properties, tick);
        head.yRot += DrugEffectInterpreter.getCameraShiftY(properties, tick);
        head.zRot  = DrugEffectInterpreter.getAlcohol(properties);

        if (model instanceof PlayerModel pem) {
            pem.hat.loadPose(head.storePose());
            pem.leftSleeve.loadPose(leftArm.storePose());
            pem.rightSleeve.loadPose(rightArm.storePose());
        }
    }

    public void onRenderOverlay(GuiGraphics context, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        getScreenEffects().render(context,
                mc.renderBuffers().bufferSource(),
                width, height, tickDelta, null);

        postEffects.render(tickDelta);
    }

    // ── hallucination rendering ───────────────────────────────────────────────

    public void renderAllHallucinations(PoseStack matrices, MultiBufferSource vertices, Camera camera, float tickDelta, DrugProperties drugProperties) {
        HallucinationManager hallucinations = drugProperties.getHallucinations();
        float alpha = Mth.clamp(hallucinations.getHallucinationStrength(tickDelta) * 15, 0, 1);
        float forcedAlpha = hallucinations.getEntities().getForcedAlpha(tickDelta);
        if (forcedAlpha > 0) {
            alpha += forcedAlpha;
            alpha /= 2F;
        }

        for (Hallucination h : hallucinations.getEntities()) {
            try {
                h.render(matrices, vertices, camera, tickDelta, alpha);
            } catch (Throwable t) {
                OrangeSunshine.LOGGER.fatal("Exception whilst rendering hallucination", t);
                h.setDead();
                while (!matrices.isEmpty()) {
                    matrices.popPose();
                }
            }
        }
    }
}
