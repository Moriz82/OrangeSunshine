package moriz.orangesunshine.client.render.blocks;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.entity.RiftJarBlockEntity;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.ArrayList;

public class RiftJarRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public float fractionOpen;
    public float fractionHandleUp;
    public float currentRiftFraction;
    public boolean jarBroken;
    public float ticks;
    public Vec3 pos = Vec3.ZERO;
    public List<RiftJarBlockEntity.JarRiftConnection> connections = new ArrayList<>();
}
