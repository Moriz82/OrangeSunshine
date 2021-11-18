package com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.post;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.GlobalUniforms;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class WaterDistort extends PostShader {
    public WaterDistort() throws IOException, JsonSyntaxException {
        super(new ResourceLocation(OrangeSunshine.MOD_ID, "shaders/post/water_distort.json"));
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
