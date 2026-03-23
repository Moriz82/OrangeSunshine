package moriz.orangesunshine.client.render.shader;

import java.io.IOException;
import java.util.*;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugType;
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
import static moriz.orangesunshine.client.render.shader.UniformBinding.UboFieldType.FLOAT;
import static moriz.orangesunshine.client.render.shader.UniformBinding.UboFieldType.VEC2;
import static moriz.orangesunshine.client.render.shader.UniformBinding.UboFieldType.VEC4;

public class ShaderLoader implements ResourceManagerReloadListener, IdentifiableResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ShaderLoader POST_EFFECTS = new ShaderLoader(DrugRenderer.INSTANCE.getPostEffects())
            .addShader("heat_distortion", UniformBinding.start()
                    .program("heat_distortion", (setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        float strength = DrugRenderer.INSTANCE.getEnvironmentalEffects().getHeatDistortion();

                        if (strength <= 0 || !OrangeSunshineClient.getConfig().visual.doHeatDistortion) {
                            return;
                        }

                        setter.set("PixelSize", 1F / screenWidth, 1F / screenHeight);
                        setter.set("Strength", strength);
                        setter.set("Ticks", ShaderContext.ticks() * 0.15f);
                        pass.run();
                    })
                    .ubo("heat_distortion", "HeatDistortionConfig", List.of(
                            new UboField("PixelSize", VEC2),
                            new UboField("Strength", FLOAT),
                            new UboField("Ticks", FLOAT))))
            .addShader("underwater_distortion", UniformBinding.start()
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
                    })
                    .ubo("heat_distortion", "HeatDistortionConfig", List.of(
                            new UboField("PixelSize", VEC2),
                            new UboField("Strength", FLOAT),
                            new UboField("Ticks", FLOAT))))
            .addShader("simple_effects", UniformBinding.start()
                    .program("simple_effects", (setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        var h = ShaderContext.hallucinations();
                        if (setter.setIfNonZero("QuickColorRotation", h.getQuickColorRotation(tickDelta))
                         | setter.setIfNonZero("SlowColorRotation", h.getSlowColorRotation(tickDelta))
                         | setter.setIfNonZero("Desaturation", h.getDesaturation(tickDelta))
                         | setter.setIfNonZero("ColorIntensification", h.getColorIntensification(tickDelta))
                         | setter.setIfNonZero("Inversion", ShaderContext.modifier(Drug.INVERSION_HALLUCINATION_STRENGTH))
                         | h.getPulseColor(tickDelta)[3] > 0
                         | h.getContrastColorization(tickDelta)[3] > 0) {
                            setter.set("Ticks", ShaderContext.ticks());
                            pass.run();
                        }
                    })
                    .ubo("simple_effects", "SimpleEffectsConfig", List.of(
                            new UboField("Ticks", FLOAT),
                            new UboField("SlowColorRotation", FLOAT),
                            new UboField("QuickColorRotation", FLOAT),
                            new UboField("ColorIntensification", FLOAT),
                            new UboField("Desaturation", FLOAT),
                            new UboField("Inversion", FLOAT)))
                    .program("simple_effects_depth", (setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        var h = ShaderContext.hallucinations();
                        var worldColorization = h.getContrastColorization(tickDelta);
                        if (h.getQuickColorRotation(tickDelta) > 0
                         | h.getSlowColorRotation(tickDelta) > 0
                         | h.getDesaturation(tickDelta) > 0
                         | h.getColorIntensification(tickDelta) > 0
                         | ShaderContext.modifier(Drug.INVERSION_HALLUCINATION_STRENGTH) > 0
                         | worldColorization[3] > 0) {
                            setter.set("Ticks", ShaderContext.ticks());
                            setter.set("ColorSafeMode", GLStateProxy.isColorSafeMode() ? 1 : 0);
                            setter.set("WorldColorization", worldColorization);
                            pass.run();
                        }
                    })
                    .ubo("simple_effects_depth", "SimpleEffectsDepthConfig", List.of(
                            new UboField("Ticks", FLOAT),
                            new UboField("ColorSafeMode", FLOAT),
                            new UboField("WorldColorization", VEC4))))
            .addShader("ps_blur", UniformBinding.start()
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
                        setter.set("Repeats", Mth.ceil(Math.max(hBlur, vBlur)));
                        pass.run();
                    })
                    .ubo("ps_blur", "PsBlurConfig", List.of(
                            new UboField("PixelSize", VEC2),
                            new UboField("HBlur", FLOAT),
                            new UboField("VBlur", FLOAT),
                            new UboField("Repeats", FLOAT),
                            new UboField("_pad0", FLOAT),
                            new UboField("_pad1", FLOAT),
                            new UboField("_pad2", FLOAT))))
            .addShader("depth_of_field", UniformBinding.start()
                    .program("depth_of_field", (setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        var config = OrangeSunshineClient.getConfig().visual;

                        if ((config.dofFocalBlurFar <= 0 && config.dofFocalBlurNear <= 0)
                         || (config.dofFocalPointNear <= 0 && config.dofFocalPointFar >= ShaderContext.viewDistace())) {
                            return;
                        }

                        float zNear = 0.05f;
                        float zFar = ShaderContext.viewDistace();

                        setter.set("PixelSize", 1F / screenWidth, 1F / screenHeight);
                        setter.set("Vertical", 0);
                        setter.set("FocalPointNear", config.dofFocalPointNear);
                        setter.set("FocalPointFar", config.dofFocalPointFar);
                        setter.set("FocalBlurNear", config.dofFocalBlurNear);
                        setter.set("FocalBlurFar", config.dofFocalBlurFar);
                        setter.set("DepthRange", zNear, zFar);
                        pass.run();
                    })
                    .ubo("depth_of_field", "DepthOfFieldConfig", List.of(
                            new UboField("PixelSize", VEC2),
                            new UboField("Vertical", FLOAT),
                            new UboField("FocalPointNear", FLOAT),
                            new UboField("FocalPointFar", FLOAT),
                            new UboField("FocalBlurNear", FLOAT),
                            new UboField("FocalBlurFar", FLOAT),
                            new UboField("DepthRange", VEC2))))
            .addShader("double_vision", UniformBinding.start()
                    .program("double_vision", (setter, tickDelta, screenWidth, screenHeight, pass) -> {
                        float doubleVision = ShaderContext.modifier(Drug.DOUBLE_VISION);
                        if (doubleVision <= 0) {
                            return;
                        }
                        float tick = ShaderContext.ticks();
                        setter.set("TotalAlpha", Mth.clamp(doubleVision, 0, 1));
                        setter.set("Distance", (float) Math.sin(tick / 40F) * 0.01F * doubleVision);
                        setter.set("Stretch", 1F + doubleVision * 0.1F);
                        pass.run();
                    })
                    .ubo("double_vision", "DoubleVisionConfig", List.of(
                            new UboField("TotalAlpha", FLOAT),
                            new UboField("Distance", FLOAT),
                            new UboField("Stretch", FLOAT),
                            new UboField("_pad0", FLOAT))))
        ;


    private final Minecraft client = Minecraft.getInstance();

    private static final Identifier ID = OrangeSunshine.id("post_effect_shaders");

    private final Map<Identifier, UniformBinding.Set> activeShaderIds = new LinkedHashMap<>();

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
        List<LoadedShader> loaded = new ArrayList<>();
        for (var entry : activeShaderIds.entrySet()) {
            LoadedShader shader = loadShader(entry);
            if (shader != null) {
                loaded.add(shader);
                LOGGER.info("[OrangeSunshine] Loaded post shader: {} ({} passes)", entry.getKey(), shader.getPassCount());
            }
        }
        renderer.onShadersLoaded(loaded);
        LOGGER.info("[OrangeSunshine] Post-effect shader pipeline: {} shaders loaded", loaded.size());
    }

    public LoadedShader loadShader(Map.Entry<Identifier, UniformBinding.Set> entry) {
        try {
            return new LoadedShader(client, entry.getKey(), entry.getValue());
        } catch (IOException e) {
            LOGGER.warn("[OrangeSunshine] Failed to load shader: {}", entry.getKey(), e);
        }
        return null;
    }
}
