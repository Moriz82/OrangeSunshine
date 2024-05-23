package moriz.orangesunshine.datagen;

import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.item.PSItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(PSBlocks.SULFUR_ORE, copperLikeOreDrops(PSBlocks.SULFUR_ORE, PSItems.SULFUR));
        addDrop(PSBlocks.SALT_DEPOSIT, copperLikeOreDrops(PSBlocks.SALT_DEPOSIT, PSItems.SALT));
        addDrop(PSBlocks.PYROLUSITE, copperLikeOreDrops(PSBlocks.PYROLUSITE, PSItems.MANGANESE_DIOXIDE));
        addDrop(PSBlocks.PHOSPHORUS_ORE, copperLikeOreDrops(PSBlocks.PHOSPHORUS_ORE, PSItems.PHOSPHORUS));
    }

        public LootTable.Builder copperLikeOreDrops(Block drop, Item item) {
        return BlockLootTableGenerator.dropsWithSilkTouch(drop, (LootPoolEntry.Builder)this.applyExplosionDecay(drop,
                ((LeafEntry.Builder)
                        ItemEntry.builder(item)
                                .apply(SetCountLootFunction
                                        .builder(UniformLootNumberProvider
                                                .create(2.0f, 5.0f))))
                        .apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))));
    }
}