package moriz.orangesunshine.fabric;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.PSDamageTypes;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.command.PSCommands;
import moriz.orangesunshine.entity.PSTradeOffers;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.item.PSItems;
import moriz.orangesunshine.world.gen.PSWorldGen;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
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
        PSWorldGen.bootstrap();
        PSCommands.bootstrap();
        PSDamageTypes.bootstrap();
        registerTradeOffers();

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

    private void registerTradeOffers() {
        TradeOfferHelper.registerVillagerOffers(PSTradeOffers.DRUG_DEALER_PROFESSION, 1, factories -> {
            factories.add(sell(1, PSItems.CANNABIS_LEAF, 1, 9, 1, 0.7f));
            factories.add(sell(1, PSItems.CANNABIS_SEEDS, 5, 12, 2, 0.5f));
            factories.add(sell(1, PSItems.HASH_MUFFIN, 2, 3, 1, 0.7f));
            factories.add(sell(1, PSItems.COCA_LEAVES, 4, 4, 1, 0.5f));
            factories.add(sell(2, PSItems.COCA_SEEDS, 4, 4, 1, 0.5f));
            factories.add(sell(1, PSItems.PEYOTE, 5, 4, 4, 0.5f));
            factories.add(sell(1, PSItems.CIGARETTE, 4, 2, 3, 0.8f));
        });
        TradeOfferHelper.registerVillagerOffers(PSTradeOffers.DRUG_DEALER_PROFESSION, 2, factories -> {
            factories.add(sell(2, PSItems.DRIED_CANNABIS_BUDS, 2, 8, 2, 0.9f));
            factories.add(sell(2, PSItems.DRIED_CANNABIS_LEAF, 2, 5, 3, 0.8f));
            factories.add(sell(3, PSItems.DRIED_PEYOTE, 10, 2, 2, 0.5f));
            factories.add(sell(3, PSItems.DRIED_COCA_LEAVES, 20, 3, 2, 0.5f));
            factories.add(sell(1, PSItems.CIGAR, 5, 2, 3, 0.5f));
            factories.add(sell(1, PSItems.SMOKING_PIPE, 5, 2, 3, 0.5f));
            factories.add(sell(6, PSItems.DRYING_TABLE, 1, 2, 3, 0.5f));
        });
        TradeOfferHelper.registerVillagerOffers(PSTradeOffers.DRUG_DEALER_PROFESSION, 3, factories -> {
            factories.add(sell(5, PSItems.BROWN_MAGIC_MUSHROOMS, 8, 3, 3, 0.5f));
            factories.add(sell(2, PSItems.RED_MAGIC_MUSHROOMS, 8, 3, 3, 0.5f));
            factories.add(sell(3, PSItems.SYRINGE, 4, 3, 1, 0.5f));
            factories.add(sell(3, PSItems.BONG, 4, 3, 1, 0.5f));
            factories.add(sell(1, PSItems.PEYOTE_JOINT, 3, 2, 3, 0.5f));
            factories.add(sell(1, PSItems.JOINT, 2, 2, 3, 0.5f));
            if (OrangeSunshine.getConfig().balancing.enableHarmonium) {
                factories.add(new VillagerTrades.DyedArmorForEmeralds(PSItems.HARMONIUM, 3, 7, 2));
            }
        });
        TradeOfferHelper.registerVillagerOffers(PSTradeOffers.DRUG_ADDICT_PROFESSION, 1, factories -> {
            factories.add(buy(5, PSItems.BROWN_MAGIC_MUSHROOMS, 8, 3, 3, 0.5f));
            factories.add(buy(2, PSItems.RED_MAGIC_MUSHROOMS, 8, 3, 3, 0.5f));
            factories.add(buy(1, PSItems.JOINT, 2, 2, 3, 0.5f));
            factories.add(buy(1, PSItems.CIGARETTE, 4, 2, 3, 0.8f));
            factories.add(buy(1, PSItems.CIGAR, 5, 2, 3, 0.5f));
            factories.add(buy(1, PSItems.HASH_MUFFIN, 2, 3, 1, 0.7f));
            factories.add(buy(4, PSItems.DRIED_CANNABIS_BUDS, 2, 8, 2, 0.9f));
            factories.add(buy(4, PSItems.DRIED_CANNABIS_LEAF, 2, 5, 3, 0.8f));
            factories.add(buy(5, PSItems.DRIED_PEYOTE, 10, 2, 2, 0.5f));
            factories.add(buy(5, PSItems.DRIED_COCA_LEAVES, 20, 3, 2, 0.5f));
        });
        if (OrangeSunshine.getConfig().balancing.worldGeneration.farmerDrugDeals) {
            TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 1, factories -> {
                factories.add(sell(2, PSItems.WINE_GRAPES, 3, 8, 1, 0.5F));
                factories.add(sell(1, PSItems.HOP_CONES, 1, 4, 1, 0.6F));
                factories.add(sell(1, PSItems.HOP_SEEDS, 1, 4, 1, 0.4F));
                factories.add(sell(1, PSItems.WOODEN_MUG, 1, 4, 1, 0.5F));
                factories.add(sell(4, PSItems.DRIED_TOBACCO, 1, 4, 1, 0.3F));
                factories.add(sell(2, PSItems.CIGARETTE, 1, 4, 1, 0.8F));
                factories.add(sell(2, PSItems.CIGAR, 1, 4, 1, 0.8F));
                factories.add(sell(2, PSItems.TOBACCO_SEEDS, 1, 4, 1, 0.3F));
                factories.add(sell(1, PSItems.COFFEE_BEANS, 1, 4, 1, 0.8F));
                factories.add(sell(1, PSItems.COFFEA_CHERRIES, 1, 4, 1, 0.6F));
            });
        }
    }

    private static net.minecraft.world.item.ItemStack emeralds(int count) {
        return new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.EMERALD, count);
    }

    private static VillagerTrades.ItemListing buy(int cost, net.minecraft.world.item.Item returnItem, int returnCount, int maxUses, int experience, float priceChange) {
        return (level, entity, random) -> new net.minecraft.world.item.trading.MerchantOffer(
                new net.minecraft.world.item.trading.ItemCost(returnItem, returnCount),
                emeralds(cost), maxUses, experience, priceChange);
    }

    private static VillagerTrades.ItemListing sell(int cost, net.minecraft.world.item.Item returnItem, int returnCount, int maxUses, int experience, float priceChange) {
        return (level, entity, random) -> new net.minecraft.world.item.trading.MerchantOffer(
                new net.minecraft.world.item.trading.ItemCost(net.minecraft.world.item.Items.EMERALD, cost),
                new net.minecraft.world.item.ItemStack(returnItem, returnCount),
                maxUses, experience, priceChange);
    }
}
