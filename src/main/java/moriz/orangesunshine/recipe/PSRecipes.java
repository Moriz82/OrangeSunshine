package moriz.orangesunshine.recipe;

import moriz.orangesunshine.OrangeSunshine;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;

/**
 * @author Sollace
 * @since 5 Jan 2023
 */
public interface PSRecipes {
    RecipeSerializer<FillRecepticalRecipe> FILL_RECEPTICAL = RecipeSerializer.register("orangesunshine:fill_receptical", new FillRecepticalRecipe.Serializer());
    RecipeSerializer<ChangeRecepticalRecipe> CHANGE_RECEPTICAL = RecipeSerializer.register("orangesunshine:change_receptical", new ChangeRecepticalRecipe.Serializer());
    RecipeSerializer<PouringRecipe> POUR_DRINK = RecipeSerializer.register("orangesunshine:pour_drink", new PouringRecipe.Serializer());
    RecipeSerializer<SmeltingFluidRecipe> SMELTING_RECEPTICAL = RecipeSerializer.register("orangesunshine:smelting_receptical", new SmeltingFluidRecipe.Serializer());
    RecipeSerializer<BottleRecipe> CRAFTING_SHAPED = RecipeSerializer.register("orangesunshine:crafting_shaped", new BottleRecipe.Serializer());
    RecipeSerializer<FluidAwareShapelessRecipe> SHAPELESS_FLUID = RecipeSerializer.register("orangesunshine:shapeless_fluid", new FluidAwareShapelessRecipe.Serializer());

    RecipeType<MashingRecipe> MASHING_TYPE = registerType("mashing");
    RecipeSerializer<MashingRecipe> MASHING = RecipeSerializer.register("orangesunshine:mashing", new MashingRecipe.Serializer());

    RecipeType<DryingRecipe> DRYING_TYPE = registerType("drying");
    RecipeSerializer<DryingRecipe> DRYING = RecipeSerializer.register("orangesunshine:drying", new DryingRecipe.Serializer(200));

    RecipeType<MortarPestleRecipe> MORTAR_PESTLE_TYPE = registerType("mortar_pestle_recipe");
    RecipeSerializer<MortarPestleRecipe> MORTAR_PESTLE = RecipeSerializer.register("orangesunshine:mortar_pestle_recipe", new MortarPestleRecipe.Serializer());

    RecipeType<MixingTableRecipe> MIXING_TABLE_TYPE = registerType("mixing");
    RecipeSerializer<MixingTableRecipe> MIXING_TABLE = RecipeSerializer.register("orangesunshine:mixing", new MixingTableRecipe.Serializer());

    @SuppressWarnings("unchecked")
    static <T extends net.minecraft.world.item.crafting.Recipe<?>> RecipeType<T> registerType(String name) {
        return Registry.register(BuiltInRegistries.RECIPE_TYPE,
                OrangeSunshine.id(name),
                new RecipeType<T>() {
                    @Override public String toString() { return "orangesunshine:" + name; }
                });
    }

    static void bootstrap() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!"minecraft".contentEquals(key.identifier().getNamespace())) {
                return;
            }

            final boolean isVillagerChest = key.identifier().getPath().contains("village");
            if ((isVillagerChest || OrangeSunshine.getConfig().balancing.worldGeneration.villageChests)
                    || (!isVillagerChest || OrangeSunshine.getConfig().balancing.worldGeneration.dungeonChests)) {
                tableBuilder.withPool(LootPool.lootPool().add(NestedLootTable.lootTableReference(
                        net.minecraft.resources.ResourceKey.create(
                                Registries.LOOT_TABLE,
                                Identifier.fromNamespaceAndPath("orangesunshinemc", key.identifier().getPath())
                        )
                )));
            }
        });
    }
}
