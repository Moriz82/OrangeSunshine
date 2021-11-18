package com.BrotherHoodOfDiethylamide.OrangeSunshine.mixin;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.events.hooks.IncreaseAirSupplyEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
    @Inject(at = @At("HEAD"), method = "increaseAirSupply", cancellable = true)
    private void increaseAirSupply(int airSupply, CallbackInfoReturnable<Integer> cir) {
        if (MinecraftForge.EVENT_BUS.post(new IncreaseAirSupplyEvent( (LivingEntity)(Object)this ))) {
            cir.setReturnValue(airSupply);
        }
    }
}
