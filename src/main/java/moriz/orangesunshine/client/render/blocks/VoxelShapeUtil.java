package moriz.orangesunshine.client.render.blocks;

import java.util.Map;
import java.util.function.Function;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface VoxelShapeUtil {
    static Function<Direction, VoxelShape> rotator(VoxelShape base) {
        Map<Direction, VoxelShape> rotations = Shapes.rotateHorizontal(base);
        return direction -> rotations.getOrDefault(direction, base);
    }
}
