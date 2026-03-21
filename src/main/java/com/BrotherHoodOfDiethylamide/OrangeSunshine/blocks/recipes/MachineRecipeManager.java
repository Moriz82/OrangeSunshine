package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineRecipeManager {
    public static final String DRYING_TABLE = "drying_table";
    public static final String FRIDGE = "fridge";
    public static final String COMPOUND_COMPRESSOR = "compound_compressor";
    public static final String COMPOUND_EXTRACTOR = "compound_extractor";

    private static final Map<String, List<MachineRecipe>> RECIPES = new HashMap<>();

    public static void register(String machineType, MachineRecipe recipe) {
        RECIPES.computeIfAbsent(machineType, k -> new ArrayList<>()).add(recipe);
    }

    public static void register(String machineType, ItemStack input, ItemStack output, int processingTime) {
        register(machineType, new MachineRecipe(input, output, processingTime));
    }

    @Nullable
    public static MachineRecipe findRecipe(String machineType, ItemStack[] inputs) {
        List<MachineRecipe> recipes = RECIPES.get(machineType);
        if (recipes == null) return null;
        for (MachineRecipe recipe : recipes) {
            if (recipe.matches(inputs)) return recipe;
        }
        return null;
    }

    @Nullable
    public static MachineRecipe findRecipe(String machineType, ItemStack input) {
        return findRecipe(machineType, new ItemStack[]{input});
    }

    public static List<MachineRecipe> getRecipes(String machineType) {
        return RECIPES.getOrDefault(machineType, new ArrayList<>());
    }
}
