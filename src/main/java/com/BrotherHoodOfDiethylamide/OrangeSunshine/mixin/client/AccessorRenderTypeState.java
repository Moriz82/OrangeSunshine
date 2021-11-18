package com.BrotherHoodOfDiethylamide.OrangeSunshine.mixin.client;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderType.State.class)
public interface AccessorRenderTypeState {
    @Accessor
    RenderState.TextureState getTextureState();
}
