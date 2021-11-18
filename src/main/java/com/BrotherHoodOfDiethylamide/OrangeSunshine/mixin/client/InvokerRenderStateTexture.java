package com.BrotherHoodOfDiethylamide.OrangeSunshine.mixin.client;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(RenderState.TextureState.class)
public interface InvokerRenderStateTexture {
    @Invoker
    Optional<ResourceLocation> callTexture();
}
