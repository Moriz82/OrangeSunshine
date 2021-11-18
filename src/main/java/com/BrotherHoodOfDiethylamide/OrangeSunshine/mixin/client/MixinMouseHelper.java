package com.BrotherHoodOfDiethylamide.OrangeSunshine.mixin.client;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.MouseSmootherEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHelper.class)
public class MixinMouseHelper {
    @Shadow private double accumulatedDX;
    @Shadow private double accumulatedDY;

    @Inject(at = @At("HEAD"), method = "turnPlayer()V")
    private void onTurn(CallbackInfo ci) {
        if (MouseSmootherEffect.INSTANCE.getAmplifier() > 0) {
            MouseSmootherEffect.INSTANCE.tick(accumulatedDX, accumulatedDY);
        } else {
            MouseSmootherEffect.INSTANCE.reset();
        }
    }

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHelper;accumulatedDX:D", ordinal = 1), method = "turnPlayer()V")
    private double getAccumulatedDX(MouseHelper mouseHelper) {
        if (MouseSmootherEffect.INSTANCE.getAmplifier() > 0) {
            double d4 = Minecraft.getInstance().options.sensitivity * 0.6D + 0.2D;
            double d5 = d4 * d4 * d4 * 8.0D;
            return MouseSmootherEffect.INSTANCE.getX()/d5;
        } else {
            return accumulatedDX;
        }
    }

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHelper;accumulatedDY:D", ordinal = 1), method = "turnPlayer()V")
    private double getAccumulatedDY(MouseHelper mouseHelper) {
        if (MouseSmootherEffect.INSTANCE.getAmplifier() > 0) {
            double d4 = Minecraft.getInstance().options.sensitivity * 0.6D + 0.2D;
            double d5 = d4 * d4 * d4 * 8.0D;
            return MouseSmootherEffect.INSTANCE.getY()/d5;
        } else {
            return accumulatedDY;
        }
    }
}
