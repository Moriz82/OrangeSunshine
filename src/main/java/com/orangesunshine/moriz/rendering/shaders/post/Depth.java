package com.orangesunshine.moriz.rendering.shaders.post;

import com.google.gson.JsonSyntaxException;
import com.orangesunshine.moriz.OrangeSunshine;
import com.orangesunshine.moriz.rendering.shaders.GlobalUniforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class Depth extends PostShader {
    public Depth() throws IOException, JsonSyntaxException {
        super(new ResourceLocation(OrangeSunshine.MODID, "shaders/post/depth.json"));
    }

    @Override
    public boolean shouldRender() {
        return getDrugEffects().HUE_AMPLITUDE.getValue() > EPSILON;
    }

    @Override
    public void render(float partialTicks) {
        setUniform("Amplitude", getDrugEffects().HUE_AMPLITUDE.getClamped());
        setUniform("TimePassed", GlobalUniforms.timePassed);
        process(partialTicks);
    }
}
