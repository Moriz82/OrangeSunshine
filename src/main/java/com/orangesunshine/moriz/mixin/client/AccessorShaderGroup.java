package com.orangesunshine.moriz.mixin.client;

import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.shaders.Uniform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Uniform.class)
public interface AccessorShaderGroup {
    @Accessor
    List<Shader> getPasses();
}
