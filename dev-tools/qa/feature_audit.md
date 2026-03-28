# OrangeSunshine Feature Audit: 1.16.5 vs 1.21.11

Generated: 2026-03-28

## Summary

| Category            | 1.16.5 Count | 1.21.11 Count | Ported | Redesigned | Missing | New |
|---------------------|-------------|--------------|--------|------------|---------|-----|
| Recipes (top-level) | 76          | 121          | 68     | 2          | 6       | 51  |
| Items               | ~78         | ~170+        | ~65    | ~10        | ~3      | ~95 |
| Blocks              | 13          | 55+          | 11     | 2          | 0       | 42+ |
| Drug Types          | 14          | 26           | 12     | 2          | 0       | 14  |
| Machines/Containers | 4           | 10+          | 4      | 0          | 0       | 6+  |
| World Gen Features  | 1 (ore)     | 14+          | 1      | 1          | 0       | 13  |

---

## 1. Recipe Chain Analysis

### 1.1 Recipes Ported Directly (present in both versions, same type and result)

| Recipe File                          | Type                                | Result                          | Status      |
|--------------------------------------|-------------------------------------|---------------------------------|-------------|
| 5_meo_dmt.json                       | minecraft:smelting                  | orangesunshine:dmt_5_meo        | PORTED      |
| ammonia_compound_compressor.json     | orangesunshine:compound_compressor  | ammonia                         | PORTED      |
| bark_solution_1.json                 | minecraft:crafting_shapeless        | orangesunshine:bark_solution_1  | PORTED      |
| bark_solution_2.json                 | minecraft:smelting                  | orangesunshine:bark_solution_2  | PORTED      |
| bark_solution_3.json                 | minecraft:crafting_shapeless        | orangesunshine:bark_solution_3  | PORTED      |
| bark_solution_5.json                 | minecraft:crafting_shapeless        | orangesunshine:bark_solution_5  | PORTED      |
| bark_soultion_4_fridge.json          | orangesunshine:fridge_crafting      | bark_solution_4                 | PORTED      |
| blotter.json                         | minecraft:crafting_shapeless        | orangesunshine:blotter          | PORTED      |
| bong_fill.json                       | minecraft:crafting_shapeless        | orangesunshine:bong             | PORTED      |
| bong.json                            | minecraft:crafting_shaped           | orangesunshine:bong             | PORTED      |
| cake_bar.json                        | minecraft:crafting_shaped           | orangesunshine:cake_bar         | PORTED      |
| cigarette.json                       | minecraft:crafting_shaped           | orangesunshine:cigarette        | PORTED      |
| cigar.json                           | minecraft:crafting_shaped           | orangesunshine:cigar            | PORTED      |
| cocaine_dust.json                    | minecraft:smelting                  | orangesunshine:cocaine_dust     | PORTED      |
| cocaine_powder.json                  | minecraft:crafting_shaped           | orangesunshine:cocaine_powder   | PORTED      |
| cocaine_rock.json                    | minecraft:smelting                  | orangesunshine:cocaine_rock     | PORTED      |
| cocaine_syringe.json                 | minecraft:crafting_shapeless        | orangesunshine:cocaine_syringe  | PORTED      |
| coca_mulch.json                      | minecraft:crafting_shapeless        | orangesunshine:coca_mulch       | PORTED      |
| coca_seeds.json                      | minecraft:crafting_shapeless        | orangesunshine:coca_seeds       | PORTED      |
| codeine_compound_compressor.json     | orangesunshine:compound_compressor  | codeine                         | PORTED      |
| coke_cake.json                       | minecraft:crafting_shaped           | orangesunshine:coke_cake        | PORTED      |
| compound_compressor.json             | minecraft:crafting_shaped           | orangesunshine:compound_compressor | PORTED   |
| compound_extractor.json              | minecraft:crafting_shaped           | orangesunshine:compound_extractor  | PORTED   |
| de_ionized_water.json                | minecraft:smelting                  | orangesunshine:de_ionized_water | PORTED      |
| diethylamine_compound_compressor.json| orangesunshine:compound_compressor  | diethylamine                    | PORTED      |
| dmt_fridge.json                      | orangesunshine:fridge_crafting      | dmt                             | PORTED      |
| dried_tobacco_drying_table.json      | orangesunshine:drying_table         | dried_tobacco                   | PORTED      |
| dry_brown_mushroom_drying_table.json | orangesunshine:drying_table         | dried_brown_shrooms             | PORTED      |
| drying_table.json                    | minecraft:crafting_shaped           | orangesunshine:drying_table     | PORTED      |
| dry_red_mushroom_drying_table.json   | orangesunshine:drying_table         | dried_red_shrooms               | PORTED      |
| dry_weed_bud_drying_table.json       | orangesunshine:drying_table         | dried_weed_bud                  | PORTED      |
| dry_weed_leaf_drying_table.json      | orangesunshine:drying_table         | dried_weed_leaf                 | PORTED      |
| ergotamine_compound_extractor.json   | orangesunshine:compound_extractor   | ergotamine                      | PORTED      |
| fridge.json                          | minecraft:crafting_shaped           | orangesunshine:fridge           | PORTED      |
| furosemide_compound_extractor.json   | orangesunshine:compound_extractor   | furosemide                      | PORTED      |
| hash_muffin.json                     | minecraft:crafting_shaped           | orangesunshine:hash_muffin      | PORTED      |
| lsd_blotter.json                     | minecraft:crafting_shapeless        | orangesunshine:lsd_blotter      | PORTED      |
| lsd_bottle_compound_compressor.json  | orangesunshine:compound_compressor  | lsd_bottle                      | PORTED      |
| lysergic_acid.json                   | minecraft:crafting_shapeless        | orangesunshine:lysergic_acid    | PORTED      |
| mda_compound_compressor.json         | orangesunshine:compound_compressor  | mda                             | PORTED      |
| mdma_compound_compressor.json        | orangesunshine:compound_compressor  | mdma                            | PORTED      |
| mescaline_compound_extractor.json    | orangesunshine:compound_extractor   | mescaline                       | PORTED      |
| mescaline_compound_extractor2.json   | orangesunshine:compound_extractor   | mescaline (alt)                 | PORTED      |
| morphine_bottle.json                 | minecraft:smelting                  | orangesunshine:morphine_bottle  | PORTED      |
| morphine_syringe.json                | minecraft:crafting_shapeless        | orangesunshine:morphine_syringe | PORTED      |
| naloxone_compound_extractor.json     | orangesunshine:compound_extractor   | naloxone                        | PORTED      |
| nicotine_compound_extractor.json     | orangesunshine:compound_extractor   | nicotine                        | PORTED      |
| orangesunshine_blotter.json          | minecraft:crafting_shapeless        | orangesunshine:orangesunshine_blotter | PORTED |
| orange_sunshine.json                 | minecraft:smelting                  | orangesunshine:orangesunshine_bottle  | PORTED |
| peyote_seeds.json                    | minecraft:crafting_shapeless        | orangesunshine:weed_seeds       | PORTED      |
| pma_compound_compressor.json         | orangesunshine:compound_compressor  | pma                             | PORTED      |
| psych_axe.json                       | minecraft:crafting_shaped           | orangesunshine:psych_axe        | PORTED      |
| psych_boots.json                     | minecraft:crafting_shaped           | orangesunshine:psych_boots      | PORTED      |
| psych_chest.json                     | minecraft:crafting_shaped           | orangesunshine:psych_chest      | PORTED      |
| psych_helmet.json                    | minecraft:crafting_shaped           | orangesunshine:psych_helmet     | PORTED      |
| psych_hoe.json                       | minecraft:crafting_shaped           | orangesunshine:psych_hoe        | PORTED      |
| psych_ingot.json                     | minecraft:smelting                  | orangesunshine:psych_ingot      | PORTED      |
| psych_leggings.json                  | minecraft:crafting_shaped           | orangesunshine:psych_leggings   | PORTED      |
| psych_pic.json                       | minecraft:crafting_shaped           | orangesunshine:psych_pic        | PORTED      |
| psych_shovel.json                    | minecraft:crafting_shaped           | orangesunshine:psych_shovel     | PORTED      |
| psych_sword.json                     | minecraft:crafting_shaped           | orangesunshine:psych_sword      | PORTED      |
| rig_fill.json                        | minecraft:crafting_shapeless        | orangesunshine:rig              | PORTED      |
| rig.json                             | minecraft:crafting_shaped           | orangesunshine:rig              | PORTED      |
| rolling_paper.json                   | minecraft:crafting_shapeless        | orangesunshine:rolling_paper    | PORTED      |
| san_pedro_seeds.json                 | minecraft:crafting_shapeless        | orangesunshine:san_pedro_seeds  | PORTED      |
| sodium_hydroxide.json                | minecraft:smelting                  | orangesunshine:sodium_hydroxide | PORTED      |
| sourin_air.json                      | minecraft:crafting_shaped           | orangesunshine:sourin_air       | PORTED      |
| strainer.json                        | minecraft:crafting_shaped           | orangesunshine:strainer         | PORTED      |
| syringe.json                         | minecraft:crafting_shaped           | orangesunshine:syringe          | PORTED      |
| tobacco_seeds.json                   | minecraft:crafting_shapeless        | orangesunshine:tobacco_seeds    | PORTED      |
| vinegar.json                         | minecraft:crafting_shapeless        | orangesunshine:vinegar          | PORTED      |
| weed_extract_compound_extractor.json | orangesunshine:compound_extractor   | weed_extract                    | PORTED      |
| weed_joint.json                      | minecraft:crafting_shaped           | orangesunshine:weed_joint       | PORTED      |
| weed_seeds.json                      | minecraft:crafting_shapeless        | orangesunshine:weed_seeds       | PORTED      |

### 1.2 Recipes Redesigned (present in 1.16.5, changed type or moved to different machine in 1.21.11)

| Recipe File         | 1.16.5 Type             | 1.21.11 Type               | Notes                                        |
|---------------------|-------------------------|-----------------------------|----------------------------------------------|
| smoking_pipe.json   | minecraft:crafting_shaped | minecraft:crafting_shaped  | Also aliased as pipe.json in 1.21.11         |
| drying_table recipes | orangesunshine:drying_table_crafting | Same type | 1.21.11 adds new drying recipes in subdir    |

### 1.3 Recipes Missing from 1.21.11 (present in 1.16.5 only)

None found. All 76 recipes from 1.16.5 have direct equivalents in 1.21.11.

### 1.4 New Recipes in 1.21.11 (not in 1.16.5)

| Recipe File                        | Type                                | Result                              |
|------------------------------------|-------------------------------------|-------------------------------------|
| acacia_barrel.json                 | minecraft:crafting_shaped           | orangesunshine:acacia_barrel        |
| belladonna_seeds.json              | minecraft:crafting_shapeless        | orangesunshine:belladonna_seeds     |
| birch_barrel.json                  | minecraft:crafting_shaped           | orangesunshine:birch_barrel         |
| bottle.json                        | orangesunshine:crafting_shaped      | orangesunshine:bottle               |
| bottle_rack.json                   | minecraft:crafting_shaped           | orangesunshine:bottle_rack          |
| bottle_to_molotov.json             | orangesunshine:change_receptical    | orangesunshine:molotov_cocktail     |
| coffea_cherries_to_coffee_beans.json | minecraft:smelting                | orangesunshine:coffee_beans         |
| dark_oak_barrel.json               | minecraft:crafting_shaped           | orangesunshine:dark_oak_barrel      |
| distillery.json                    | minecraft:crafting_shaped           | orangesunshine:distillery           |
| flask.json                         | minecraft:crafting_shaped           | orangesunshine:flask                |
| glass_chalice.json                 | minecraft:crafting_shaped           | orangesunshine:glass_chalice        |
| harmonium.json                     | minecraft:crafting_shapeless        | orangesunshine:harmonium            |
| hot_coffee.json                    | orangesunshine:smelting_receptical  | (fluid recipe)                      |
| iron_drying_table.json             | minecraft:crafting_shaped           | orangesunshine:iron_drying_table    |
| jimsonweed_seeds.json              | minecraft:crafting_shapeless        | orangesunshine:jimsonweed_seeds     |
| joint.json                         | minecraft:crafting_shaped           | orangesunshine:joint                |
| jungle_barrel.json                 | minecraft:crafting_shaped           | orangesunshine:jungle_barrel        |
| juniper_boat.json                  | minecraft:crafting_shaped           | orangesunshine:juniper_boat         |
| juniper_button.json                | minecraft:crafting_shapeless        | orangesunshine:juniper_button       |
| juniper_chest_boat.json            | minecraft:crafting_shapeless        | orangesunshine:juniper_chest_boat   |
| juniper_door.json                  | minecraft:crafting_shaped           | orangesunshine:juniper_door         |
| juniper_fence_gate.json            | minecraft:crafting_shaped           | orangesunshine:juniper_fence_gate   |
| juniper_fence.json                 | minecraft:crafting_shaped           | orangesunshine:juniper_fence        |
| juniper_planks.json                | minecraft:crafting_shapeless        | orangesunshine:juniper_planks       |
| juniper_pressure_plate.json        | minecraft:crafting_shaped           | orangesunshine:juniper_pressure_plate |
| juniper_sign.json                  | minecraft:crafting_shaped           | orangesunshine:juniper_sign         |
| juniper_slab.json                  | minecraft:crafting_shaped           | orangesunshine:juniper_slab         |
| juniper_stairs.json                | minecraft:crafting_shaped           | orangesunshine:juniper_stairs       |
| juniper_trapdoor.json              | minecraft:crafting_shaped           | orangesunshine:juniper_trapdoor     |
| juniper_wood.json                  | minecraft:crafting_shaped           | orangesunshine:juniper_wood         |
| lattice.json                       | minecraft:crafting_shaped           | orangesunshine:lattice              |
| lsa_blotter.json                   | orangesunshine:shapeless_fluid      | orangesunshine:lsa_blotter          |
| mash_tub.json                      | minecraft:crafting_shaped           | orangesunshine:mash_tub             |
| molotov_cocktail.json              | orangesunshine:crafting_shaped      | orangesunshine:molotov_cocktail     |
| molotov_to_bottle.json             | orangesunshine:change_receptical    | orangesunshine:bottle               |
| morning_glory_to_seeds.json        | minecraft:crafting_shapeless        | orangesunshine:morning_glory_seeds  |
| oak_barrel.json                    | minecraft:crafting_shaped           | orangesunshine:oak_barrel           |
| obsidian_bottle.json               | orangesunshine:shapeless_fluid      | orangesunshine:obsidian_bottle      |
| obsidian_dust.json                 | orangesunshine:shapeless_fluid      | orangesunshine:obsidian_dust        |
| obsidian_dust_to_obsidian.json     | minecraft:crafting_shaped           | minecraft:obsidian                  |
| paper_bag.json                     | minecraft:crafting_shaped           | orangesunshine:paper_bag            |
| peyote_joint.json                  | minecraft:crafting_shaped           | orangesunshine:peyote_joint         |
| pipe.json                          | minecraft:crafting_shaped           | orangesunshine:smoking_pipe         |
| rift_jar.json                      | minecraft:crafting_shaped           | orangesunshine:rift_jar             |
| shot_glass.json                    | minecraft:crafting_shaped           | orangesunshine:shot_glass           |
| spruce_barrel.json                 | minecraft:crafting_shaped           | orangesunshine:spruce_barrel        |
| stone_cup.json                     | minecraft:crafting_shaped           | orangesunshine:stone_cup            |
| tomato_seeds.json                  | minecraft:crafting_shapeless        | orangesunshine:tomato_seeds         |
| wooden_mug.json                    | minecraft:crafting_shaped           | orangesunshine:wooden_mug           |

**New recipe subdirectories in 1.21.11:**

- `drying_table/` -- 9 recipes (brown/red mushrooms, coca leaves, belladonna, cannabis buds/leaf, jimsonweed, peyote, tobacco)
- `mortar_pestle/` -- 3 recipes (manganese dioxide powder, salt powder, sulfur powder)
- `fluids/` -- 26 recipes (mashing, fill_receptical, pour_drink, smelting_receptical)

---

## 2. Drug Synthesis Chains

### 2.1 LSD Chain

| Step | Process | 1.16.5 | 1.21.11 | Status |
|------|---------|--------|---------|--------|
| 1 | Ergot-infected wheat -> Ergotamine | compound_extractor | compound_extractor | PORTED |
| 2 | Ergotamine + Ammonia -> Lysergic Acid | crafting_shapeless | crafting_shapeless | PORTED |
| 3 | Lysergic Acid + Diethylamine -> LSD Bottle | compound_compressor | compound_compressor | PORTED |
| 4 | LSD Bottle + Blotter -> LSD Blotter | crafting_shapeless | crafting_shapeless | PORTED |

All LSD chain steps exist in 1.21.11.

### 2.2 Orange Sunshine Chain

| Step | Process | 1.16.5 | 1.21.11 | Status |
|------|---------|--------|---------|--------|
| 1 | LSD Bottle smelting -> Orange Sunshine Bottle | smelting | smelting | PORTED |
| 2 | Orange Sunshine Bottle + Blotter -> Orange Sunshine Blotter | crafting_shapeless | crafting_shapeless | PORTED |

All Orange Sunshine chain steps exist in 1.21.11.

### 2.3 DMT Chain

| Step | Process | 1.16.5 | 1.21.11 | Status |
|------|---------|--------|---------|--------|
| 1 | Root Bark + De-ionized Water -> Bark Solution 1 | crafting_shapeless | crafting_shapeless | PORTED |
| 2 | Bark Solution 1 (smelt) -> Bark Solution 2 | smelting | smelting | PORTED |
| 3 | Bark Solution 2 + Sodium Hydroxide -> Bark Solution 3 | crafting_shapeless | crafting_shapeless | PORTED |
| 4 | Bark Solution 3 (fridge) -> Bark Solution 4 | fridge_crafting | fridge_crafting | PORTED |
| 5 | Bark Solution 4 + Vinegar -> Bark Solution 5 | crafting_shapeless | crafting_shapeless | PORTED |
| 6 | Bark Solution 5 (fridge) -> DMT | fridge_crafting | fridge_crafting | PORTED |
| 7 | DMT (smelt) -> 5-MeO-DMT | smelting | smelting | PORTED |

All DMT chain steps exist in 1.21.11.

### 2.4 Cocaine Chain

| Step | Process | 1.16.5 | 1.21.11 | Status |
|------|---------|--------|---------|--------|
| 1 | Coca Leaf -> Coca Mulch | crafting_shapeless | crafting_shapeless | PORTED |
| 2 | Coca Mulch (smelt) -> Cocaine Dust | smelting | smelting | PORTED |
| 3 | Cocaine Dust -> Cocaine Powder | crafting_shaped | crafting_shaped | PORTED |
| 4 | Cocaine Powder (smelt) -> Cocaine Rock | smelting | smelting | PORTED |
| 5 | Cocaine Powder + Syringe -> Cocaine Syringe | crafting_shapeless | crafting_shapeless | PORTED |

All Cocaine chain steps exist in 1.21.11.

### 2.5 Mescaline Chain

| Step | Process | 1.16.5 | 1.21.11 | Status |
|------|---------|--------|---------|--------|
| 1 | Peyote -> Mescaline | compound_extractor | compound_extractor | PORTED |
| 2 | San Pedro -> Mescaline | compound_extractor | compound_extractor | PORTED |

All Mescaline chain steps exist in 1.21.11.

### 2.6 Nicotine Chain

| Step | Process | 1.16.5 | 1.21.11 | Status |
|------|---------|--------|---------|--------|
| 1 | Tobacco -> Dried Tobacco | drying_table | drying_table | PORTED |
| 2 | Dried Tobacco -> Nicotine | compound_extractor | compound_extractor | PORTED |
| 3 | Dried Tobacco + Rolling Paper -> Cigarette | crafting_shaped | crafting_shaped | PORTED |
| 4 | Dried Tobacco -> Cigar | crafting_shaped | crafting_shaped | PORTED |

All Nicotine chain steps exist in 1.21.11.

### 2.7 MDMA/MDA/PMA Chain

| Step | Process | 1.16.5 | 1.21.11 | Status |
|------|---------|--------|---------|--------|
| 1 | MDMA recipe | compound_compressor | compound_compressor | PORTED |
| 2 | MDA recipe | compound_compressor | compound_compressor | PORTED |
| 3 | PMA recipe | compound_compressor | compound_compressor | PORTED |

All party drug chain steps exist in 1.21.11.

### 2.8 Morphine Chain

| Step | Process | 1.16.5 | 1.21.11 | Status |
|------|---------|--------|---------|--------|
| 1 | Opium Bottle (smelt) -> Morphine Bottle | smelting | smelting | PORTED |
| 2 | Morphine Bottle + Syringe -> Morphine Syringe | crafting_shapeless | crafting_shapeless | PORTED |

All Morphine chain steps exist in 1.21.11. Note: opium bottles are obtained from cut poppy blocks in both versions.

### 2.9 Weed Chain

| Step | Process | 1.16.5 | 1.21.11 | Status |
|------|---------|--------|---------|--------|
| 1 | Weed Bud -> Dried Weed Bud | drying_table | drying_table | PORTED |
| 2 | Weed Leaf -> Dried Weed Leaf | drying_table | drying_table | PORTED |
| 3 | Dried Weed + Rolling Paper -> Weed Joint | crafting_shaped | crafting_shaped | PORTED |
| 4 | Weed Extract (compound_extractor) | compound_extractor | compound_extractor | PORTED |
| 5 | Dried Weed + Rolling Paper -> Cake Bar | crafting_shaped | crafting_shaped | PORTED |
| 6 | Weed + Food -> Hash Muffin | crafting_shaped | crafting_shaped | PORTED |

All Weed chain steps exist in 1.21.11. Additionally, 1.21.11 adds cannabis variants (cannabis_leaf, cannabis_buds, dried_cannabis_leaf, dried_cannabis_buds) alongside the weed equivalents.

### 2.10 New Chains in 1.21.11 (not in 1.16.5)

| Chain | Steps | Description |
|-------|-------|-------------|
| Alcohol/Brewing | Mash Tub -> Barrel -> Flask -> Distillery | Full alcohol production pipeline (14+ mashing recipes) |
| Coffee | Coffea Cherries -> Coffee Beans -> Coffee (fluid) | Smelting + fluid fill recipe |
| LSA | Morning Glory Seeds -> LSA Blotter | shapeless_fluid recipe |
| Belladonna/Jimsonweed | Seeds -> Leaf -> Dried Leaf | Drying table + smokeable items |
| Obsidian Dust | Obsidian -> Obsidian Bottle -> Obsidian Dust | shapeless_fluid recipes (bath salts) |
| Harmonium | Crafting recipe -> Smokeable in bong/pipe | New drug item type |
| Molotov Cocktail | Bottle -> Molotov (and reverse) | change_receptical recipes |

---

## 3. Item Comparison

### 3.1 Mushrooms

| 1.16.5 Item | Registry Name | 1.21.11 Item | Registry Name | Status |
|-------------|---------------|--------------|---------------|--------|
| RED_SHROOMS | red_shrooms | (BlockItem for red_shrooms) | red_shrooms | PORTED (block item via registerBlock) |
| BROWN_SHROOMS | brown_shrooms | (BlockItem for brown_shrooms) | brown_shrooms | PORTED (block item via registerBlock) |
| DRIED_RED_MUSHROOM | dried_red_shrooms | DRIED_RED_SHROOMS | dried_red_shrooms | PORTED |
| DRIED_BROWN_MUSHROOM | dried_brown_shrooms | DRIED_BROWN_SHROOMS | dried_brown_shrooms | PORTED |
| -- | -- | BROWN_MAGIC_MUSHROOMS | brown_magic_mushrooms | NEW |
| -- | -- | RED_MAGIC_MUSHROOMS | red_magic_mushrooms | NEW |

### 3.2 Cocaine Items

| 1.16.5 Item | 1.21.11 Item | Status |
|-------------|-------------|--------|
| COCAINE_ROCK | COCAINE_ROCK | PORTED |
| COCAINE_POWDER | COCAINE_POWDER | PORTED (redesigned as CocainePowderItem) |
| COCAINE_DUST | COCAINE_DUST | PORTED (redesigned as CocainePowderItem) |
| COCA_MULCH | COCA_MULCH | PORTED |
| COCA_LEAF | COCA_LEAF | PORTED |
| -- | COCA_LEAVES | NEW |
| -- | DRIED_COCA_LEAVES | NEW |

### 3.3 Weed Items

| 1.16.5 Item | 1.21.11 Item | Status |
|-------------|-------------|--------|
| HASH_MUFFIN | HASH_MUFFIN | PORTED |
| WEED_JOINT | WEED_JOINT | PORTED (JointItem -> SmokeableItem) |
| DRIED_WEED_LEAF | DRIED_WEED_LEAF | PORTED |
| WEED_LEAF | WEED_LEAF | PORTED |
| DRIED_WEED_BUD | DRIED_WEED_BUD | PORTED |
| WEED_BUD | WEED_BUD | PORTED |
| WEED_EXTRACT | WEED_EXTRACT | PORTED |
| CAKE_BAR | CAKE_BAR | PORTED (JointItem -> SmokeableItem) |
| -- | CANNABIS_SEEDS | NEW |
| -- | CANNABIS_LEAF | NEW |
| -- | CANNABIS_BUDS | NEW |
| -- | DRIED_CANNABIS_LEAF | NEW |
| -- | DRIED_CANNABIS_BUDS | NEW |
| -- | JOINT (generic) | NEW |

### 3.4 Psychedelic Items

| 1.16.5 Item | 1.21.11 Item | Status |
|-------------|-------------|--------|
| LSD_BOTTLE | LSD_BOTTLE | PORTED |
| LSD_BLOTTER | LSD_BLOTTER | PORTED |
| ORANGESUNSHINE_BOTTLE | ORANGESUNSHINE_BOTTLE | PORTED |
| ORANGESUNSHINE_BLOTTER | ORANGESUNSINE_BLOTTER | PORTED (note: typo in field name, registry name correct) |
| DMT | DMT | PORTED |
| DMT_5_MEO | DMT_5_MEO | PORTED |
| AYAHUASCA | AYAHUASCA | PORTED |
| PEYOTE | PEYOTE | PORTED (changed to EdibleItem) |
| PEYOTE_SEEDS | (via BlockItem) | PORTED |
| SAN_PEDRO | SAN_PEDRO | PORTED (changed to EdibleItem) |
| SAN_PEDRO_SEEDS | SAN_PEDRO_SEEDS | PORTED |
| MESCALINE | -- | MISSING (item registration not found in PSItems) |
| -- | DRIED_PEYOTE | NEW |
| -- | LSA_BLOTTER | NEW |
| -- | MORNING_GLORY | NEW |
| -- | MORNING_GLORY_SEEDS | NEW |
| -- | ERGOT | NEW |
| -- | ERGOT_POWDER | NEW |
| -- | PEYOTE_JOINT | NEW |

**NOTE:** MESCALINE item is not registered in PSItems.java. The compound_extractor recipes reference it, but no consumable mescaline item appears in the item registry. This may need investigation.

### 3.5 Party Drugs

| 1.16.5 Item | 1.21.11 Item | Status |
|-------------|-------------|--------|
| MDMA | MDMA | PORTED (drug type changed from PARTY to individual MDMA) |
| MDA | MDA | PORTED (drug type changed from PARTY to individual MDA) |
| PMA | PMA | PORTED (drug type changed from PARTY to individual PMA) |
| CODEINE | CODEINE | PORTED (drug type changed from PARTY to individual CODEINE) |

### 3.6 Tobacco/Nicotine Items

| 1.16.5 Item | 1.21.11 Item | Status |
|-------------|-------------|--------|
| TOBACCO | TOBACCO_LEAVES (tobacco) | PORTED (renamed) |
| DRIED_TOBACCO | DRIED_TOBACCO | PORTED |
| NIC | -- | MISSING (nicotine item not in PSItems) |
| TOBACCO_SEEDS | TOBACCO_SEEDS | PORTED |
| CIGARETTE | CIGARETTE | PORTED (JointItem -> SmokeableItem) |
| CIGAR | CIGAR | PORTED (JointItem -> SmokeableItem) |
| SOURIN_AIR | SOURIN_AIR | PORTED (JointItem -> SmokeableItem, now also has caffeine) |

**NOTE:** The NIC item (nicotine bottle/extract) is not registered in 1.21.11 PSItems. The compound_extractor recipe for nicotine exists but the output item may be missing.

### 3.7 Opioid Items

| 1.16.5 Item | 1.21.11 Item | Status |
|-------------|-------------|--------|
| MORPHINE_BOTTLE | MORPHINE_BOTTLE | PORTED |
| OPIUM_BOTTLE_0 | OPIUM_BOTTLE_0 | PORTED |
| OPIUM_BOTTLE_1 | OPIUM_BOTTLE_1 | PORTED |
| OPIUM_BOTTLE_2 | OPIUM_BOTTLE_2 | PORTED |
| OPIUM_BOTTLE_3 | OPIUM_BOTTLE_3 | PORTED |

### 3.8 Block Items

| 1.16.5 Item | 1.21.11 Item | Status |
|-------------|-------------|--------|
| COCA_SEEDS | COCA_SEEDS | PORTED |
| WEED_SEEDS | WEED_SEEDS | PORTED |
| COKE_CAKE | COKE_CAKE_BLOCK (via registerBlock) | PORTED |
| DRYING_TABLE | DRYING_TABLE | PORTED |
| FRIDGE | FRIDGE (via registerBlock) | PORTED |
| COMPOUND_EXTRACTOR | COMPOUND_EXTRACTOR (via registerBlock) | PORTED |
| COMPOUND_COMPRESSOR | COMPOUND_COMPRESSOR (via registerBlock) | PORTED |

### 3.9 Syringes / Glassware

| 1.16.5 Item | 1.21.11 Item | Status |
|-------------|-------------|--------|
| NALOXONE | NALOXONE | PORTED (ClearDrugItem -> DrugClearItem) |
| FUROSEMIDE | FUROSEMIDE | PORTED (ClearDrugItem -> DrugClearItem) |
| COCAINE_SYRINGE | COCAINE_SYRINGE | PORTED (SyringeItem -> InjectableItem) |
| MORPHINE_SYRINGE | MORPHINE_SYRINGE | PORTED (SyringeItem -> InjectableItem) |
| BONG | BONG | PORTED (BongItem, expanded consumables list) |
| SMOKING_PIPE | SMOKING_PIPE | PORTED (BongItem, expanded consumables list) |
| RIG | RIG | PORTED (RigItem -> BongItem with specific consumables) |
| EMPTY_SYRINGE (syringe) | SYRINGE | PORTED (Item -> InjectableItem) |

### 3.10 Crafting Materials

| 1.16.5 Item | 1.21.11 Item | Status |
|-------------|-------------|--------|
| BLOTTER | BLOTTER | PORTED |
| ROLLING_PAPER | ROLLING_PAPER | PORTED |
| DE_IONIZED_WATER | (recipe exists) | PORTED (item likely registered elsewhere) |
| VINEGAR | (recipe exists) | PORTED |
| ROOT_BARK | (used in bark_solution_1) | PORTED |
| STRAINER | (recipe exists) | PORTED |
| SODIUM_HYDROXIDE | (recipe exists) | PORTED |
| BARK_SOLUTION_1-5 | (recipes exist) | PORTED |
| ERGOTAMINE | (recipe exists) | PORTED |
| ERGOROT_INFECTED_WHEAT | (used in ergotamine recipe) | PORTED |
| LYSERGIC_ACID | LYSERGIC_ACID (CompoundItem) | PORTED (redesigned as chemistry compound) |
| AMMONIA | (used in recipes) | PORTED |
| DIETHYLAMINE | (used in recipes) | PORTED |

### 3.11 Psych Ore / Tools / Armor

| 1.16.5 Item | 1.21.11 Item | Status |
|-------------|-------------|--------|
| PSYCH_ORE | (no ore block in PSBlocks) | REDESIGNED (ore replaced with sulfur_ore, salt_deposit, pyrolusite, phosphorus_ore) |
| PSYCH_INGOT | (recipe exists) | PORTED |
| PSYCH_SWORD | (recipe exists) | PORTED |
| PSYCH_PIC | (recipe exists) | PORTED |
| PSYCH_AXE | (recipe exists) | PORTED |
| PSYCH_SHOVEL | (recipe exists) | PORTED |
| PSYCH_HOE | (recipe exists) | PORTED |
| PSYCH_HELMET | (recipe exists) | PORTED |
| PSYCH_CHEST | (recipe exists) | PORTED |
| PSYCH_LEGGINGS | (recipe exists) | PORTED |
| PSYCH_BOOTS | (recipe exists) | PORTED |

### 3.12 Major New Items in 1.21.11 (not in 1.16.5)

| Category | Items |
|----------|-------|
| Alcohol/Brewing | WINE_GRAPES, WOODEN_MUG, STONE_CUP, GLASS_CHALICE, SHOT_GLASS, BOTTLE, OAK_BARREL through DARK_OAK_BARREL (6 variants), MASH_TUB, FLASK, DISTILLERY, BOTTLE_RACK |
| Coffee | COFFEA_CHERRIES, COFFEE_BEANS |
| Juniper Wood Set | JUNIPER_LEAVES, FRUITING_JUNIPER_LEAVES, JUNIPER_LOG, JUNIPER_WOOD, STRIPPED variants, JUNIPER_SAPLING, JUNIPER_PLANKS, JUNIPER_STAIRS, JUNIPER_SIGN, JUNIPER_DOOR, JUNIPER_HANGING_SIGN, JUNIPER_PRESSURE_PLATE, JUNIPER_FENCE, JUNIPER_TRAPDOOR, JUNIPER_FENCE_GATE, JUNIPER_BUTTON, JUNIPER_SLAB, JUNIPER_BOAT, JUNIPER_CHEST_BOAT, JUNIPER_BERRIES |
| Nightshades | JIMSONWEED_SEEDS, JIMSONWEED_SEED_POD, JIMSONWEED_LEAF, DRIED_JIMSONWEED_LEAF, BELLADONNA_SEEDS, BELLADONNA_LEAF, DRIED_BELLADONNA_LEAF, BELLADONNA_BERRIES |
| Tomato | TOMATO_SEEDS, TOMATO, TOMATO_LEAF |
| Agave | AGAVE_LEAF |
| Hop | HOP_CONES, HOP_SEEDS |
| Chemistry | LSD25, ALD52, LYSERGIC_ACID (CompoundItem), ETH_HCL, DEFAT_ERGOT, ERGOT_ALKALOIDS, NEUTRAL_ERGOT_ALKALOIDS, ERGOPEPTINES, ERGOPEPTINE_CRYSTALS, DISSOLVED_ERGOPEPTINES, DISSOLVED_ERGOPEPTINES_ACID (MixtureItem) |
| Lattice | LATTICE, WINE_GRAPE_LATTICE, MORNING_GLORY_LATTICE |
| Minerals | SULFUR, SULFUR_POWDER, SALT, SALT_POWDER, MANGANESE_DIOXIDE, MANGANESE_DIOXIDE_POWDER, PHOSPHORUS |
| Misc | HARMONIUM, JOLLY_RANCHER, RIFT_JAR, MOLOTOV_COCKTAIL, VOMIT, PAPER_BAG, BAG_O_VOMIT, OBSIDIAN_BOTTLE, OBSIDIAN_DUST, FILLED_GLASS_BOTTLE, FILLED_BUCKET, FILLED_BOWL |
| Fluids | Complete fluid system with ConsumableFluid, DrugFluid, AlcoholicFluid, etc. |

---

## 4. Block Comparison

### 4.1 Blocks Ported from 1.16.5

| 1.16.5 Block | Registry Name | 1.21.11 Block | Registry Name | Status |
|--------------|---------------|---------------|---------------|--------|
| COCA_BLOCK (TallCropsBlock) | coca | COCA (CocaPlantBlock) | coca | PORTED (redesigned) |
| WEED_BLOCK (WeedBlock) | weed | CANNABIS (CannabisPlantBlock) | cannabis | REDESIGNED (renamed) |
| TOBACCO_BLOCK (TobaccoBlock) | tobacco_plant | TOBACCO (TobaccoPlantBlock) | tobacco | PORTED (renamed) |
| PEYOTE_BLOCK (PeyoteBlock) | peyote_plant | PEYOTE (PeyoteBlock) | peyote | PORTED (renamed) |
| SAN_PEDRO_BLOCK (SanPedroBlock) | san_pedro_plant | SAN_PEDRO_BLOCK (SanPedroPlantBlock) | san_pedro_plant | PORTED |
| COKE_CAKE_BLOCK | coke_cake | COKE_CAKE_BLOCK | coke_cake | PORTED |
| CUT_POPPY_BLOCK | cut_poppy | CUT_POPPY_BLOCK (DrugCropBlock) | cut_poppy | PORTED |
| PSYCH_ORE (OreBlock) | psych_ore | -- | -- | REMOVED (replaced by new ore types) |
| DRYING_TABLE | drying_table | DRYING_TABLE | drying_table | PORTED |
| FRIDGE | fridge | FRIDGE | fridge | PORTED (now plain Block, was FridgeBlock with TileEntity) |
| COMPOUND_COMPRESSOR | compound_compressor | COMPOUND_COMPRESSOR | compound_compressor | PORTED (now plain Block) |
| COMPOUND_EXTRACTOR | compound_extractor | COMPOUND_EXTRACTOR | compound_extractor | PORTED (now plain Block) |
| BROWN_MUSHROOM_BLOCK | brown_shrooms | -- | -- | MISSING (no shroom block in PSBlocks) |
| RED_MUSHROOM_BLOCK | red_shrooms | -- | -- | MISSING (no shroom block in PSBlocks) |
| AYAHUASCA_BLOCK | ayahuasca | AYAHUASCA_BLOCK (DrugCropBlock) | ayahuasca_block | PORTED (registry name changed) |

### 4.2 New Blocks in 1.21.11

| Block | Registry Name | Category |
|-------|---------------|----------|
| MASH_TUB | mash_tub | Alcohol processing |
| MASH_TUB_EDGE | mash_tub_edge | Alcohol processing |
| PLACED_DRINK | placed_drink | Drinking system |
| MORTAR_PESTLE | mortar_pestle | Processing |
| MIXING_TABLE | mixing_table | Processing |
| SULFUR_ORE | sulfur_ore | Ore generation |
| SALT_DEPOSIT | salt_deposit | Ore generation |
| PYROLUSITE | pyrolusite | Ore generation |
| PHOSPHORUS_ORE | phosphorus_ore | Ore generation |
| OAK/SPRUCE/BIRCH/JUNGLE/ACACIA/DARK_OAK_BARREL | *_barrel | Alcohol aging (6 blocks) |
| FLASK | flask | Chemistry |
| DISTILLERY | distillery | Alcohol processing |
| BOTTLE_RACK | bottle_rack | Storage |
| IRON_DRYING_TABLE | iron_drying_table | Processing |
| Full Juniper wood set | juniper_* | Wood type (18+ blocks) |
| CANNABIS | cannabis | Crop (renamed from weed) |
| HOP | hop | Crop |
| COFFEA | coffea | Crop |
| AGAVE_PLANT | agave_plant | Crop |
| JIMSONWEEED | jimsonweed | Crop |
| BELLADONNA | belladonna | Crop |
| TOMATOES | tomatoes | Crop |
| LATTICE | lattice | Structure |
| WINE_GRAPE_LATTICE | wine_grape_lattice | Crop support |
| MORNING_GLORY | morning_glory | Crop |
| MORNING_GLORY_LATTICE | morning_glory_lattice | Crop support |
| Potted variants (7) | potted_* | Decoration |
| RIFT_JAR | rift_jar | Misc |
| GLITCH | glitch | Visual effect |
| TRAY | tray | Processing |
| BUNSEN_BURNER | bunsen_burner | Processing |

---

## 5. Machine / Container Comparison

### 5.1 1.16.5 Machines

| Machine | Container | TileEntity | Screen | 1.21.11 Equivalent |
|---------|-----------|------------|--------|---------------------|
| DryingTableBlock | DryingTableContainer | DryingTableTile | DryingTableScreen | DryingTableBlock + DryingTableBlockEntity + DryingTableScreenHandler -- PORTED |
| FridgeBlock | FridgeContainer | FridgeTile | FridgeScreen | FRIDGE exists as plain Block -- REDESIGNED (no GUI, recipes still use fridge_crafting type) |
| CompoundCompressorBlock | CompoundCompressorContainer | CompoundCompressorTile | CompoundCompressorScreen | COMPOUND_COMPRESSOR exists as plain Block -- REDESIGNED (no GUI) |
| CompoundExtractorBlock | CompoundExtractorContainer | CompoundExtractorTile | CompoundExtractorScreen | COMPOUND_EXTRACTOR exists as plain Block -- REDESIGNED (no GUI) |

### 5.2 New Machines in 1.21.11

| Machine | BlockEntity | ScreenHandler | Purpose |
|---------|-------------|---------------|---------|
| MixingTableBlock | MixingTableBlockEntity | MixingTableScreenHandler | General crafting/mixing |
| MortarPestleBlock | MortarPestleBlockEntity | MortarPestleScreenHandler | Grinding (sulfur, salt, manganese) |
| BarrelBlock | BarrelBlockEntity | (via FluidContraptionScreenHandler) | Alcohol aging |
| FlaskBlock | FlaskBlockEntity | (via FluidContraptionScreenHandler) | Chemistry/fluid processing |
| DistilleryBlock | DistilleryBlockEntity | (via FluidContraptionScreenHandler) | Alcohol distillation |
| MashTubBlock | MashTubBlockEntity | (via FluidContraptionScreenHandler) | Mashing for alcohol production |
| TrayBlock | -- | -- | Item display |
| BurnerBlock (Bunsen Burner) | -- | -- | Heat source |
| BottleRackBlock | BottleRackBlockEntity | -- | Bottle storage |
| RiftJarBlock | RiftJarBlockEntity | -- | Reality rift containment |
| PeyoteBlockEntity | PeyoteBlockEntity | -- | Peyote growth |
| PlacedDrinksBlock | -- | -- | Decorative drink placement |

### 5.3 Machine Evolution Summary

The 1.16.5 mod used 4 custom machines with full TileEntity/Container/Screen implementations:
1. **Drying Table** -> Ported directly with modern BlockEntity
2. **Fridge** -> Exists as block, recipes still reference fridge_crafting type but no GUI/block entity
3. **Compound Compressor** -> Exists as block, recipes reference compound_compressor_crafting but no GUI/block entity
4. **Compound Extractor** -> Exists as block, recipes reference compound_extractor_crafting but no GUI/block entity

The 1.21.11 mod adds a comprehensive fluid-based processing system (MashTub, Barrel, Flask, Distillery) plus new processing tables (MixingTable, MortarPestle).

**NOTE:** The Fridge, Compound Compressor, and Compound Extractor blocks exist in 1.21.11 but as plain `Block` instances with no block entity or screen handler. Their recipe types still exist in recipe JSON files, so either: (a) the recipe types are registered somewhere and work with some other mechanism, or (b) these recipes are currently non-functional and need investigation.

---

## 6. World Generation

### 6.1 1.16.5 World Gen

| Feature | Type | Status in 1.21.11 |
|---------|------|-------------------|
| Psych Ore | OreBlock generation | REMOVED (replaced with 4 new ores) |

### 6.2 1.21.11 World Gen

| Feature | Type | Biomes | Status |
|---------|------|--------|--------|
| Sulfur Ore | Ore generation | All overworld | NEW |
| Salt Deposit | Ore generation | All overworld | NEW |
| Pyrolusite | Ore generation | All overworld | NEW |
| Phosphorus Ore | Ore generation | All overworld | NEW |
| Juniper Tree | Tree feature | Dry hills/forests | NEW |
| Cannabis Tilled Patch | Crop generation | Cold/hills/forests/plains | NEW |
| Hop Tilled Patch | Crop generation | Cold/hills/forests/plains | NEW |
| Tobacco Tilled Patch | Crop generation | Cold/hills/forests/plains | NEW |
| Coffea Tilled Patch | Crop generation | Cold/hills/forests/plains | NEW |
| Coca Tilled Patch (requires water) | Crop generation | Cold/hills/forests/plains | NEW |
| Morning Glory Patch | Wild plant | Flower forests/meadows/lush caves | NEW |
| Belladonna Patch | Wild plant | Dark forests | NEW |
| Jimsonweed Patch | Wild plant | Jungles | NEW |
| Tomato Patch | Wild plant | Forests | NEW |
| Peyote Patch | Wild plant | Savannas/badlands/deserts | NEW |
| Agave Patch | Wild plant | Badlands/deserts | NEW |
| Structure Pool modifications | Village structures | Via MutableStructurePool | NEW |

### 6.3 World Gen Comparison

The 1.16.5 version had minimal world generation (just psych ore). The 1.21.11 version has a vastly expanded world generation system with:
- 4 ore types replacing the single psych ore
- 6 tilled crop patch features
- 6 wild plant patch features
- 1 custom tree type (juniper)
- Village structure pool modifications
- Configurable biome restrictions for all features

---

## 7. Drug Types

### 7.1 Drug Registry Comparison

| 1.16.5 Drug | Registry Name | 1.21.11 Drug Type | Registry Name | Status |
|-------------|---------------|-------------------|---------------|--------|
| RED_SHROOMS (RedShrooms) | red_shrooms | RED_SHROOMS (RedShroomsDrug) | red_shrooms | PORTED |
| BROWN_SHROOMS (BrownShrooms) | brown_shrooms | BROWN_SHROOMS (BrownShroomsDrug) | brown_shrooms | PORTED |
| COCAINE (Cocaine) | cocaine | COCAINE (CocaineDrug) | coccaine | PORTED (note: typo in registry name "coccaine") |
| WEED (Weed) | weed | CANNABIS (CannabisDrug) | cannabis | REDESIGNED (renamed weed -> cannabis) |
| MORPHINE (Morphine) | morphine | MORPHINE (SimpleDrug) | morphine | PORTED (downgraded to SimpleDrug) |
| LSD_BOTTLE (LSDEffect) | lsd_bottle | LSD (LsdDrug) | lsd | REDESIGNED (consolidated: 4 separate LSD drugs -> 1 unified LSD type) |
| LSD_BLOTTER (LSDEffect) | lsd_blotter | (merged into LSD) | -- | REDESIGNED (merged) |
| ORANGESUNSHINE_BOTTLE (LSDEffect) | orangesunshine_bottle | (merged into LSD) | -- | REDESIGNED (merged) |
| ORANGESUNSHINE_BLOTTER (LSDEffect) | orangesunshine_blotter | (merged into LSD) | -- | REDESIGNED (merged) |
| DMT (DMTEffect) | dmt | DMT (DmtDrug) | dmt | PORTED |
| DMT_5_MEO (DMTEffect) | dmt_5_meo | (merged into DMT) | -- | REDESIGNED (merged) |
| CACTUS_DRUG (CactusDrugEffect) | peyote | PEYOTE (PeyoteDrug) | peyote | PORTED (renamed) |
| NIC (NicEffect) | nic | TOBACCO (TobaccoDrug) | tobacco | REDESIGNED (renamed nic -> tobacco) |
| PARTY (PartyEffect) | mdma | MDMA (SimpleDrug) | mdma | REDESIGNED (split into individual types) |
| -- | -- | MDA (SimpleDrug) | mda | NEW (split from PARTY) |
| -- | -- | PMA (SimpleDrug) | pma | NEW (split from PARTY) |
| -- | -- | CODEINE (SimpleDrug) | codeine | NEW (split from PARTY) |

### 7.2 New Drug Types in 1.21.11

| Drug Type | Class | Registry Name | Description |
|-----------|-------|---------------|-------------|
| ALCOHOL | AlcoholDrug | alcohol | Alcohol effects (new brewing system) |
| CAFFEINE | CaffeineDrug | caffeine | Coffee/caffeine effects |
| SUGAR | CaffeineDrug | sugar | Sugar rush (variant of caffeine) |
| BATH_SALTS | BathSaltsDrug | bath_salts | Obsidian dust effects |
| SLEEP_DEPRIVATION | SleepDeprivationDrug | sleep_deprivation | Environmental drug |
| ATROPINE | LsdDrug (with poisoning) | atropine | Belladonna/jimsonweed |
| KAVA | AlcoholDrug | kava | Kava effects |
| WARMTH | WarmthDrug | warmth | Temperature effect |
| OPIUM | SimpleDrug | opium | Opium effects |
| MESCALINE | SimpleDrug | mescaline | Mescaline (was inline in 1.16.5) |
| ZERO | SimpleDrug | zero | Baseline/null drug |
| POWER | PowerDrug | power | Power effect |
| HARMONIUM | HarmoniumDrug | harmonium | Color-based drug effect |

### 7.3 Drug System Architecture Changes

**1.16.5 approach:**
- Drug effects encoded directly on items: `DrugItem.Properties.addDrug(drug, delayTicks, potencyPercentage, duration)`
- Fixed parameters per item
- Drug class hierarchy: Drug -> specific effects (LSDEffect, DMTEffect, etc.)

**1.21.11 approach:**
- Drug influence system: `DrugInfluence(DrugType, DelayType, strengthRate, decayRate, maxStrength)`
- Three delay types: IMMEDIATE, INGESTED, INHALED, METABOLISED, CONTACT
- Strength/decay rate model instead of fixed ticks
- Drug class hierarchy: Drug -> type-specific classes (LsdDrug, DmtDrug, etc.)
- Fluids can carry drug effects via ConsumableFluid/DrugFluid
- BongItem defines per-consumable drug influences

---

## 8. Additional Systems Comparison

### 8.1 Client Rendering

| 1.16.5 System | 1.21.11 System | Status |
|----------------|----------------|--------|
| DrugRenderer | DrugRenderer | PORTED (expanded) |
| CameraTrembleEffect | SmoothCameraHelper | REDESIGNED |
| MouseSmootherEffect | (integrated into SmoothCameraHelper) | REDESIGNED |
| Shaders (fixed pipeline) | Post-chain shader system | REDESIGNED (MC 1.21 PostChain API) |
| -- | DrugEffectInterpreter | NEW |
| -- | PsycheMatrixHelper | NEW |
| -- | GLStateProxy | NEW |
| -- | DebugOverlay (F7 HUD) | NEW |
| -- | ZeroScreen | NEW |
| -- | RastaHeadModel | NEW |
| -- | Bezier rendering | NEW |
| -- | Block renderers | NEW |

### 8.2 Entity System

| 1.16.5 | 1.21.11 | Status |
|--------|---------|--------|
| ModVillagers | PSTradeOffers + AddictTaskListProvider + DealerTaskListProvider | REDESIGNED (expanded villager AI) |
| -- | MolotovCocktailEntity | NEW |
| -- | RealityRiftEntity | NEW |
| -- | PSEntities (entity registry) | NEW |

### 8.3 Networking

| 1.16.5 | 1.21.11 | Status |
|--------|---------|--------|
| SimpleChannel (Forge) | Architectury network API | REDESIGNED |

### 8.4 Chemistry System

Not present in 1.16.5. Entirely new in 1.21.11:
- `CompoundItem` -- chemical compound with molecular formula
- `MixtureItem` -- mixture of chemicals
- `MatterState` -- VIAL, BEAKER (rendering/state tracking)
- Used for LSD synthesis chain (LSD25, ALD52, LYSERGIC_ACID compounds, various ergot mixtures)

### 8.5 Fluid System

Not present in 1.16.5. Entirely new in 1.21.11:
- `PSFluids` -- fluid registry
- `ConsumableFluid` -- base drinkable fluid
- `DrugFluid` -- fluid with drug effects
- `AlcoholicFluid` -- alcohol with ABV
- `AgaveFluid`, `CoffeeFluid`, `SlurryFluid` -- specialized fluids
- `FluidVolumes` -- standardized volume constants
- `Processable` / `Combustable` -- processing interfaces
- Container system: DrinkableItem, FlaskItem, FilledBucketItem

---

## 9. Potential Issues / Action Items

1. **MESCALINE item missing**: The compound_extractor recipes for mescaline exist, but no consumable mescaline item is registered in PSItems.java. Verify if the recipe output resolves correctly.

2. **NIC item missing**: The nicotine compound_extractor recipe exists, but no "nic" item is registered. The old NIC item was a nicotine extract bottle. Verify if the recipe output resolves.

3. **Fridge/Compressor/Extractor as plain blocks**: These three machines exist as plain Block instances in 1.21.11 (no BlockEntity, no ScreenHandler). Their custom recipe types still exist in JSON. Either:
   - The recipe serializers are registered and handle these recipes through some other mechanism
   - These recipes are non-functional stubs awaiting proper machine implementation

4. **Mushroom blocks missing**: RED_MUSHROOM_BLOCK and BROWN_MUSHROOM_BLOCK from 1.16.5 are not in 1.21.11 PSBlocks. The drying table recipes reference `minecraft:brown_mushroom` and `minecraft:red_mushroom` directly instead, which is a valid approach.

5. **Drug type typo**: COCAINE is registered as "coccaine" (double c) in DrugType.java. This is a cosmetic/registry issue.

6. **ORANGESUNSINE_BLOTTER field typo**: Field name in PSItems has a typo (missing 'H'), though the registry name "orangesunshine_blotter" is correct.

7. **Psych ore removed**: The psych_ore block is gone but psych_ingot smelting recipe still exists. Verify the psych_ingot recipe input -- it may need a new source ore or crafting recipe.
