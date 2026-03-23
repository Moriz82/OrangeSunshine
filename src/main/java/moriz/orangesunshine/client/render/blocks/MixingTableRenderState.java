package moriz.orangesunshine.client.render.blocks;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class MixingTableRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState itemLayer = new ItemStackRenderState();
}
