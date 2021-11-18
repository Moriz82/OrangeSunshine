package com.BrotherHoodOfDiethylamide.OrangeSunshine.mixin.client;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.RenderTypeTypeExt;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.client.renderer.RenderType$Type")
public class MixinRenderTypeType implements RenderTypeTypeExt {
    @Shadow @Final
    private RenderType.State state;

    @Override
    public RenderType.State getState() {
        return state;
    }
}
