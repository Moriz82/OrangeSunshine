package com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.post;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.GlobalUniforms;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class Recursion extends PostShader {
    public Recursion() throws IOException, JsonSyntaxException {
        super(new ResourceLocation(OrangeSunshine.MOD_ID, "shaders/post/bumpy.json"));
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
