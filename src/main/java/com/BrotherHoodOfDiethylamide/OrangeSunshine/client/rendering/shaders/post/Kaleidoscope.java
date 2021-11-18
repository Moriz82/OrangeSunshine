package com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.post;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.GlobalUniforms;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class Kaleidoscope extends PostShader {
    public Kaleidoscope() throws IOException, JsonSyntaxException {
        super(new ResourceLocation(OrangeSunshine.MOD_ID, "shaders/post/kaleidoscope.json"));
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
