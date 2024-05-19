package com.orangesunshine.moriz.rendering.shaders.post;

import com.google.gson.JsonSyntaxException;
import com.orangesunshine.moriz.OrangeSunshine;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class Bumpy extends PostShader {
    public Bumpy() throws IOException, JsonSyntaxException {
        super(new ResourceLocation(OrangeSunshine.MODID, "shaders/post/bumpy.json"));
    }

    @Override
    public boolean shouldRender() {
        return getDrugEffects().BUMPY.getValue() > EPSILON;
    }


}
