/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity;

import com.google.common.collect.ImmutableSet;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.item.PSItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * @author Sollace
 * @since 1 Jan 2023
 */
public interface PSTradeOffers {
    ResourceKey<PoiType> DRUG_DEALER_POI = poi("drug_dealer");
    ResourceKey<VillagerProfession> DRUG_DEALER_PROFESSION = profession("drug_dealer");
    VillagerProfession DRUG_DEALER = register(DRUG_DEALER_PROFESSION,
            type -> type.is(DRUG_DEALER_POI),
            type -> type.is(DRUG_DEALER_POI),
            ImmutableSet.of(
                    PSItems.CANNABIS_SEEDS, PSItems.HOP_SEEDS, PSItems.TOBACCO_SEEDS,
                    PSItems.COCA_SEEDS, PSItems.COFFEA_CHERRIES, PSItems.MORNING_GLORY_SEEDS,
                    PSItems.CANNABIS_BUDS, PSItems.CANNABIS_LEAF,
                    PSItems.TOBACCO_LEAVES, PSItems.COCA_LEAVES,
                    PSItems.PEYOTE, PSItems.COFFEA_CHERRIES,
                    Items.BONE_MEAL
            ),
            ImmutableSet.of(Blocks.FARMLAND),
            SoundEvents.WANDERING_TRADER_DRINK_POTION
    );

    ResourceKey<VillagerProfession> DRUG_ADDICT_PROFESSION = profession("drug_addict");
    VillagerProfession DRUG_ADDICT = register(DRUG_ADDICT_PROFESSION,
            PoiType.NONE,
            VillagerProfession.ALL_ACQUIRABLE_JOBS,
            ImmutableSet.of(),
            ImmutableSet.of(),
            null
    );

    static void bootstrap() {
        TradeOfferHelper.registerVillagerOffers(DRUG_DEALER_PROFESSION, 1, factories -> {
            factories.add(sell(1, PSItems.CANNABIS_LEAF, 1, 9, 1, 0.7f));
            factories.add(sell(1, PSItems.CANNABIS_SEEDS, 5, 12, 2, 0.5f));
            factories.add(sell(1, PSItems.HASH_MUFFIN, 2, 3, 1, 0.7f));
            factories.add(sell(1, PSItems.COCA_LEAVES, 4, 4, 1, 0.5f));
            factories.add(sell(2, PSItems.COCA_SEEDS, 4, 4, 1, 0.5f));
            factories.add(sell(1, PSItems.PEYOTE, 5, 4, 4, 0.5f));

            factories.add(sell(1, PSItems.CIGARETTE, 4, 2, 3, 0.8f));
        });
        TradeOfferHelper.registerVillagerOffers(DRUG_DEALER_PROFESSION, 2, factories -> {
            factories.add(sell(2, PSItems.DRIED_CANNABIS_BUDS, 2, 8, 2, 0.9f));
            factories.add(sell(2, PSItems.DRIED_CANNABIS_LEAF, 2, 5, 3, 0.8f));
            factories.add(sell(3, PSItems.DRIED_PEYOTE, 10, 2, 2, 0.5f));
            factories.add(sell(3, PSItems.DRIED_COCA_LEAVES, 20, 3, 2, 0.5f));

            factories.add(sell(1, PSItems.CIGAR, 5, 2, 3, 0.5f));
            factories.add(sell(1, PSItems.SMOKING_PIPE, 5, 2, 3, 0.5f));
            factories.add(sell(6, PSItems.DRYING_TABLE, 1, 2, 3, 0.5f));
        });
        TradeOfferHelper.registerVillagerOffers(DRUG_DEALER_PROFESSION, 3, factories -> {
            factories.add(sell(5, PSItems.BROWN_MAGIC_MUSHROOMS, 8, 3, 3, 0.5f));
            factories.add(sell(2, PSItems.RED_MAGIC_MUSHROOMS, 8, 3, 3, 0.5f));

            factories.add(sell(3, PSItems.SYRINGE, 4, 3, 1, 0.5f));
            factories.add(sell(3, PSItems.BONG, 4, 3, 1, 0.5f));
            factories.add(sell(1, PSItems.PEYOTE_JOINT, 3, 2, 3, 0.5f));
/*            factories.add(sell(2, PSItems.LSD_PILL, 3, 2, 3, 0.5f));
            factories.add(trade(3, Items.PAPER, 2, PSItems.LSA_SQUARE, 3, 2, 3, 0.5f));*/

            factories.add(sell(1, PSItems.JOINT, 2, 2, 3, 0.5f));

            if (OrangeSunshine.getConfig().balancing.enableHarmonium) {
                factories.add(new VillagerTrades.DyedArmorForEmeralds(PSItems.HARMONIUM, 3, 7, 2));
            }
        });

        TradeOfferHelper.registerVillagerOffers(DRUG_ADDICT_PROFESSION, 1, factories -> {
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

        registerPoi(DRUG_DEALER_POI, Stream.concat(
                PSBlocks.DRYING_TABLE.getStateDefinition().getPossibleStates().stream(),
                PSBlocks.IRON_DRYING_TABLE.getStateDefinition().getPossibleStates().stream()
        ).collect(Collectors.toUnmodifiableSet()), 1, 1);

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

    private static VillagerTrades.ItemListing buy(int cost, Item returnItem, int returnCount, int maxUses, int experience, float priceChange) {
        return (level, entity, random) -> new MerchantOffer(
                new ItemCost(returnItem, returnCount),
                new ItemStack(Items.EMERALD, cost),
                maxUses,
                experience,
                priceChange
        );
    }

    private static VillagerTrades.ItemListing sell(int cost, Item returnItem, int returnCount, int maxUses, int experience, float priceChange) {
        return (level, entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, cost),
                new ItemStack(returnItem, returnCount),
                maxUses,
                experience,
                priceChange
        );
    }

    private static VillagerTrades.ItemListing trade(int cost, Item item, int count, Item returnItem, int returnCount, int maxUses, int experience, float priceChange) {
        return (level, entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, cost),
                Optional.of(new ItemCost(item, count)),
                new ItemStack(returnItem, returnCount),
                maxUses,
                experience,
                priceChange
        );
    }

    private static ResourceKey<PoiType> poi(String id) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, OrangeSunshine.id(id));
    }

    private static ResourceKey<VillagerProfession> profession(String id) {
        return ResourceKey.create(Registries.VILLAGER_PROFESSION, OrangeSunshine.id(id));
    }

    private static VillagerProfession register(ResourceKey<VillagerProfession> key, Predicate<Holder<PoiType>> heldWorkstation, Predicate<Holder<PoiType>> acquirableWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound) {
        return Registry.register(
                BuiltInRegistries.VILLAGER_PROFESSION,
                key.identifier(),
                new VillagerProfession(
                        Component.translatable("entity.minecraft.villager." + key.identifier().getPath()),
                        heldWorkstation,
                        acquirableWorkstation,
                        gatherableItems,
                        secondaryJobSites,
                        workSound
                )
        );
    }

    private static PoiType registerPoi(ResourceKey<PoiType> key, java.util.Set<BlockState> states, int maxTickets, int validRange) {
        PoiType poiType = Registry.register(BuiltInRegistries.POINT_OF_INTEREST_TYPE, key.identifier(), new PoiType(states, maxTickets, validRange));

        try {
            Method registerBlockStates = PoiTypes.class.getDeclaredMethod("registerBlockStates", Holder.class, java.util.Set.class);
            registerBlockStates.setAccessible(true);
            registerBlockStates.invoke(null, BuiltInRegistries.POINT_OF_INTEREST_TYPE.getOrThrow(key), states);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to register POI block states for " + key.identifier(), e);
        }

        return poiType;
    }
}
