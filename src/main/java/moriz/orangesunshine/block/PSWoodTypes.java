package moriz.orangesunshine.block;

import moriz.orangesunshine.OrangeSunshine;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public interface PSWoodTypes {
    BlockSetType JUNIPER_SET = registerBlockSet("juniper");
    WoodType JUNIPER = registerWood("juniper", JUNIPER_SET);

    static BlockSetType registerBlockSet(String name) {
        Identifier id = OrangeSunshine.id(name);
        return new BlockSetTypeBuilder().register(id);
    }

    static WoodType registerWood(String name, BlockSetType blockSetType) {
        Identifier id = OrangeSunshine.id(name);
        return new WoodTypeBuilder().register(id, blockSetType);
    }
}
