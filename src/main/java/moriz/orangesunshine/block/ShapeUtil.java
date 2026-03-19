package moriz.orangesunshine.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface ShapeUtil {

    static VoxelShape createCenteredShape(double x, double y, double z) {
        return Block.box(8 - x, 0, 8 - z, 8 + x, y, 8 + z);
    }
}
