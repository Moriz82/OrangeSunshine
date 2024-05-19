package com.orangesunshine.moriz.rendering.shaders.post;

import com.google.gson.JsonSyntaxException;
import com.orangesunshine.moriz.OrangeSunshine;
import com.orangesunshine.moriz.rendering.shaders.GlobalUniforms;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class Recursion extends PostShader {
    public Recursion() throws IOException, JsonSyntaxException {
        super(new ResourceLocation(OrangeSunshine.MODID, "shaders/post/bumpy.json"));
    }

    @Override
    public boolean shouldRender() {
        return getDrugEffects().BUMPY.getValue() > EPSILON;
    }

    @Override
    public void render(float partialTicks) {
        setUniform("Intensity", getDrugEffects().BUMPY.getValue());
        setUniform("TimePassed", GlobalUniforms.timePassed);
        process(partialTicks);
    }
}
