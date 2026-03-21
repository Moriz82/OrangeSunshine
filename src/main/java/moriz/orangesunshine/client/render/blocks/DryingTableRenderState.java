package moriz.orangesunshine.client.render.blocks;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class DryingTableRenderState extends BlockEntityRenderState {
    public List<ItemState> items = new ArrayList<>();
    public long seed;

    public static class ItemState {
        public ItemStack stack;
        public float x;
        public float z;
        public float rotation;
        public boolean result;
    }
}
