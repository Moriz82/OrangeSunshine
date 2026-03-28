package moriz.orangesunshine.client;

import java.util.Optional;
import java.util.function.Supplier;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.client.item.PSModelPredicates;
import moriz.orangesunshine.client.particle.PSParticleFactories;
import moriz.orangesunshine.client.screen.PSScreens;
import moriz.orangesunshine.client.render.shader.PSShaders;
import moriz.orangesunshine.client.render.shader.ShaderLoader;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.client.render.DebugOverlay;
import moriz.orangesunshine.client.render.PSRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.Entity;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/**
 * @author Sollace
 * @since 1 Jan 2023
 */
public class OrangeSunshineClient implements ClientModInitializer {
    private static final Supplier<moriz.orangesunshine.config.JsonConfig.Loader<PSClientConfig>> CONFIG_LOADER =
            moriz.orangesunshine.config.JsonConfig.create("orangesunshine_client.json", PSClientConfig::new);

    private static KeyMapping debugMenuKey;

    public static moriz.orangesunshine.config.JsonConfig.Loader<PSClientConfig> getConfigLoader() {
        return CONFIG_LOADER.get();
    }

    public static PSClientConfig getConfig() {
        return getConfigLoader().getData();
    }

    @Override
    public void onInitializeClient() {
        // Wire the platform-agnostic config accessor to the Fabric config loader
        PSClientConfig.setConfigSupplier(OrangeSunshineClient::getConfig);
        OrangeSunshine.globalDrugProperties = () -> DrugProperties.of((Entity) Minecraft.getInstance().player);
        OrangeSunshine.crossHairTarget = () -> Optional.ofNullable(Minecraft.getInstance().hitResult);
        PSRenderers.bootstrap();
        OrangeSunshine.LOGGER.info("[OrangeSunshine] RENDERERS_OK");
        PSParticleFactories.bootstrap();
        OrangeSunshine.LOGGER.info("[OrangeSunshine] PARTICLES_OK");
        PSModelPredicates.bootstrap();
        PSScreens.bootstrap();
        PSShaders.bootstrap();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(ShaderLoader.POST_EFFECTS);
        OrangeSunshine.LOGGER.info("[OrangeSunshine] SHADERS_OK");

        debugMenuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.orangesunshine.debug",
                GLFW.GLFW_KEY_F7,
                KeyMapping.Category.register(OrangeSunshine.id("orangesunshine"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (debugMenuKey.consumeClick()) {
                DebugOverlay.isEnabled = !DebugOverlay.isEnabled;
            }
        });

        OrangeSunshine.LOGGER.info("[OrangeSunshine] CLIENT_SMOKE_OK — client bootstrap finished (rendering, particles, shaders registered)");
    }
}
