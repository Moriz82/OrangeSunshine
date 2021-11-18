package com.BrotherHoodOfDiethylamide.OrangeSunshine.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

@Pseudo
@Mixin(targets = "net.optifine.Config", remap = false)
public interface InvokerConfigOF {
    @Invoker
    static boolean callIsShaders() { return false; };
}
