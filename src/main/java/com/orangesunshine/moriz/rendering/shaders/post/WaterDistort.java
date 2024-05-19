package com.orangesunshine.moriz.rendering.shaders.post;

import com.google.gson.JsonSyntaxException;
import com.orangesunshine.moriz.OrangeSunshine;
import com.orangesunshine.moriz.rendering.shaders.GlobalUniforms;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class WaterDistort extends PostShader {
    public WaterDistort() throws IOException, JsonSyntaxException {
        super(new ResourceLocation(OrangeSunshine.MODID, "shaders/post/water_distort.json"));
    }

    @Override
    public boolean shouldRender() {
        return getDrugEffects().WATER_DISTORT.getValue() > EPSILON;
    }

    @Override
    public void render(float partialTicks) {
        setUniform("Influence", getDrugEffects().WATER_DISTORT.getValue());
        setUniform("TimePassed", GlobalUniforms.timePassed);
        process(partialTicks);
    }
}
