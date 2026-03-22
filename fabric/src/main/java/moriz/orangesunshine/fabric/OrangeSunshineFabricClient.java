package moriz.orangesunshine.fabric;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.client.OrangeSunshineClient;
import moriz.orangesunshine.fabric.network.FabricS2CNetworking;
import net.fabricmc.api.ClientModInitializer;

public final class OrangeSunshineFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricS2CNetworking.registerClientReceivers();
        OrangeSunshine.LOGGER.info("[OrangeSunshine] S2C_RECEIVERS_REGISTERED");
        new OrangeSunshineClient().onInitializeClient();

        // TODO: Item tint sources (CompoundItem/MixtureItem colors) are now data-driven
        // via ItemTintSource in 1.21.11. ColorProviderRegistry.ITEM no longer exists.
        // Dynamic item color providers need to be ported to JSON tint sources or a custom
        // ItemTintSource implementation. Deferred for visual layer pass.
    }
}
