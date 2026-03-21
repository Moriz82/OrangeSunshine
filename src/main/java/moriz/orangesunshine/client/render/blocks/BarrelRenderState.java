package moriz.orangesunshine.client.render.blocks;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class BarrelRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public float tapRotation;
    public float xRot;
    public float y;
    public boolean tapVisible;
    public boolean rackVisible;
    public float treeY;
    public Identifier texture;
    @Nullable
    public Identifier symbol;
}
