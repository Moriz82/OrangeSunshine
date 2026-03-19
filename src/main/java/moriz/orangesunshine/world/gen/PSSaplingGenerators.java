package moriz.orangesunshine.world.gen;

import java.util.Optional;

import net.minecraft.world.level.block.grower.TreeGrower;

public interface PSSaplingGenerators {
    TreeGrower JUNIPER = new TreeGrower("orangesunshine:juniper", Optional.empty(), Optional.of(PSWorldGen.JUNIPER_TREE_CONFIG), Optional.empty());
}
