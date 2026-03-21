package moriz.orangesunshine.client;

import java.util.Optional;
import java.util.function.Supplier;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.client.item.PSModelPredicates;
import moriz.orangesunshine.client.particle.PSParticleFactories;
import moriz.orangesunshine.client.screen.PSScreens;
import moriz.orangesunshine.client.render.shader.PSShaders;
import moriz.orangesunshine.client.render.shader.ShaderLoader;
import moriz.orangesunshine.config.JsonConfig;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.client.render.PSRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.Entity;

/**
 * @author Sollace
 * @since 1 Jan 2023
 */
public class OrangeSunshineClient implements ClientModInitializer {
    private static final Supplier<JsonConfig.Loader<PSClientConfig>> CONFIG_LOADER = JsonConfig.create("orangesunshine_client.json", PSClientConfig::new);

    public static JsonConfig.Loader<PSClientConfig> getConfigLoader() {
        return CONFIG_LOADER.get();
    }

    public static PSClientConfig getConfig() {
        return getConfigLoader().getData();
    }

    @Override
    public void onInitializeClient() {
        OrangeSunshine.globalDrugProperties = () -> DrugProperties.of((Entity) Minecraft.getInstance().player);
        OrangeSunshine.crossHairTarget = () -> Optional.ofNullable(Minecraft.getInstance().hitResult);
        PSRenderers.bootstrap();
        PSParticleFactories.bootstrap();
        PSModelPredicates.bootstrap();
        PSScreens.bootstrap();
        PSShaders.bootstrap();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(ShaderLoader.POST_EFFECTS);
    }
}
