package moriz.orangesunshine.client.render.blocks;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class FlaskRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public int fluidColor;
    public float fluidLevel;
    public float inputProgress;
    public float outputProgress;
}
