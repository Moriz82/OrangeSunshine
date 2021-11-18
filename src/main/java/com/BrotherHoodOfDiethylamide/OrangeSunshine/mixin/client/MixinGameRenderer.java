package com.BrotherHoodOfDiethylamide.OrangeSunshine.mixin.client;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.events.hooks.BobHurtEvent;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(at = @At("HEAD"), method = "bobHurt(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V")
    private void onCamera(MatrixStack matrixStack, float partialTicks, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new BobHurtEvent(matrixStack, partialTicks));
    }
}
