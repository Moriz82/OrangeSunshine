package moriz.orangesunshine.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class SmoothCameraHelper {
    private static final Identifier SMOOTH_CAM_SHADER = OrangeSunshine.id("shaders/post/smooth_cam.json");

    private static float shakeX;
    private static float shakeY;
    private static float shakeZ;

    private static float prevShakeX;
    private static float prevShakeY;
    private static float prevShakeZ;

    private static float shakeScale = 0.01f;

    public static void updateShake(float tickDelta) {
        prevShakeX = shakeX;
        prevShakeY = shakeY;
        prevShakeZ = shakeZ;

        shakeX = shakeY = shakeZ = 0;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        DrugProperties properties = DrugProperties.of(mc.player);
        if (properties == null) return;

        float strength = properties.getModifier(Drug.VIEW_WOBBLYNESS);
        float tick = mc.player.tickCount + tickDelta;

        shakeX = Mth.sin(tick / 150 * (float) Math.PI) * strength;
        shakeY = Mth.sin(tick / 170 * (float) Math.PI) * strength;
        shakeZ = Mth.sin(tick / 190 * (float) Math.PI) * strength;
    }

    public static void applyCameraShake(PoseStack matrices, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        DrugProperties properties = DrugProperties.of(mc.player);
        if (properties == null) return;

        float strength = properties.getModifier(Drug.VIEW_WOBBLYNESS);
        if (strength <= 0) return;

        float currentShakeX = Mth.lerp(tickDelta, prevShakeX, shakeX) * shakeScale;
        float currentShakeY = Mth.lerp(tickDelta, prevShakeY, shakeY) * shakeScale;
        float currentShakeZ = Mth.lerp(tickDelta, prevShakeZ, shakeZ) * shakeScale;

        matrices.translate(currentShakeX, currentShakeY, currentShakeZ);
    }
}
