package moriz.orangesunshine.client;

import java.util.Optional;
import java.util.function.Supplier;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.client.item.PSModelPredicates;
import moriz.orangesunshine.client.render.shader.ShaderLoader;
import moriz.orangesunshine.client.screen.PSScreens;
import moriz.orangesunshine.config.JsonConfig;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.client.render.DrugRenderer;
import moriz.orangesunshine.client.render.PSRenderers;
import moriz.orangesunshine.client.render.SmoothCameraHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceType;

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
        OrangeSunshine.globalDrugProperties = () -> DrugProperties.of((Entity)MinecraftClient.getInstance().player);
        OrangeSunshine.crossHairTarget = () -> Optional.ofNullable(MinecraftClient.getInstance().crosshairTarget);
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            DrugProperties.of((Entity)client.player).ifPresent(properties -> {
                DrugRenderer.INSTANCE.update(properties, client.player);
                SmoothCameraHelper.INSTANCE.tick(properties);
            });
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            DrugProperties.of((Entity)client.player).ifPresent(properties -> {
                DrugRenderer.INSTANCE.renderAllHallucinations(context.matrixStack(), context.consumers(), context.camera(), context.tickDelta(), properties);
            });
        });

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ShaderLoader.POST_EFFECTS);

        PSRenderers.bootstrap();
        PSModelPredicates.bootstrap();
        PSScreens.bootstrap();
    }
}
