package moriz.orangesunshine.client.render.blocks;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public class PeyoteRenderState extends BlockEntityRenderState {
    public int age;
    public long seed;
    public Vec3 offset = Vec3.ZERO;
    public Identifier texture;
}
