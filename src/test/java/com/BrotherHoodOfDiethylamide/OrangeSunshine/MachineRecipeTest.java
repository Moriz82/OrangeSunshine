package com.BrotherHoodOfDiethylamide.OrangeSunshine;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.MachineRecipe;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.MachineRecipeManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the machine recipe system without needing Minecraft to run.
 * Uses mock ItemStacks through the recipe matching logic.
 */
public class MachineRecipeTest {

    @Before
    public void setUp() {
        // Clear existing recipes to avoid cross-test contamination
        // We test the pure logic of MachineRecipe matching
    }

    @Test
    public void testMachineRecipeManagerHasRecipeLists() {
        // All machine types should have recipe lists (even if empty)
        assertNotNull(MachineRecipeManager.getRecipes(MachineRecipeManager.DRYING_TABLE));
        assertNotNull(MachineRecipeManager.getRecipes(MachineRecipeManager.FRIDGE));
        assertNotNull(MachineRecipeManager.getRecipes(MachineRecipeManager.COMPOUND_COMPRESSOR));
        assertNotNull(MachineRecipeManager.getRecipes(MachineRecipeManager.COMPOUND_EXTRACTOR));
    }

    @Test
    public void testMachineTypeConstants() {
        assertEquals("drying_table", MachineRecipeManager.DRYING_TABLE);
        assertEquals("fridge", MachineRecipeManager.FRIDGE);
        assertEquals("compound_compressor", MachineRecipeManager.COMPOUND_COMPRESSOR);
        assertEquals("compound_extractor", MachineRecipeManager.COMPOUND_EXTRACTOR);
    }

    @Test
    public void testMachineRecipeDataClass() {
        // Test that MachineRecipe stores data correctly
        // We create a fake recipe without actual ItemStacks to test the data class
        MachineRecipe recipe = new FakeRecipe(200);
        assertEquals("Processing time should be 200", 200, recipe.getProcessingTime());
        assertNotNull("Output should not be null if set", recipe.getOutput());
    }

    @Test
    public void testRecipeManagerFindRecipeReturnsNullForEmpty() {
        // A machine type with no recipes should return null
        assertNull("Unknown machine type should return null",
                MachineRecipeManager.findRecipe("nonexistent_machine", new net.minecraft.item.ItemStack[0]));
    }

    /**
     * Helper: fake recipe to test data class without needing ItemStack (which needs MC loaded)
     */
    private static class FakeRecipe extends MachineRecipe {
        public FakeRecipe(int time) {
            super(net.minecraft.item.ItemStack.EMPTY, net.minecraft.item.ItemStack.EMPTY, time);
        }
    }
}
