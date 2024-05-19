package com.orangesunshine.moriz.rendering.shaders.post;

import com.google.gson.JsonSyntaxException;
import com.orangesunshine.moriz.OrangeSunshine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class Color extends PostShader {
    public Color() throws IOException, JsonSyntaxException {
        super(new ResourceLocation(OrangeSunshine.MODID, "shaders/post/color.json"));
    }

    @Override
    public boolean shouldRender() {
        return getDrugEffects().SATURATION.getValue() != 0 || getDrugEffects().BRIGHTNESS.getValue() != 0;
    }

    @Override
    public void render(float partialTicks) {
        setUniform("Saturation", Mth.clamp(getDrugEffects().SATURATION.getValue() + 1F, 0F, 3F));
        setUniform("Brightness", getDrugEffects().BRIGHTNESS.getValue());
        process(partialTicks);
    }
}
