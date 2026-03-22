package moriz.orangesunshine.client.render.shader;

import java.io.IOException;
import java.util.*;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.util.MathUtils;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import moriz.orangesunshine.client.OrangeSunshineClient;
import moriz.orangesunshine.client.render.DrugRenderer;
import moriz.orangesunshine.client.render.GLStateProxy;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import static moriz.orangesunshine.client.render.shader.UniformBinding.UboField;
import static moriz.orangesunshine.client.render.shader.UniformBinding.UboFieldType.*;

public class ShaderLoader implements ResourceManagerReloadListener, IdentifiableResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ShaderLoader POST_EFFECTS = new ShaderLoader(DrugRenderer.INSTANCE.getPostEffects())
            // Add order = Application order!
            .addShader("heat_distortion", UniformBinding.start()
                    .ubo("heat_distortion", "HeatDistortionConfig", List.of(
                            new UboField("PixelSize", VEC2),
                            new UboField("Strength", FLOAT),
                            new UboField("Ticks", FLOAT)
                    ))
                    .program("heat_distortion", (setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        float strength = DrugRenderer.INSTANCE.getEnvironmentalEffects().getHeatDistortion();

                        if (strength <= 0 || !OrangeSunshineClient.getConfig().visual.doHeatDistortion) {
                            return;
                        }

                        setter.set("PixelSize", 1F / screenWidth, 1F / screenHeight);
                        setter.set("Strength", strength);
                        setter.set("Ticks", ShaderContext.ticks() * 0.15f);
                        pass.run();
                    }))
            .addShader("underwater_distortion", UniformBinding.start()
                    .ubo("heat_distortion", "HeatDistortionConfig", List.of(
                            new UboField("PixelSize", VEC2),
                            new UboField("Strength", FLOAT),
                            new UboField("Ticks", FLOAT)
                    ))
                    .program("heat_distortion", (setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        float strength = DrugRenderer.INSTANCE.getEnvironmentalEffects().getWaterDistortion();
                        float peyote = ShaderContext.drug(DrugType.PEYOTE) + ShaderContext.drug(DrugType.LSD);

                        if (peyote <= 0 && (strength <= 0 || !OrangeSunshineClient.getConfig().visual.doWaterDistortion)) {
                            return;
                        }

                        strength = Math.max(peyote * 0.01073F, strength);
                        setter.set("PixelSize", 1F / screenWidth, 1F / screenHeight);
                        setter.set("Strength", strength);
                        setter.set("Ticks", ShaderContext.ticks() * 0.03f);
                        pass.run();
                    }))
            .addShader("simple_effects", UniformBinding.start()
                    .ubo("simple_effects", "SimpleEffectsConfig", List.of(
                            new UboField("Ticks", FLOAT),
                            new UboField("SlowColorRotation", FLOAT),
                            new UboField("QuickColorRotation", FLOAT),
                            new UboField("ColorIntensification", FLOAT),
                            new UboField("Desaturation", FLOAT),
                            new UboField("Inversion", FLOAT)
                    ))
                    .ubo("simple_effects_depth", "SimpleEffectsDepthConfig", List.of(
                            new UboField("Ticks", FLOAT),
                            new UboField("ColorSafeMode", FLOAT),
                            new UboField("_pad0", FLOAT),
                            new UboField("_pad1", FLOAT),
                            new UboField("WorldColorization", VEC4)
                    ))
                    .bind((setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        setter.set("Ticks", ShaderContext.ticks());
                        pass.run();
                    })
                    .program("simple_effects", (setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        var h = ShaderContext.hallucinations();
                        if (setter.setIfNonZero("QuickColorRotation", h.getQuickColorRotation(tickDelta))
                         | setter.setIfNonZero("SlowColorRotation", h.getSlowColorRotation(tickDelta))
                         | setter.setIfNonZero("Desaturation", h.getDesaturation(tickDelta))
                         | setter.setIfNonZero("ColorIntensification", h.getColorIntensification(tickDelta))
                         | setter.setIfNonZero("Inversion", ShaderContext.modifier(Drug.INVERSION_HALLUCINATION_STRENGTH))
                         | h.getPulseColor(tickDelta)[3] > 0
                         | h.getContrastColorization(tickDelta)[3] > 0) {
                            pass.run();
                        }
                    })
                    .program("simple_effects_depth", (setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        var h = ShaderContext.hallucinations();
                        var worldColorization = h.getContrastColorization(tickDelta);
                        if (h.getQuickColorRotation(tickDelta) > 0
                         | h.getSlowColorRotation(tickDelta) > 0
                         | h.getDesaturation(tickDelta) > 0
                         | h.getColorIntensification(tickDelta) > 0
                         | ShaderContext.modifier(Drug.INVERSION_HALLUCINATION_STRENGTH) > 0
                         | worldColorization[3] > 0) {
                            setter.set("ColorSafeMode", GLStateProxy.isColorSafeMode() ? 1 : 0);
                            setter.set("WorldColorization", worldColorization);
                            pass.run();
                        }
                    }))
            .addShader("ps_blur", UniformBinding.start()
                    .ubo("ps_blur", "PsBlurConfig", List.of(
                            new UboField("PixelSize", VEC2),
                            new UboField("HBlur", FLOAT),
                            new UboField("VBlur", FLOAT),
                            new UboField("Repeats", FLOAT),
                            new UboField("_pad0", FLOAT),
                            new UboField("_pad1", FLOAT),
                            new UboField("_pad2", FLOAT)
                    ))
                    .program("ps_blur", (setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        float menuBlur = DrugRenderer.INSTANCE.getMenuBlur() + Math.max(0, ShaderContext.drug(DrugType.SLEEP_DEPRIVATION) - 0.7F) * ShaderContext.tickDelta() * 15;
                        float vBlur = ShaderContext.drug(DrugType.POWER) + menuBlur;
                        float hBlur = menuBlur + (
                              ShaderContext.drug(DrugType.BATH_SALTS) * 6F
                            + ShaderContext.drug(DrugType.BATH_SALTS) * (ShaderContext.ticks() % 5)
                        );

                        if (vBlur <= 0 && hBlur <= 0) {
                            return;
                        }

                        setter.set("PixelSize", 1F / screenWidth, 1F / screenHeight);
                        setter.set("HBlur", hBlur);
                        setter.set("VBlur", vBlur);
                        setter.set("Repeats", (float) Mth.ceil(Math.max(hBlur, vBlur)));
                        pass.run();
                    }))
            .addShader("depth_of_field", UniformBinding.start()
                    .ubo("depth_of_field", "DepthOfFieldConfig", List.of(
                            new UboField("PixelSize", VEC2),
                            new UboField("Vertical", FLOAT),
                            new UboField("FocalPointNear", FLOAT),
                            new UboField("FocalPointFar", FLOAT),
                            new UboField("FocalBlurNear", FLOAT),
                            new UboField("FocalBlurFar", FLOAT),
                            new UboField("_pad0", FLOAT),
                            new UboField("DepthRange", VEC2),
                            new UboField("_pad1", FLOAT),
                            new UboField("_pad2", FLOAT)
                    ))
                    .program("depth_of_field", (setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        var config = OrangeSunshineClient.getConfig().visual;

                        if ((config.dofFocalBlurFar <= 0 && config.dofFocalBlurNear <= 0)
                         || (config.dofFocalPointNear <= 0 && config.dofFocalPointFar >= ShaderContext.viewDistace())) {
                            return;
                        }

                        float zNear = 0.05f;
                        float zFar = ShaderContext.viewDistace();

                        float focalPointNear = config.dofFocalPointNear / zFar;
                        float focalPointFar = config.dofFocalPointFar / zFar;
                        float focalBlurFar = config.dofFocalBlurFar;
                        float focalBlurNear = config.dofFocalBlurNear;

                        setter.set("PixelSize", 1.0f / screenWidth, 1.0f / screenHeight);
                        setter.set("FocalPointNear", focalPointNear);
                        setter.set("FocalPointFar", focalPointFar);
                        setter.set("DepthRange", zNear, zFar);

                        float maxDof = Math.max(focalBlurFar, focalBlurNear);

                        for (int n = 0; n < Mth.ceil(maxDof); n++) {
                            float curBlurNear = Mth.clamp(focalBlurNear - n, 0, 1);
                            float curBlurFar = Mth.clamp(focalBlurFar - n, 0, 1);

                            if (curBlurNear > 0.0f || curBlurFar > 0.0f) {
                                setter.set("FocalBlurNear", curBlurNear);
                                setter.set("FocalBlurFar", curBlurFar);

                                for (int i = 0; i < 2; i++) {
                                    setter.set("Vertical", (float) i);
                                    pass.run();
                                }
                            }
                        }
                    }))
            .addShader("ps_bloom", UniformBinding.start()
                    .ubo("ps_bloom", "PsBloomConfig", List.of(
                            new UboField("PixelSize", VEC2),
                            new UboField("Vertical", FLOAT),
                            new UboField("TotalAlpha", FLOAT)
                    ))
                    .program("ps_bloom", (setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        float bloom = ShaderContext.hallucinations().getBloom(tickDelta);
                        setter.set("PixelSize", 1F / screenWidth * 2F, 1F / screenHeight * 2F);
                        for (int n = 0; n < Mth.ceil(bloom); n++) {
                            setter.set("TotalAlpha", Math.min(1, bloom - n));
                            for (int i = 0; i < 2; i++) {
                                setter.set("Vertical", (float) i);
                                pass.run();
                            }
                        }
                    }))
            .addShader("ps_colored_bloom", UniformBinding.start()
                    .ubo("ps_colored_bloom", "PsColoredBloomConfig", List.of(
                            new UboField("PixelSize", VEC2),
                            new UboField("Vertical", FLOAT),
                            new UboField("TotalAlpha", FLOAT),
                            new UboField("BloomColor", VEC4)
                    ))
                    .program("ps_colored_bloom", (setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        float[] color = ShaderContext.hallucinations().getColorBloom(tickDelta);
                        if (color[3] <= 0) {
                            return;
                        }

                        setter.set("BloomColor", color[0], color[1], color[2], color[3]);
                        setter.set("PixelSize", 1F / screenWidth, 1F / screenHeight);

                        for (int n = 0; n < Mth.ceil(color[3]); n++) {
                            setter.set("TotalAlpha", Math.max(1, color[3] - n));
                            for (int i = 0; i < 2; i++) {
                                setter.set("Vertical", (float) i);
                                pass.run();
                            }
                        }
                    }))
            .addShader("double_vision", UniformBinding.start()
                    .ubo("double_vision", "DoubleVisionConfig", List.of(
                            new UboField("TotalAlpha", FLOAT),
                            new UboField("Distance", FLOAT),
                            new UboField("Stretch", FLOAT),
                            new UboField("_pad0", FLOAT)
                    ))
                    .program("double_vision", (setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        float strength = ShaderContext.modifier(Drug.DOUBLE_VISION);

                        if (strength <= 0 || !OrangeSunshineClient.getConfig().visual.doWaterDistortion) {
                            return;
                        }

                        setter.set("TotalAlpha", strength);
                        setter.set("Distance", Mth.sin(ShaderContext.ticks() / 20F) * 0.05f * strength);
                        setter.set("Stretch", 1 + strength);
                        pass.run();
                    }))
            .addShader("ps_blur_noise", UniformBinding.start()
                    .ubo("ps_blur_noise", "PsBlurNoiseConfig", List.of(
                            new UboField("PixelSize", VEC2),
                            new UboField("TotalAlpha", FLOAT),
                            new UboField("Seed", FLOAT),
                            new UboField("Strength", FLOAT),
                            new UboField("_pad0", FLOAT),
                            new UboField("_pad1", FLOAT),
                            new UboField("_pad2", FLOAT)
                    ))
                    .program("ps_blur_noise", (setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        float strength = ShaderContext.drug(DrugType.POWER) * 0.6F;

                        if (strength <= 0) {
                            return;
                        }

                        setter.set("PixelSize", 1F / screenWidth, 1F / screenHeight);
                        setter.set("Strength", strength);
                        setter.set("Seed", new Random((long) (ShaderContext.ticks() * 1000.0)).nextFloat() * 9 + 1);
                        pass.run();
                    }))
            .addShader("underwater_overlay", UniformBinding.start()
                    .ubo("distortion_map", "DistortionMapConfig", List.of(
                            new UboField("TotalAlpha", FLOAT),
                            new UboField("Strength", FLOAT),
                            new UboField("_pad0", FLOAT),
                            new UboField("_pad1", FLOAT),
                            new UboField("TexTranslation0", VEC2),
                            new UboField("TexTranslation1", VEC2)
                    ))
                    .program("distortion_map", (setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        float strength = DrugRenderer.INSTANCE.getEnvironmentalEffects().getWaterDistortion();

                        if (strength <= 0 || !OrangeSunshineClient.getConfig().visual.waterOverlayEnabled) {
                            return;
                        }

                        setter.set("TotalAlpha", strength);
                        setter.set("Strength", strength * 0.2F);
                        setter.set("TexTranslation0", 0, ShaderContext.ticks() * 0.005F);
                        setter.set("TexTranslation1", 0.5F, ShaderContext.ticks() * 0.007F);
                        pass.run();
                    }))
            .addShader("digital", UniformBinding.start()
                    .ubo("digital_depth", "DigitalConfig", List.of(
                            new UboField("NewResolution", VEC2),
                            new UboField("TextProgress", FLOAT),
                            new UboField("MaxColors", FLOAT),
                            new UboField("Saturation", FLOAT),
                            new UboField("TotalAlpha", FLOAT),
                            new UboField("_pad0", FLOAT),
                            new UboField("_pad1", FLOAT),
                            new UboField("DepthRange", VEC2),
                            new UboField("_pad2", FLOAT),
                            new UboField("_pad3", FLOAT)
                    ))
                    .program("digital_depth", (setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        float digital = ShaderContext.drug(DrugType.ZERO);
                        if (digital <= 0) {
                            return;
                        }

                        float[] maxDownscale = OrangeSunshineClient.getConfig().visual.getDigitalEffectPixelResize();
                        float downscale = MathUtils.mixEaseInOut(0, 0.95F, Math.min(digital * 3, 1));
                        downscale += digital * 0.05f; //Bigger pixels!

                        float textProgress = MathUtils.easeZeroToOne((digital - 0.2F) * 5);
                        float binaryProgress = MathUtils.easeZeroToOne((digital - 0.8F) * 10);

                        setter.set("NewResolution",
                                screenWidth * (1 + (maxDownscale[0] - 1) * downscale),
                                screenHeight * (1 + (maxDownscale[1] - 1) * downscale)
                        );
                        setter.set("TotalAlpha", 1F);
                        setter.set("TextProgress", textProgress + binaryProgress);
                        setter.set("MaxColors", digital > 0.4F ? (Math.max(256F / ((digital - 0.4F) * 640 + 1), 2)) : -1); //Step 3, 0.2 is enough for only 2 colors
                        setter.set("Saturation", 1 - MathUtils.easeZeroToOne((digital - 0.6F) * 5));
                        setter.set("DepthRange", 0.05F, ShaderContext.viewDistace());
                        pass.run();
                    }))
        ;


    private final Minecraft client = Minecraft.getInstance();

    private static final Identifier ID = OrangeSunshine.id("post_effect_shaders");

    private final Map<Identifier, UniformBinding.Set> activeShaderIds = new HashMap<>();

    private final PostEffectRenderer renderer;

    public ShaderLoader(PostEffectRenderer renderer) {
        this.renderer = renderer;
    }

    public ShaderLoader addShader(Identifier id, UniformBinding.Set bindings) {
        activeShaderIds.put(id, bindings);
        return this;
    }

    public ShaderLoader addShader(String id, UniformBinding.Set bindings) {
        return addShader(OrangeSunshine.id("shaders/post/" + id + ".json"), bindings);
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        renderer.onShadersLoaded(List.of());
        renderer.onShadersLoaded(activeShaderIds.entrySet().stream().map(this::loadShader).filter(Objects::nonNull).toList());
    }

    public LoadedShader loadShader(Map.Entry<Identifier, UniformBinding.Set> entry) {
        try {
            return new LoadedShader(client, entry.getKey(), entry.getValue());
        } catch (IOException e) {
            LOGGER.warn("Failed to load shader: {}", entry.getKey(), e);
        }
        return null;
    }
}
