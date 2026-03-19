/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render.effect;

import com.mojang.blaze3d.systems.RenderSystem;

import moriz.orangesunshine.PSSounds;
import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.client.render.RenderUtil;
import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/**
 * @author Sollace
 * @since 19 April 2023
 */
public class TirednessScreenEffect implements ScreenEffect {
    private static final Identifier EYELID_OVERLAY = OrangeSunshine.id("textures/environment/eyelid_overlay.png");

    private float prevOverlayOpacity;
    private float overlayOpacity;

    private int ticksBlinking;

    @Override
    public void update(float tickDelta) {

        PlayerEntity entity = MinecraftClient.getInstance().player;
        DrugProperties properties = DrugProperties.of(entity);

        prevOverlayOpacity = overlayOpacity;

        float drowsyness = Math.max(0, properties.getModifier(Drug.DROWSYNESS) - 0.6F);

        overlayOpacity = MathUtils.approach(overlayOpacity, ticksBlinking > 0 ? drowsyness * 0.9F + Mth.sin(entity.age / 10F) : 0, 0.03F);
        if (drowsyness > 0.3F && overlayOpacity > 0.6F) {
            entity.getWorld().playSound(entity.getX(), entity.getY(), entity.getZ(),
                PSSounds.ENTITY_PLAYER_HEARTBEAT,
                SoundCategory.AMBIENT, drowsyness, 0.3F, false);
        }

        if (drowsyness < 0.2F) {
            ticksBlinking = Math.max(ticksBlinking - 1, 0);
            return;
        }

        if (--ticksBlinking <= 0 && (ticksBlinking < -300 || entity.getWorld().random.nextFloat() < properties.getModifier(Drug.DROWSYNESS))) {
            ticksBlinking = (int)entity.getWorld().random.nextTriangular(300, 200);
            entity.sendMessage(Component.literal("...I should really sleep..."), true);
        }
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertices, int screenWidth, int screenHeight, float ticks, PingPong pingPong) {
        matrices.push();
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();

        float opacity = Mth.lerp(ticks, prevOverlayOpacity, overlayOpacity);

        if (opacity > 0) {
            RenderUtil.drawOverlay(matrices, opacity * 0.8F, screenWidth, screenHeight, EYELID_OVERLAY, 0, 0, 1, 1, (int)(opacity * 5.8F));
        }

        RenderSystem.enableDepthTest();
        matrices.pop();
    }

    @Override
    public void close() {

    }
}
