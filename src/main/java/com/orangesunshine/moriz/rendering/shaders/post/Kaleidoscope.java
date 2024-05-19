package com.orangesunshine.moriz.rendering.shaders.post;

import com.google.gson.JsonSyntaxException;
import com.orangesunshine.moriz.OrangeSunshine;
import com.orangesunshine.moriz.rendering.shaders.GlobalUniforms;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class Kaleidoscope extends PostShader {
    public Kaleidoscope() throws IOException, JsonSyntaxException {
        super(new ResourceLocation(OrangeSunshine.MODID, "shaders/post/kaleidoscope.json"));
    }

    @Override
    public boolean shouldRender() {
        return getDrugEffects().KALEIDOSCOPE_INTENSITY.getValue() > EPSILON;
    }

    @Override
    public void render(float partialTicks) {
        setUniform("Extend", getDrugEffects().KALEIDOSCOPE_INTENSITY.getValue());
        setUniform("TimePassed", GlobalUniforms.timePassed);
        setUniform("TimePassedSin", GlobalUniforms.timePassedSin);
        process(partialTicks);
    }
}
