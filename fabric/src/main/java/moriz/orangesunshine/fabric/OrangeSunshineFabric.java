package moriz.orangesunshine.fabric;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.item.PSItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;

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

        FlammableBlockRegistry.getDefaultInstance().add(PSBlocks.JUNIPER_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(PSBlocks.STRIPPED_JUNIPER_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(PSBlocks.JUNIPER_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(PSBlocks.STRIPPED_JUNIPER_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(PSBlocks.JUNIPER_LEAVES, 30, 60);
        FlammableBlockRegistry.getDefaultInstance().add(PSBlocks.LATTICE, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(PSBlocks.WINE_GRAPE_LATTICE, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(PSBlocks.MORNING_GLORY_LATTICE, 5, 20);
        StrippableBlockRegistry.register(PSBlocks.JUNIPER_LOG, PSBlocks.STRIPPED_JUNIPER_LOG);
        StrippableBlockRegistry.register(PSBlocks.JUNIPER_WOOD, PSBlocks.STRIPPED_JUNIPER_WOOD);

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
