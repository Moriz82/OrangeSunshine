package moriz.orangesunshine.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mixin(VoxelShape.class)
abstract class MixinVoxelShape {
    // Raytrace fix for block collissions that extend beyond their tile
    @Inject(method = "clip", at = @At("RETURN"), cancellable = true)
    private void raycast(Vec3 start, Vec3 end, BlockPos pos, CallbackInfoReturnable<BlockHitResult> info) {
        BlockHitResult result = info.getReturnValue();
        if (result == null) {
            return;
        }

        Vec3 diff = result.getLocation().subtract(result.getBlockPos().getCenter());
        final double maxDiff = 1.0000001;
        if (Math.abs(diff.x) < maxDiff && Math.abs(diff.y) < maxDiff && Math.abs(diff.z) < maxDiff) {
            return;
        }

        info.setReturnValue(result.withPosition(BlockPos.containing(result.getLocation())));
    }
}
