package moriz.orangesunshine.recipe;

import moriz.orangesunshine.OrangeSunshine;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootTable;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;

/**
 * @author Sollace
 * @since 5 Jan 2023
 */
public interface PSRecipes {
    RecipeSerializer<FillRecepticalRecipe> FILL_RECEPTICAL = RecipeSerializer.register("orangesunshine:fill_receptical", new FillRecepticalRecipe.Serializer());
    RecipeSerializer<ChangeRecepticalRecipe> CHANGE_RECEPTICAL = RecipeSerializer.register("orangesunshine:change_receptical", new ChangeRecepticalRecipe.Serializer());
    RecipeSerializer<PouringRecipe> POUR_DRINK = RecipeSerializer.register("orangesunshine:pour_drink", new SpecialRecipeSerializer<>(PouringRecipe::new));
    RecipeSerializer<SmeltingFluidRecipe> SMELTING_RECEPTICAL = RecipeSerializer.register("orangesunshine:smelting_receptical", new SmeltingFluidRecipe.Serializer());
    RecipeSerializer<BottleRecipe> CRAFTING_SHAPED = RecipeSerializer.register("orangesunshine:crafting_shaped", new BottleRecipe.Serializer());
    RecipeSerializer<FluidAwareShapelessRecipe> SHAPELESS_FLUID = RecipeSerializer.register("orangesunshine:shapeless_fluid", new FluidAwareShapelessRecipe.Serializer());

    RecipeType<MashingRecipe> MASHING_TYPE = RecipeType.register("orangesunshine:mashing");
    RecipeSerializer<MashingRecipe> MASHING = RecipeSerializer.register("orangesunshine:mashing", new MashingRecipe.Serializer());

    RecipeType<DryingRecipe> DRYING_TYPE = RecipeType.register("orangesunshine:drying");
    RecipeSerializer<DryingRecipe> DRYING = RecipeSerializer.register("orangesunshine:drying", new DryingRecipe.Serializer(200));

    RecipeType<MortarPestleRecipe> MORTAR_PESTLE_TYPE = RecipeType.register("orangesunshine:mortar_pestle_recipe");
    RecipeSerializer<MortarPestleRecipe> MORTAR_PESTLE = RecipeSerializer.register("orangesunshine:mortar_pestle_recipe", new MortarPestleRecipe.Serializer());

    RecipeType<MortarPestleRecipe> MIXING_TABLE_TYPE = RecipeType.register("orangesunshine:mixing");
    RecipeSerializer<MortarPestleRecipe> MIXING_TABLE = RecipeSerializer.register("orangesunshine:mixing", new MortarPestleRecipe.Serializer());

    static void bootstrap() {
        LootTableEvents.MODIFY.register((res, manager, id, supplier, setter) -> {
            if (!"minecraft".contentEquals(id.getNamespace())) {
                return;
            }
            LootTable table = manager.getLootTable(new Identifier("orangesunshinemc", id.getPath()));
            if (table != LootTable.EMPTY) {
                final boolean isVillagerChest = id.getPath().contains("village");
                if ((isVillagerChest || OrangeSunshine.getConfig().balancing.worldGeneration.villageChests)
                || (!isVillagerChest || OrangeSunshine.getConfig().balancing.worldGeneration.dungeonChests)) {
                    supplier.pools(table.pools);
                }
            }
        });
    }
}
