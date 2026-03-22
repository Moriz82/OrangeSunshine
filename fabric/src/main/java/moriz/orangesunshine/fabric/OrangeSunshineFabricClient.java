package moriz.orangesunshine.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.client.OrangeSunshineClient;
import moriz.orangesunshine.client.PSClientConfig;
import moriz.orangesunshine.fabric.client.DrugDebugHud;
import moriz.orangesunshine.fabric.network.FabricS2CNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;

public final class OrangeSunshineFabricClient implements ClientModInitializer {

    private static KeyMapping drugDebugKey;

    @Override
    public void onInitializeClient() {
        FabricS2CNetworking.registerClientReceivers();
        OrangeSunshine.LOGGER.info("[OrangeSunshine] S2C_RECEIVERS_REGISTERED");
        new OrangeSunshineClient().onInitializeClient();

        // Auto-enable verbose debug logging in dev/runClient environment
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            PSClientConfig visual = OrangeSunshineClient.getConfig();
            visual.visual.visualDebugLogging = true;
            OrangeSunshine.LOGGER.info("[OrangeSunshine] DEV env — visualDebugLogging forced ON");
        }

        // F7 drug debug HUD
        drugDebugKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.orangesunshine.drug_debug",
                InputConstants.KEY_F7,
                KeyMapping.Category.MISC
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (drugDebugKey.consumeClick()) {
                DrugDebugHud.toggle();
                OrangeSunshine.LOGGER.info("[OrangeSunshine] Drug debug HUD: {}", DrugDebugHud.isVisible() ? "ON" : "OFF");
            }
        });
        HudRenderCallback.EVENT.register((guiGraphics, deltaTick) -> DrugDebugHud.render(guiGraphics, net.minecraft.client.Minecraft.getInstance()));

        // TODO: Item tint sources (CompoundItem/MixtureItem colors) are now data-driven
        // via ItemTintSource in 1.21.11. ColorProviderRegistry.ITEM no longer exists.
        // Dynamic item color providers need to be ported to JSON tint sources or a custom
        // ItemTintSource implementation. Deferred for visual layer pass.
    }
}
