package com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.post;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class Bloom extends PostShader {
    public Bloom() throws IOException, JsonSyntaxException {
        super(new ResourceLocation(OrangeSunshine.MOD_ID, "shaders/post/bloom.json"));
    }

    @Override
    public boolean shouldRender() {
        return getDrugEffects().BLOOM_RADIUS.getValue() > EPSILON;
    }

    @Override
    public void render(float partialTicks) {
        setUniform("Radius", getDrugEffects().BLOOM_RADIUS.getValue());
        setUniform("Threshold", 1F - getDrugEffects().BLOOM_THRESHOLD.getValue());
        process(partialTicks);
    }
}
