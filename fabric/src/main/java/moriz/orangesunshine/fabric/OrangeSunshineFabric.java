package moriz.orangesunshine.fabric;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.item.PSItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;

public final class OrangeSunshineFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) ->
                DrugProperties.of(player).sendCapabilities());
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) ->
                DrugProperties.of(newPlayer).copyFrom(DrugProperties.of(oldPlayer), alive));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                DrugProperties.of(handler.player).sendCapabilities());

        OrangeSunshine.init();

        FuelRegistryEvents.BUILD.register((builder, context) -> {
            builder.add(PSItems.LATTICE, 700);
            builder.add(PSItems.SMOKING_PIPE, 200);
            builder.add(PSItems.JOINT, 20);
            builder.add(PSItems.PEYOTE_JOINT, 20);
            builder.add(PSItems.CIGAR, 80);
            builder.add(PSItems.CIGARETTE, 50);
            builder.add(PSItems.WOODEN_MUG, 50);
        });
    }
}
