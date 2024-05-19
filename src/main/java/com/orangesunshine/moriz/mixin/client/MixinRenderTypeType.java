package com.orangesunshine.moriz.mixin.client;

import com.orangesunshine.moriz.rendering.shaders.RenderTypeTypeExt;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.client.renderer.RenderType$Type")
public class MixinRenderTypeType implements RenderTypeTypeExt {
    @Shadow @Final
    private RenderType.CompositeState state;

    @Override
    public RenderType.CompositeState getState() {
        return state;
    }
}
