package com.BrotherHoodOfDiethylamide.OrangeSunshine.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

@Pseudo
@Mixin(targets = "net.optifine.render.RenderUtils", remap = false)
public interface InvokerRenderUtilsOF {
    @Invoker
    static boolean callSetFlushRenderBuffers(boolean flushRenderBuffers) {
        return false;
    }

    @Invoker
    static boolean callIsFlushRenderBuffers() {
        return false;
    }

    @Invoker
    static void callFlushRenderBuffers() {

    }
}
