package moriz.orangesunshine.client.render.blocks;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import java.util.Map;
import java.util.HashMap;

public class MashTubRenderState extends BlockEntityRenderState {
    public int fluidColor;
    public float fluidLevel;
    public ItemStack solidContents = ItemStack.EMPTY;
    public Map<Item, Integer> suppliedIngredients = new HashMap<>();
}
