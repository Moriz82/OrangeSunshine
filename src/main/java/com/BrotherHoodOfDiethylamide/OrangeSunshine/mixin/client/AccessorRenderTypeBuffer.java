package com.BrotherHoodOfDiethylamide.OrangeSunshine.mixin.client;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(IRenderTypeBuffer.Impl.class)
public interface AccessorRenderTypeBuffer {
    @Accessor
    Optional<RenderType> getLastState();
}
