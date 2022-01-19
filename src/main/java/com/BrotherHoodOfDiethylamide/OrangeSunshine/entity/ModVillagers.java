package com.BrotherHoodOfDiethylamide.OrangeSunshine.entity;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ModBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.INameable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ModVillagers {
	public static final DeferredRegister<PointOfInterestType> POINT_OF_INTEREST_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, OrangeSunshine.MOD_ID);
	public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, OrangeSunshine.MOD_ID);
	//public static final Method register = ObfuscationReflectionHelper.findMethod(PointOfInterestType.class, "register", String.class, Set.class, int.class, int.class);


	public static final RegistryObject<PointOfInterestType> SHMOKESTACKZ_POI = POINT_OF_INTEREST_TYPES.register("shmokestackz",
			() -> new PointOfInterestType("shmokestackz", PointOfInterestType.getBlockStates(ModBlocks.DRYING_TABLE.get()), 1,1));
	public static final RegistryObject<VillagerProfession> SHMOKESTACKZ_PROF = VILLAGER_PROFESSIONS.register("shmokestackz",
			() -> new VillagerProfession("shmokestackz", SHMOKESTACKZ_POI.get(), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_CLERIC));


	public static final RegistryObject<PointOfInterestType> DR_FLEUR_POI = POINT_OF_INTEREST_TYPES.register("dr_fleur",
			() -> new PointOfInterestType("dr_fleur", PointOfInterestType.getBlockStates(ModBlocks.COMPOUND_EXTRACTOR.get()), 1,1));
	public static final RegistryObject<VillagerProfession> DR_FLEUR_PROF = VILLAGER_PROFESSIONS.register("dr_fleur",
			() -> new VillagerProfession("dr_fleur", DR_FLEUR_POI.get(), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_CLERIC));


	public static void fillShmokeStackzTrades() {
		MultiItemForEmeraldsTrade multiTrade = new MultiItemForEmeraldsTrade(ImmutableList.of(
				ModItems.WEED_BUD.get(),
				ModItems.WEED_LEAF.get(),
				ModItems.WEED_JOINT.get(),
				ModItems.WEED_EXTRACT.get(),
				ModItems.WEED_SEEDS.get(),
				ModItems.DRIED_WEED_BUD.get(),
				ModItems.DRIED_WEED_LEAF.get()
		), ImmutableList.of(1, 1, 1, 1, 1, 1, 1), ImmutableList.of(1, 1, 1, 1, 1, 1, 1), 10, 20);
		MultiEmeraldsForItemTrade multiTrade2 = multiTrade.toEmeraldsForItem();
		VillagerTrades.ITrade[] level1 = new VillagerTrades.ITrade[]{multiTrade2, multiTrade2};//new VillagerTrades.EmeraldForItemsTrade(ModItems.WEED_BUD, 1, 5, 15)};
		VillagerTrades.ITrade[] level2 = new VillagerTrades.ITrade[]{multiTrade2, multiTrade2};//new VillagerTrades.EmeraldForItemsTrade(ModItems.WEED_EXTRACT, 1, 10, 30)};
		VillagerTrades.ITrade[] level3 = new VillagerTrades.ITrade[]{multiTrade, multiTrade};
		VillagerTrades.ITrade[] level4 = new VillagerTrades.ITrade[]{multiTrade, multiTrade};
		VillagerTrades.ITrade[] level5 = new VillagerTrades.ITrade[]{multiTrade, multiTrade};
		VillagerTrades.TRADES.put(SHMOKESTACKZ_PROF.get(), toIntMap(ImmutableMap.of(1, level1, 2, level2, 3, level3, 4, level4, 5, level5)));
	}

	public static void fillDrFleurTrades() {
		MultiItemForEmeraldsTrade multiTrade = new MultiItemForEmeraldsTrade(ImmutableList.of(
				ModItems.ORANGESUNSHINE_BLOTTER.get(),
				ModItems.ORANGESUNSHINE_BOTTLE.get(),
				ModItems.LSD_BOTTLE.get(),
				ModItems.LSD_BLOTTER.get(),
				ModItems.DMT.get(),
				ModItems.DMT_5_MEO.get(),
				ModItems.BROWN_SHROOMS.get(),
				ModItems.DRIED_BROWN_MUSHROOM.get(),
				ModItems.RED_SHROOMS.get(),
				ModItems.DRIED_RED_MUSHROOM.get(),
				ModItems.OPIUM_BOTTLE_0.get()
		), ImmutableList.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1), ImmutableList.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1), 10, 20);
		MultiEmeraldsForItemTrade multiTrade2 = multiTrade.toEmeraldsForItem();
		VillagerTrades.ITrade[] level1 = new VillagerTrades.ITrade[]{multiTrade2, multiTrade2};//new VillagerTrades.EmeraldForItemsTrade(ModItems.WEED_BUD, 1, 5, 15)};
		VillagerTrades.ITrade[] level2 = new VillagerTrades.ITrade[]{multiTrade2, multiTrade2};//new VillagerTrades.EmeraldForItemsTrade(ModItems.WEED_EXTRACT, 1, 10, 30)};
		VillagerTrades.ITrade[] level3 = new VillagerTrades.ITrade[]{multiTrade, multiTrade};
		VillagerTrades.ITrade[] level4 = new VillagerTrades.ITrade[]{multiTrade, multiTrade};
		VillagerTrades.ITrade[] level5 = new VillagerTrades.ITrade[]{multiTrade, multiTrade};
		VillagerTrades.TRADES.put(DR_FLEUR_PROF.get(), toIntMap(ImmutableMap.of(1, level1, 2, level2, 3, level3, 4, level4, 5, level5)));
	}

	private static Int2ObjectMap<VillagerTrades.ITrade[]> toIntMap(ImmutableMap<Integer, VillagerTrades.ITrade[]> p_221238_0_) {
		return new Int2ObjectOpenHashMap<>(p_221238_0_);
	}

	public static class MultiItemForEmeraldsTrade implements VillagerTrades.ITrade {
		private final List<Item> items;
		private final List<Integer> amountOfItems;
		private final List<Integer> amountOfEmeralds;
		private final int uses;
		private final int villagerExp;

		public MultiItemForEmeraldsTrade(List<Item> items, List<Integer> amountOfItems, List<Integer> amountOfEmeralds, int uses, int villagerExp) {
			this.items = items;
			this.amountOfItems = amountOfItems;
			this.amountOfEmeralds = amountOfEmeralds;
			this.uses = uses;
			this.villagerExp = villagerExp;
		}

		public MultiEmeraldsForItemTrade toEmeraldsForItem(){
			return new MultiEmeraldsForItemTrade(items, amountOfItems, amountOfEmeralds, uses, villagerExp);
		}

		@Nullable
		@Override
		public MerchantOffer getOffer(@Nonnull Entity entity, Random random) {
			int choose = random.nextInt(items.size());
			return new MerchantOffer(new ItemStack(Items.EMERALD, amountOfEmeralds.get(choose)), new ItemStack(items.get(choose), amountOfItems.get(choose)), this.uses, this.villagerExp, 0.05f);
		}
	}

	public static class MultiEmeraldsForItemTrade implements VillagerTrades.ITrade {
		private final List<Item> items;
		private final List<Integer> amountOfItems;
		private final List<Integer> amountOfEmeralds;
		private final int uses;
		private final int villagerExp;

		public MultiEmeraldsForItemTrade(List<Item> items, List<Integer> amountOfItems, List<Integer> amountOfEmeralds, int uses, int villagerExp) {
			this.items = items;
			this.amountOfItems = amountOfItems;
			this.amountOfEmeralds = amountOfEmeralds;
			this.uses = uses;
			this.villagerExp = villagerExp;
		}

		public MultiItemForEmeraldsTrade toItemForEmeralds(){
			return new MultiItemForEmeraldsTrade(items, amountOfItems, amountOfEmeralds, uses, villagerExp);
		}

		@Nullable
		@Override
		public MerchantOffer getOffer(@Nonnull Entity entity, Random random) {
			int choose = random.nextInt(items.size());
			return new MerchantOffer(new ItemStack(items.get(choose), amountOfItems.get(choose)), new ItemStack(Items.EMERALD, amountOfEmeralds.get(choose)), this.uses, this.villagerExp, 0.05f);
		}
	}

	/*public static void registerPOI() {
		try {
			register.invoke("shmokestackz", PointOfInterestType.getBlockStates(ModBlocks.DRYING_TABLE.get()), 1, 1);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}*/
}
