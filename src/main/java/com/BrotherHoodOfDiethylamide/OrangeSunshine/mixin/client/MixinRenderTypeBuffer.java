package com.BrotherHoodOfDiethylamide.OrangeSunshine.mixin.client;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.RenderTypeBufferExt;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(IRenderTypeBuffer.Impl.class)
public abstract class MixinRenderTypeBuffer implements RenderTypeBufferExt {

    @Shadow protected Optional<RenderType> lastState;

    @Shadow public abstract void endBatch();

    @Override
    public void flushRenderBuffers() {
        RenderType renderType = null;
        if (lastState.isPresent()) renderType = lastState.get();
        endBatch();

        if (renderType != null) ((IRenderTypeBuffer.Impl)(Object)this).getBuffer(renderType);
    }
}
