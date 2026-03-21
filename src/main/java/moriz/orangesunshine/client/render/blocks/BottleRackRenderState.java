package moriz.orangesunshine.client.render.blocks;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.ArrayList;

public class BottleRackRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public List<ItemStack> items = new ArrayList<>();
}
