/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.entity.BlockEntityTypeSupportHelper;
import moriz.orangesunshine.block.entity.PSBlockEntities;
import moriz.orangesunshine.item.PSItems;
import moriz.orangesunshine.world.gen.PSSaplingGenerators;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

public interface PSBlocks {
    Block MASH_TUB = register("mash_tub", new MashTubBlock(Properties.of().setId(blockKey("mash_tub"))
            .sound(SoundType.WOOD)
            .strength(2)
            .noOcclusion()
            .isSuffocating(BlockConstructionUtils::never)
            .isViewBlocking(BlockConstructionUtils::never)
            .pushReaction(PushReaction.BLOCK)
    ));
    Block MASH_TUB_EDGE = register("mash_tub_edge", new MashTubWallBlock(Properties.ofFullCopy(MASH_TUB).setId(blockKey("mash_tub_edge"))));
    Block PLACED_DRINK = register("placed_drink", new PlacedDrinksBlock(Properties.of().setId(blockKey("placed_drink"))
            .instabreak()
            .noOcclusion()
            .isSuffocating(BlockConstructionUtils::never)
            .isViewBlocking(BlockConstructionUtils::never)
            .pushReaction(PushReaction.DESTROY)
    ));

    Block MORTAR_PESTLE = registerBlock("mortar_pestle", new MortarPestleBlock(Properties.ofFullCopy(Blocks.STONE).setId(blockKey("mortar_pestle")).noOcclusion()));
    Block MIXING_TABLE = registerBlock("mixing_table", new MixingTableBlock(Properties.of().setId(blockKey("mixing_table")).sound(SoundType.COPPER).noOcclusion().strength(1).pushReaction(PushReaction.BLOCK)));

    Block SULFUR_ORE = register("sulfur_ore", new Block(Properties.ofFullCopy(Blocks.COAL_ORE).setId(blockKey("sulfur_ore")).sound(SoundType.STONE)));
    Block SALT_DEPOSIT = register("salt_deposit", new Block(Properties.ofFullCopy(Blocks.COAL_ORE).setId(blockKey("salt_deposit")).sound(SoundType.STONE)));
    Block PYROLUSITE = register("pyrolusite", new Block(Properties.ofFullCopy(Blocks.COAL_ORE).setId(blockKey("pyrolusite")).sound(SoundType.STONE)));
    Block PHOSPHORUS_ORE = register("phosphorus_ore", new Block(Properties.ofFullCopy(Blocks.COAL_ORE).setId(blockKey("phosphorus_ore")).sound(SoundType.STONE)));

    Block OAK_BARREL = register("oak_barrel", BlockConstructionUtils.barrel(MapColor.WOOD, blockKey("oak_barrel")));
    Block SPRUCE_BARREL = register("spruce_barrel", BlockConstructionUtils.barrel(MapColor.PODZOL, blockKey("spruce_barrel")));
    Block BIRCH_BARREL = register("birch_barrel", BlockConstructionUtils.barrel(MapColor.SAND, blockKey("birch_barrel")));
    Block JUNGLE_BARREL = register("jungle_barrel", BlockConstructionUtils.barrel(MapColor.DIRT, blockKey("jungle_barrel")));
    Block ACACIA_BARREL = register("acacia_barrel", BlockConstructionUtils.barrel(MapColor.COLOR_ORANGE, blockKey("acacia_barrel")));
    Block DARK_OAK_BARREL = register("dark_oak_barrel", BlockConstructionUtils.barrel(MapColor.COLOR_BROWN, blockKey("dark_oak_barrel")));

    Block FLASK = register("flask", new FlaskBlock(Properties.of().setId(blockKey("flask")).sound(SoundType.COPPER).strength(1).pushReaction(PushReaction.BLOCK)));
    Block DISTILLERY = register("distillery", new DistilleryBlock(Properties.of().setId(blockKey("distillery")).sound(SoundType.COPPER).strength(1).pushReaction(PushReaction.BLOCK)));
    Block BOTTLE_RACK = register("bottle_rack", new BottleRackBlock(Properties.of().setId(blockKey("bottle_rack")).mapColor(MapColor.WOOD).sound(SoundType.WOOD).strength(0.5F).ignitedByLava()));

    Block DRYING_TABLE = register("drying_table", new DryingTableBlock(Properties.of().setId(blockKey("drying_table")).mapColor(MapColor.WOOD).sound(SoundType.WOOD).strength(2).ignitedByLava()));
    Block IRON_DRYING_TABLE = register("iron_drying_table", new DryingTableBlock(Properties.of().setId(blockKey("iron_drying_table")).mapColor(MapColor.METAL).sound(SoundType.METAL).strength(5)));

    JuniperLeavesBlock JUNIPER_LEAVES = register("juniper_leaves", new JuniperLeavesBlock(BlockConstructionUtils.leaves(SoundType.GRASS).setId(blockKey("juniper_leaves"))));
    JuniperLeavesBlock FRUITING_JUNIPER_LEAVES = register("fruiting_juniper_leaves", new JuniperLeavesBlock(BlockConstructionUtils.leaves(SoundType.GRASS).setId(blockKey("fruiting_juniper_leaves"))));
    Block JUNIPER_LOG = register("juniper_log", BlockConstructionUtils.log(MapColor.COLOR_CYAN, MapColor.COLOR_LIGHT_GRAY, blockKey("juniper_log")));
    Block JUNIPER_WOOD = register("juniper_wood", BlockConstructionUtils.log(MapColor.COLOR_CYAN, MapColor.COLOR_LIGHT_GRAY, blockKey("juniper_wood")));
    Block STRIPPED_JUNIPER_LOG = register("stripped_juniper_log", BlockConstructionUtils.log(MapColor.COLOR_CYAN, MapColor.COLOR_LIGHT_GRAY, blockKey("stripped_juniper_log")));
    Block STRIPPED_JUNIPER_WOOD = register("stripped_juniper_wood", BlockConstructionUtils.log(MapColor.COLOR_CYAN, MapColor.COLOR_LIGHT_GRAY, blockKey("stripped_juniper_wood")));
    Block JUNIPER_SAPLING = register("juniper_sapling", new SaplingBlock(PSSaplingGenerators.JUNIPER, BlockConstructionUtils.plant(SoundType.GRASS).setId(blockKey("juniper_sapling"))));

    Block JUNIPER_PLANKS = register("juniper_planks", new Block(Properties.of().setId(blockKey("juniper_planks")).mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.BASS).strength(2, 3).sound(SoundType.WOOD).ignitedByLava()));
    Block JUNIPER_STAIRS = register("juniper_stairs", new StairBlock(JUNIPER_PLANKS.defaultBlockState(), Properties.ofFullCopy(JUNIPER_PLANKS).setId(blockKey("juniper_stairs"))));
    Block JUNIPER_SIGN = register("juniper_sign", new StandingSignBlock(PSWoodTypes.JUNIPER, Properties.of().setId(blockKey("juniper_sign")).mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.BASS).noCollision().strength(1).sound(SoundType.WOOD)));
    Block JUNIPER_DOOR = register("juniper_door", new DoorBlock(PSWoodTypes.JUNIPER.setType(), Properties.of().setId(blockKey("juniper_door")).mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY)));
    Block JUNIPER_WALL_SIGN = register("juniper_wall_sign", new WallSignBlock(PSWoodTypes.JUNIPER, Properties.of().setId(blockKey("juniper_wall_sign")).mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.BASS).noCollision().strength(1).sound(SoundType.WOOD)));
    Block JUNIPER_HANGING_SIGN = register("juniper_hanging_sign", new CeilingHangingSignBlock(PSWoodTypes.JUNIPER, Properties.of().setId(blockKey("juniper_hanging_sign")).mapColor(MapColor.COLOR_CYAN).instrument(NoteBlockInstrument.BASS).noCollision().strength(1).ignitedByLava()));
    Block JUNIPER_WALL_HANGING_SIGN = register("juniper_wall_hanging_sign", new WallHangingSignBlock(PSWoodTypes.JUNIPER, Properties.of().setId(blockKey("juniper_wall_hanging_sign")).mapColor(MapColor.COLOR_CYAN).instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).ignitedByLava()));
    Block JUNIPER_PRESSURE_PLATE = register("juniper_pressure_plate", new PressurePlateBlock(PSWoodTypes.JUNIPER.setType(), Properties.of().setId(blockKey("juniper_pressure_plate")).mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.BASS).noCollision().strength(0.5F).ignitedByLava().pushReaction(PushReaction.DESTROY)));
    Block JUNIPER_FENCE = register("juniper_fence", new FenceBlock(Properties.of().setId(blockKey("juniper_fence")).mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.BASS).strength(2, 3).sound(SoundType.WOOD).ignitedByLava()));
    Block JUNIPER_TRAPDOOR = register("juniper_trapdoor", new TrapDoorBlock(PSWoodTypes.JUNIPER.setType(), Properties.of().setId(blockKey("juniper_trapdoor")).mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.BASS).strength(3).noOcclusion().isValidSpawn(BlockConstructionUtils::never).ignitedByLava()));
    Block JUNIPER_FENCE_GATE = register("juniper_fence_gate", new FenceGateBlock(PSWoodTypes.JUNIPER, Properties.of().setId(blockKey("juniper_fence_gate")).mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.BASS).strength(2, 3).ignitedByLava()));
    Block JUNIPER_BUTTON = register("juniper_button", BlockConstructionUtils.woodenButton(PSWoodTypes.JUNIPER.setType(), blockKey("juniper_button")));
    Block JUNIPER_SLAB = register("juniper_slab", new SlabBlock(Properties.ofFullCopy(JUNIPER_PLANKS).setId(blockKey("juniper_slab"))));

    DrugCropBlock CANNABIS = register("cannabis", new DrugCropBlock(BlockConstructionUtils.plant(SoundType.GRASS).setId(blockKey("cannabis"))));
    HopPlantBlock HOP = register("hop", new HopPlantBlock(BlockConstructionUtils.plant(SoundType.GRASS).setId(blockKey("hop"))));
    TobaccoPlantBlock TOBACCO = register("tobacco", new TobaccoPlantBlock(BlockConstructionUtils.plant(SoundType.GRASS).setId(blockKey("tobacco"))));
    DrugCropBlock COCA = register("coca", new DrugCropBlock(BlockConstructionUtils.plant(SoundType.GRASS).setId(blockKey("coca"))));
    CoffeaPlantBlock COFFEA = register("coffea", new CoffeaPlantBlock(BlockConstructionUtils.plant(SoundType.GRASS).setId(blockKey("coffea"))));
    PeyoteBlock PEYOTE = register("peyote", new PeyoteBlock(BlockConstructionUtils.plant(SoundType.GRASS).setId(blockKey("peyote"))));
    AgavePlantBlock AGAVE_PLANT = register("agave_plant", new AgavePlantBlock(BlockConstructionUtils.plant(SoundType.GRASS).setId(blockKey("agave_plant"))));
    NightshadeBlock JIMSONWEEED = register("jimsonweed", new NightshadeBlock(
            () -> PSItems.JIMSONWEED_SEED_POD,
            () -> PSItems.JIMSONWEED_LEAF, BlockConstructionUtils.plant(SoundType.GRASS).setId(blockKey("jimsonweed"))));
    NightshadeBlock BELLADONNA = register("belladonna", new NightshadeBlock(
            () -> PSItems.BELLADONNA_BERRIES,
            () -> PSItems.BELLADONNA_LEAF, BlockConstructionUtils.plant(SoundType.GRASS).setId(blockKey("belladonna"))));
    NightshadeBlock TOMATOES = register("tomatoes", new NightshadeBlock(
            () -> PSItems.TOMATO,
            () -> PSItems.TOMATO_LEAF, BlockConstructionUtils.plant(SoundType.GRASS).setId(blockKey("tomatoes"))));

    Block LATTICE = register("lattice", new LatticeBlock(Properties.of().setId(blockKey("lattice")).mapColor(MapColor.WOOD)
            .sound(SoundType.WOOD).strength(0.3F).noOcclusion().ignitedByLava()));
    Block WINE_GRAPE_LATTICE = register("wine_grape_lattice", new BurdenedLatticeBlock(true, null, 1, Properties.of().setId(blockKey("wine_grape_lattice")).mapColor(MapColor.WOOD)
            .sound(SoundType.WOOD).strength(0.3F).randomTicks().noOcclusion().ignitedByLava()
    ));
    Block MORNING_GLORY = register("morning_glory", new VineStemBlock(() -> PSBlocks.MORNING_GLORY_LATTICE, Properties.of().setId(blockKey("morning_glory"))
            .mapColor(MapColor.PLANT)
            .noCollision()
            .instabreak()
            .randomTicks()
            .sound(SoundType.GRASS)
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .ignitedByLava()
    ));
    Block MORNING_GLORY_LATTICE = register("morning_glory_lattice", new BurdenedLatticeBlock(true, MORNING_GLORY, 2, Properties.of().setId(blockKey("morning_glory_lattice"))
            .mapColor(MapColor.WOOD)
            .sound(SoundType.WOOD)
            .strength(0.3F)
            .randomTicks()
            .noOcclusion()
            .ignitedByLava()
    ));

    Block POTTED_MORNING_GLORY = register("potted_morning_glory", BlockConstructionUtils.pottedPlant(MORNING_GLORY, blockKey("potted_morning_glory")));
    Block POTTED_JUNIPER_SAPLING = register("potted_juniper_sapling", BlockConstructionUtils.pottedPlant(JUNIPER_SAPLING, blockKey("potted_juniper_sapling")));
    Block POTTED_CANNABIS = register("potted_cannabis", BlockConstructionUtils.pottedPlant(CANNABIS, blockKey("potted_cannabis")));
    Block POTTED_HOP = register("potted_hop", BlockConstructionUtils.pottedPlant(HOP, blockKey("potted_hop")));
    Block POTTED_TOBACCO = register("potted_tobacco", BlockConstructionUtils.pottedPlant(TOBACCO, blockKey("potted_tobacco")));
    Block POTTED_COCA = register("potted_coca", BlockConstructionUtils.pottedPlant(COCA, blockKey("potted_coca")));
    Block POTTED_COFFEA = register("potted_coffea", BlockConstructionUtils.pottedPlant(COFFEA, blockKey("potted_coffea")));

    Block RIFT_JAR = register("rift_jar", new RiftJarBlock(Properties.of().setId(blockKey("rift_jar")).strength(0.5F).sound(SoundType.GLASS).noOcclusion().pushReaction(PushReaction.DESTROY)));
    Block GLITCH = register("glitch", new GlitchedBlock(Properties.of().setId(blockKey("glitch")).mapColor(MapColor.COLOR_BLACK).instabreak().strength(0)
            .emissiveRendering(BlockConstructionUtils::always)
            .air().noOcclusion().noTerrainParticles().noLootTable()
    ));

    Block TRAY = register("tray", new TrayBlock(Properties.of().setId(blockKey("tray")).mapColor(MapColor.METAL).strength(0.7F).sound(SoundType.METAL).noOcclusion()));
    Block BUNSEN_BURNER = register("bunsen_burner", new BurnerBlock(Properties.of().setId(blockKey("bunsen_burner")).mapColor(MapColor.METAL).strength(0.7F).sound(SoundType.METAL).noOcclusion()));

    Block SAN_PEDRO_BLOCK = registerBlock("san_pedro_plant", new SanPedroPlantBlock(Properties.of().setId(blockKey("san_pedro_plant")).mapColor(MapColor.PLANT).sound(SoundType.WOOL)));
    Block COKE_CAKE_BLOCK = registerBlock("coke_cake", new CokeCakeBlock(Properties.of().setId(blockKey("coke_cake")).mapColor(MapColor.SNOW).sound(SoundType.WOOL)));
    Block CUT_POPPY_BLOCK = registerBlock("cut_poppy", new DrugCropBlock(Properties.of().setId(blockKey("cut_poppy")).mapColor(MapColor.PLANT).sound(SoundType.GRASS).noOcclusion().instabreak()));
    Block AYAHUASCA_BLOCK = registerBlock("ayahuasca_block", new DrugCropBlock(Properties.of().setId(blockKey("ayahuasca_block")).mapColor(MapColor.PLANT).sound(SoundType.GRASS).noOcclusion().instabreak()));
    Block FRIDGE = registerBlock("fridge", new Block(Properties.of().setId(blockKey("fridge")).strength(3.5F).sound(SoundType.METAL)));
    Block COMPOUND_EXTRACTOR = registerBlock("compound_extractor", new Block(Properties.of().setId(blockKey("compound_extractor")).strength(3.5F).sound(SoundType.METAL)));
    Block COMPOUND_COMPRESSOR = registerBlock("compound_compressor", new Block(Properties.of().setId(blockKey("compound_compressor")).strength(3.5F).sound(SoundType.METAL)));

    static ResourceKey<Block> blockKey(String name) {
        return ResourceKey.create(Registries.BLOCK, OrangeSunshine.id(name));
    }

    static <T extends Block> T register(String name, T block) {
        return Registry.register(BuiltInRegistries.BLOCK, OrangeSunshine.id(name), block);
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(BuiltInRegistries.BLOCK, OrangeSunshine.id(name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(BuiltInRegistries.ITEM, OrangeSunshine.id(name),
                new BlockItem(block, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, OrangeSunshine.id(name)))));
    }

    static void bootstrap() {
        PSBlockEntities.bootstrap();
    }
}
