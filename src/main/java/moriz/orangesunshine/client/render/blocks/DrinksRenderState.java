package moriz.orangesunshine.client.render.blocks;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import java.util.List;
import java.util.ArrayList;

public class DrinksRenderState extends BlockEntityRenderState {
    public List<DrinkRenderEntry> drinks = new ArrayList<>();
    public BlockPos hitPos;
    public boolean hasDrinkAtHitPos;

    public static record DrinkRenderEntry(float x, float y, float z, float rotation, ItemStack stack, float height) {}
}
