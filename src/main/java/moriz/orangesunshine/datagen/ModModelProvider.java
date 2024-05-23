package moriz.orangesunshine.datagen;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.chemistry.MatterState;
import moriz.orangesunshine.chemistry.MatterStateItem;
import moriz.orangesunshine.item.PSItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.data.client.Model;

import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(PSBlocks.SULFUR_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(PSBlocks.SALT_DEPOSIT);
        blockStateModelGenerator.registerSimpleCubeAll(PSBlocks.PYROLUSITE);
        blockStateModelGenerator.registerSimpleCubeAll(PSBlocks.PHOSPHORUS_ORE);

        blockStateModelGenerator.registerSimpleState(PSBlocks.MORTAR_PESTLE);
        blockStateModelGenerator.registerSimpleState(PSBlocks.MIXING_TABLE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(PSItems.SULFUR, Models.GENERATED);
        itemModelGenerator.register(PSItems.SULFUR_POWDER, Models.GENERATED);
        itemModelGenerator.register(PSItems.SALT, Models.GENERATED);
        itemModelGenerator.register(PSItems.SALT_POWDER, Models.GENERATED);
        itemModelGenerator.register(PSItems.MANGANESE_DIOXIDE, Models.GENERATED);
        itemModelGenerator.register(PSItems.MANGANESE_DIOXIDE_POWDER, Models.GENERATED);
        itemModelGenerator.register(PSItems.PHOSPHORUS, Models.GENERATED);

        registerCompoundItem(PSItems.LYSERGIC_ACID, itemModelGenerator);
        registerCompoundItem(PSItems.LSD25, itemModelGenerator);
        registerCompoundItem(PSItems.ALD52, itemModelGenerator);

        registerCompoundItem(PSItems.ETH_HCL, itemModelGenerator);
        registerCompoundItem(PSItems.DEFAT_ERGOT, itemModelGenerator);
        registerCompoundItem(PSItems.ERGOT_ALKALOIDS, itemModelGenerator);
        registerCompoundItem(PSItems.NEUTRAL_ERGOT_ALKALOIDS, itemModelGenerator);
        registerCompoundItem(PSItems.ERGOPEPTINES, itemModelGenerator);
        registerCompoundItem(PSItems.DISSOLVED_ERGOPEPTINES, itemModelGenerator);
        registerCompoundItem(PSItems.DISSOLVED_ERGOPEPTINES_ACID, itemModelGenerator);
    }

    public void registerCompoundItem(MatterStateItem item, ItemModelGenerator itemModelGenerator){
        String modelLocation = "";
        MatterState matterState = item.getMatterState();
        switch (matterState){
            case BEAKER -> {modelLocation = "item/compound_solid_model";}
            case FLASK -> {
                modelLocation = "item/compound_liquid_model";
            }
            case GAS -> {throw new RuntimeException("GAS MODEL NOT IMPLEMENTED");}
            case VIAL -> {modelLocation = "item/compound_vial_model";}
        }

        itemModelGenerator.register((Item) item,
                new Model(Optional.of(OrangeSunshine.id(modelLocation)), Optional.empty()));
    }
}
