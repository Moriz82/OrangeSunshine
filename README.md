# Orange Sunshine

A Minecraft mod that adds drugs, their production chains, and associated effects. Available for **Fabric**, **Forge**, and **NeoForge**.

> **Current version:** 1.21.11 · alpha
> **Java:** 21

---

## Features

### Substances

| Substance | Source | Consumption |
|---|---|---|
| Cannabis | Grow → dry → roll | Joint, pipe, bong |
| Tobacco | Grow → dry → process | Cigarette, cigar, pipe |
| Coca / Cocaine | Grow → dry → extract | Snort (cocaine powder) |
| LSD / Psychedelics | Chemical synthesis | Vial |
| Peyote | Grow (cactus-like) | Peyote joint |
| Alcohol | Ferment → distill | Flask, bottle, mug |
| Caffeine | Coffee beans | Brewed coffee |
| Kava | Brew | Drink |
| Atropine | Belladonna / Jimsonweed | Extract |
| Bath Salts | Chemical synthesis | Inject |
| Harmonium | Rare / crafted | Special |

### Drug Effects

Each substance has its own influence system with escalating levels:
- **Visual:** motion blur, depth of field, color shifts, lens flare, heat distortion
- **Gameplay:** message distortion, altered movement, hunger/fatigue changes
- **Hallucinations:** entity hallucinations, drifting camera, reality rifts
- **Social:** drug dealers, addicts, and drug-aware villager trades

### Plants & World Generation

New plants spawn naturally in appropriate biomes:
- **Cannabis** — forests, plains, hills
- **Tobacco** — same as cannabis
- **Coca** — jungle (requires water)
- **Coffea** — forest biomes
- **Hops** — forests, hills
- **Morning Glory** — flower forest, meadow, lush caves
- **Belladonna** — dark forest
- **Jimsonweed** — jungle / sparse jungle
- **Peyote** — savanna, badlands, deserts
- **Agave** — badlands, deserts
- **Juniper tree** — dry hills and forests

New ores:
- **Sulfur Ore, Salt Deposit, Pyrolusite, Phosphorus Ore** — overworld underground

### Production Blocks

| Block | Purpose |
|---|---|
| Mortar & Pestle | Grinding raw materials |
| Drying Table / Iron Drying Table | Drying plants |
| Mixing Table | Combining substances |
| Distillery | Distilling alcohol |
| Barrel (6 wood types) | Fermenting / aging |
| Flask | Storing fluids |
| Wooden Vat (Mash Tub) | Mashing |
| Tray, Bunsen Burner, Lattice | Processing / growing support |
| Bottle Rack | Storage display |

### Items (100+)

Raw materials, dried plants, chemical compounds, smoked/consumed items, containers, and more. Includes:
- **Smokeable:** joint, peyote joint, cigarette, cigar, pipe, bong
- **Drinkable:** flask, obsidian bottle, filled glass bottle, filled bowl, wooden mug
- **Injectable:** syringe items
- **Chemical compounds:** LSD-25, ALD-52, mescaline, and many others (as vials/powders)
- **Miscellaneous:** rift jar, molotov cocktail, paper bag, harmonium crystal

### Commands

```
/drug set <player> <type> <level>
/drug add <player> <type> <amount>
/drug lock <player> [type]
/drug unlock <player> [type]
/hallucinate <player> <type>
/vomit <player>
```

---

## Installation

### Fabric
1. Install [Fabric Loader](https://fabricmc.net/use/) 0.18.4+
2. Install [Fabric API](https://modrinth.com/mod/fabric-api) for 1.21.11
3. Drop the `-fabric` jar into your `mods/` folder

### Forge
1. Install Forge 1.21.11-61.1.4+
2. Drop the `-forge` jar into your `mods/` folder

### NeoForge
1. Install NeoForge 21.11.38+
2. Drop the `-neoforge` jar into your `mods/` folder

---

## Building from Source

**Requirements:** Java 21 (`JAVA_HOME` pointing to a JDK 21)

```bash
# All loaders
./gradlew build

# Fabric only
./gradlew :fabric:build

# Run dev server (Fabric)
./gradlew :fabric:runServer

# Run dev client (Fabric)
./gradlew :fabric:runClient
```

### Automated smoke test
```bash
# Full build + server start + RCON checks + log analysis
bash dev-tools/smoke-test.sh

# Skip rebuild
bash dev-tools/smoke-test.sh --skip-build

# Client mode (requires DISPLAY)
bash dev-tools/smoke-test.sh --client
```

---

## Configuration

In-game settings screen (accessible via ModMenu on Fabric or the mod config button):
- **Visuals** — shaders, overlays, depth of field, motion blur, sun flare
- **Sounds** — drug theme music
- **Gameplay** — message distortion, harmonium, rift jars, molotovs
- **Balancing** — world generation rates for each plant/ore

---

## Links

- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/orange-sunshine)
- [Issues](https://github.com/Moriz82/OrangeSunshine/issues)
- [Source](https://github.com/Moriz82/OrangeSunshine)

## License

MIT
