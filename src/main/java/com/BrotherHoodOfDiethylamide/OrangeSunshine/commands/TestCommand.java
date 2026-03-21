package com.BrotherHoodOfDiethylamide.OrangeSunshine.commands;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ModBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.MachineRecipe;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.recipes.MachineRecipeManager;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity.TileDryingTable;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.PlayerProperties;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugEffects;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugInstance;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugRegistry;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.*;

/**
 * /ostest - Automated in-game test runner for OrangeSunshine features.
 * Run from creative mode to verify drug effects, machine recipes, items, and blocks work.
 *
 * Usage: /ostest [category]
 *   /ostest         - run all tests
 *   /ostest drugs   - test drug system only
 *   /ostest recipes - test machine recipes only
 *   /ostest items   - test all items exist
 *   /ostest blocks  - test all blocks exist
 *   /ostest give    - give all mod items to player
 */
public class TestCommand extends CommandBase {
    private int passed = 0;
    private int failed = 0;
    private List<String> failures = new ArrayList<>();

    @Override
    public String getName() {
        return "ostest";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/ostest [drugs|recipes|items|blocks|give]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        passed = 0;
        failed = 0;
        failures = new ArrayList<>();

        String category = args.length > 0 ? args[0].toLowerCase() : "all";

        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;

            switch (category) {
                case "drugs":
                    testDrugs(sender, player);
                    break;
                case "recipes":
                    testRecipes(sender);
                    break;
                case "items":
                    testItems(sender);
                    break;
                case "blocks":
                    testBlocks(sender);
                    break;
                case "give":
                    giveAllItems(sender, player);
                    return;
                default:
                    testItems(sender);
                    testBlocks(sender);
                    testDrugs(sender, player);
                    testRecipes(sender);
                    testMachines(sender, player);
                    break;
            }

            printSummary(sender);
        }
    }

    private void testItems(ICommandSender sender) {
        send(sender, TextFormatting.YELLOW + "=== Testing Items ===");

        // Check all items exist and are registered
        checkItems(sender, "Red Shrooms", ModItems.RED_SHROOMS);
        checkItems(sender, "Cocaine Rock", ModItems.COCAINE_ROCK);
        checkItems(sender, "Cocaine Powder", ModItems.COCAINE_POWDER);
        checkItems(sender, "Weed Joint", ModItems.WEED_JOINT);
        checkItems(sender, "LSD Blotter", ModItems.LSD_BLOTTER);
        checkItems(sender, "LSD Bottle", ModItems.LSD_BOTTLE);
        checkItems(sender, "OS Blotter", ModItems.ORANGESUNSHINE_BLOTTER);
        checkItems(sender, "OS Bottle", ModItems.ORANGESUNSHINE_BOTTLE);
        checkItems(sender, "DMT", ModItems.DMT_ITEM);
        checkItems(sender, "MDMA", ModItems.MDMA);
        checkItems(sender, "Morphine Syringe", ModItems.MORPHINE_SYRINGE);
        checkItems(sender, "Bong", ModItems.BONG);
        checkItems(sender, "Psych Ingot", ModItems.PSYCH_INGOT);
        checkItems(sender, "Psych Sword", ModItems.PSYCH_SWORD);
        checkItems(sender, "Weed Leaf", ModItems.WEED_LEAF);
        checkItems(sender, "Dried Weed Bud", ModItems.DRIED_WEED_BUD);
        checkItems(sender, "Root Bark", ModItems.ROOT_BARK);
        checkItems(sender, "Blotter", ModItems.BLOTTER);
        checkItems(sender, "Rolling Paper", ModItems.ROLLING_PAPER);

        // Check ALL_ITEMS list
        assertTrue(sender, "ModItems.ALL_ITEMS not empty", !ModItems.ALL_ITEMS.isEmpty());
        assertTrue(sender, "ModItems.ALL_ITEMS has 60+ items", ModItems.ALL_ITEMS.size() >= 60);
        send(sender, "Total items registered: " + ModItems.ALL_ITEMS.size());
    }

    private void checkItems(ICommandSender sender, String name, Item item) {
        assertTrue(sender, "Item '" + name + "' not null", item != null);
        if (item != null) {
            assertTrue(sender, "Item '" + name + "' has registry name",
                    item.getRegistryName() != null);
        }
    }

    private void testBlocks(ICommandSender sender) {
        send(sender, TextFormatting.YELLOW + "=== Testing Blocks ===");
        assertTrue(sender, "ModBlocks.PSYCH_ORE exists", ModBlocks.PSYCH_ORE != null);
        assertTrue(sender, "ModBlocks.DRYING_TABLE exists", ModBlocks.DRYING_TABLE != null);
        assertTrue(sender, "ModBlocks.FRIDGE exists", ModBlocks.FRIDGE != null);
        assertTrue(sender, "ModBlocks.COMPOUND_COMPRESSOR exists", ModBlocks.COMPOUND_COMPRESSOR != null);
        assertTrue(sender, "ModBlocks.COMPOUND_EXTRACTOR exists", ModBlocks.COMPOUND_EXTRACTOR != null);
        assertTrue(sender, "ModBlocks.CUT_POPPY_BLOCK exists", ModBlocks.CUT_POPPY_BLOCK != null);
        assertTrue(sender, "ModBlocks.WEED_BLOCK exists", ModBlocks.WEED_BLOCK != null);
        assertTrue(sender, "ModBlocks.COKE_CAKE_BLOCK exists", ModBlocks.COKE_CAKE_BLOCK != null);
        assertTrue(sender, "ModBlocks.ALL_BLOCKS has 15+ blocks", ModBlocks.ALL_BLOCKS.size() >= 15);
        send(sender, "Total blocks registered: " + ModBlocks.ALL_BLOCKS.size());
    }

    private void testDrugs(ICommandSender sender, EntityPlayer player) {
        send(sender, TextFormatting.YELLOW + "=== Testing Drug System ===");

        // Check DrugRegistry has all expected drugs
        String[] expectedDrugs = {"red_shrooms", "brown_shrooms", "cocaine", "weed", "morphine",
                "lsd_bottle", "lsd_blotter", "orangesunshine_bottle", "orangesunshine_blotter",
                "dmt", "dmt_5_meo", "peyote", "nic", "mdma"};
        for (String name : expectedDrugs) {
            assertTrue(sender, "Drug '" + name + "' in registry", DrugRegistry.DRUGS.containsKey(name));
        }
        assertTrue(sender, "DrugRegistry has 14 drugs", DrugRegistry.DRUGS.size() == 14);

        // Test adding a drug effect
        Drug cocaine = Drug.byName("cocaine");
        assertTrue(sender, "cocaine drug found", cocaine != null);

        if (cocaine != null) {
            // Add a cocaine effect
            Drug.addDrug(player, new DrugInstance(cocaine, 0, 0.5f, 100));

            // Check it was added
            List<DrugInstance> sources = Drug.getDrugSources(player);
            assertTrue(sender, "Drug source added", !sources.isEmpty());

            // Tick several times to activate
            for (int i = 0; i < 5; i++) {
                Drug.tick(player);
            }

            // Check active drugs
            Map<Drug, Float> active = Drug.getActiveDrugs(player);
            assertTrue(sender, "Cocaine active after ticks", active.containsKey(cocaine));

            // Clean up
            Drug.clearDrugs(player);
            assertTrue(sender, "Drugs cleared", Drug.getDrugSources(player).isEmpty());
        }

        // Test ADSR envelope
        Drug.Envelope envelope = new Drug.Envelope(100f, 50f, 0.8f, 200f);
        float level = envelope.getLevel(150, 1000);
        assertTrue(sender, "Envelope level > 0", level > 0f);
    }

    private void testRecipes(ICommandSender sender) {
        send(sender, TextFormatting.YELLOW + "=== Testing Machine Recipes ===");

        List<MachineRecipe> dtRecipes = MachineRecipeManager.getRecipes(MachineRecipeManager.DRYING_TABLE);
        assertTrue(sender, "Drying table has recipes", !dtRecipes.isEmpty());

        List<MachineRecipe> frRecipes = MachineRecipeManager.getRecipes(MachineRecipeManager.FRIDGE);
        assertTrue(sender, "Fridge has recipes", !frRecipes.isEmpty());

        List<MachineRecipe> ccRecipes = MachineRecipeManager.getRecipes(MachineRecipeManager.COMPOUND_COMPRESSOR);
        assertTrue(sender, "Compound compressor has recipes", !ccRecipes.isEmpty());

        List<MachineRecipe> ceRecipes = MachineRecipeManager.getRecipes(MachineRecipeManager.COMPOUND_EXTRACTOR);
        assertTrue(sender, "Compound extractor has recipes", !ceRecipes.isEmpty());

        // Test specific recipe lookups
        MachineRecipe weedLeafRecipe = MachineRecipeManager.findRecipe(
                MachineRecipeManager.DRYING_TABLE, new ItemStack(ModItems.WEED_LEAF));
        assertTrue(sender, "Weed leaf recipe exists in drying table", weedLeafRecipe != null);
        if (weedLeafRecipe != null) {
            assertTrue(sender, "Weed leaf -> dried weed leaf",
                    weedLeafRecipe.getOutput().getItem() == ModItems.DRIED_WEED_LEAF);
        }

        MachineRecipe cokeRecipe = MachineRecipeManager.findRecipe(
                MachineRecipeManager.COMPOUND_COMPRESSOR, new ItemStack(ModItems.COCAINE_ROCK));
        assertTrue(sender, "Cocaine rock recipe exists in compressor", cokeRecipe != null);

        send(sender, "Drying table recipes: " + dtRecipes.size());
        send(sender, "Fridge recipes: " + frRecipes.size());
        send(sender, "Compressor recipes: " + ccRecipes.size());
        send(sender, "Extractor recipes: " + ceRecipes.size());
    }

    private void testMachines(ICommandSender sender, EntityPlayer player) {
        send(sender, TextFormatting.YELLOW + "=== Testing Machine Tile Entities ===");
        World world = player.world;
        BlockPos testPos = player.getPosition().up(3);

        // Test DryingTable tile entity
        world.setBlockState(testPos, ModBlocks.DRYING_TABLE.getDefaultState());
        TileEntity te = world.getTileEntity(testPos);
        assertTrue(sender, "DryingTable tile entity spawns", te instanceof TileDryingTable);
        if (te instanceof TileDryingTable) {
            TileDryingTable dt = (TileDryingTable) te;
            dt.setInventorySlotContents(0, new ItemStack(ModItems.WEED_LEAF));
            assertTrue(sender, "DryingTable accepts weed leaf", !dt.getStackInSlot(0).isEmpty());
        }

        // Clean up
        world.setBlockToAir(testPos);
        assertTrue(sender, "Block removed successfully", world.isAirBlock(testPos));
    }

    private void giveAllItems(ICommandSender sender, EntityPlayer player) {
        int given = 0;
        for (Item item : ModItems.ALL_ITEMS) {
            if (item == null) continue;
            if (!player.inventory.addItemStackToInventory(new ItemStack(item))) {
                player.dropItem(new ItemStack(item), false);
            }
            given++;
        }
        send(sender, TextFormatting.GREEN + "Given " + given + " mod items to player!");
    }

    private void assertTrue(ICommandSender sender, String name, boolean condition) {
        if (condition) {
            passed++;
        } else {
            failed++;
            failures.add(name);
            send(sender, TextFormatting.RED + "FAIL: " + name);
        }
    }

    private void printSummary(ICommandSender sender) {
        send(sender, "");
        send(sender, TextFormatting.YELLOW + "=== Test Results ===");
        send(sender, TextFormatting.GREEN + "PASSED: " + passed);
        if (failed > 0) {
            send(sender, TextFormatting.RED + "FAILED: " + failed);
            send(sender, TextFormatting.RED + "Failures:");
            for (String f : failures) {
                send(sender, TextFormatting.RED + "  - " + f);
            }
        } else {
            send(sender, TextFormatting.GREEN + "All tests passed!");
        }
    }

    private void send(ICommandSender sender, String msg) {
        sender.sendMessage(new TextComponentString(msg));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "drugs", "recipes", "items", "blocks", "give");
        }
        return Collections.emptyList();
    }
}
