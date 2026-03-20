package moriz.orangesunshine.block;

import moriz.orangesunshine.OrangeSunshine;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public interface PSWoodTypes {
    BlockSetType JUNIPER_SET = registerBlockSet("juniper");
    WoodType JUNIPER = registerWood("juniper", JUNIPER_SET);

    static BlockSetType registerBlockSet(String name) {
        String typeName = OrangeSunshine.id(name).toString();
        BlockSetType type = new BlockSetType(typeName);
        try {
            java.lang.reflect.Method m = BlockSetType.class.getDeclaredMethod("register", BlockSetType.class);
            m.setAccessible(true);
            return (BlockSetType) m.invoke(null, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register BlockSetType " + typeName, e);
        }
    }

    static WoodType registerWood(String name, BlockSetType blockSetType) {
        String typeName = OrangeSunshine.id(name).toString();
        WoodType type = new WoodType(typeName, blockSetType);
        try {
            java.lang.reflect.Method m = WoodType.class.getDeclaredMethod("register", WoodType.class);
            m.setAccessible(true);
            return (WoodType) m.invoke(null, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register WoodType " + typeName, e);
        }
    }
}
