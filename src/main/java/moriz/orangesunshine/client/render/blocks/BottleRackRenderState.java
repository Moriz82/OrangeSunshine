package moriz.orangesunshine.client.render.blocks;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;

public class BottleRackRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    /** One entry per rack slot (9); entries may be empty. */
    public final ItemStackRenderState[] itemLayers = new ItemStackRenderState[9];

    public BottleRackRenderState() {
        for (int i = 0; i < itemLayers.length; i++) {
            itemLayers[i] = new ItemStackRenderState();
        }
    }
}
