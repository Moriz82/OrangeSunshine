package com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.post;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class Color extends PostShader {
    public Color() throws IOException, JsonSyntaxException {
        super(new ResourceLocation(OrangeSunshine.MOD_ID, "shaders/post/color.json"));
    }

    @Override
    public boolean shouldRender() {
        return getDrugEffects().SATURATION.getValue() != 0 || getDrugEffects().BRIGHTNESS.getValue() != 0;
    }

    @Override
    public void render(float partialTicks) {
        setUniform("Saturation", MathHelper.clamp(getDrugEffects().SATURATION.getValue() + 1F, 0F, 3F));
        setUniform("Brightness", getDrugEffects().BRIGHTNESS.getValue());
        process(partialTicks);
    }
}
