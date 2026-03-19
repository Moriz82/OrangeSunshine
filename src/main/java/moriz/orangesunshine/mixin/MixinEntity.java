package moriz.orangesunshine.mixin;

import moriz.orangesunshine.block.MashTubBlock;
import moriz.orangesunshine.entity.TouchingWaterAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

@Mixin(Entity.class)
abstract class MixinEntity implements TouchingWaterAccessor {
    @Shadow
    protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;

    @Override
    @Accessor("wasTouchingWater")
    public abstract void setTouchingWater(boolean touchingWater);

    @Inject(method = "updateFluidHeightAndDoFluidPushing(Lnet/minecraft/tags/TagKey;D)Z", at = @At("RETURN"), cancellable = true)
    private void onUpdateMovementInFluid(TagKey<Fluid> tag, double speed, CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValue()) {
            Entity self = (Entity)(Object)this;
            BlockPos.betweenClosedStream(self.getBoundingBox().deflate(0.001D)).map(pos -> {
                BlockState state = self.level().getBlockState(pos);
                if (state.getBlock() instanceof MashTubBlock tub) {
                    return tub.getFluidHeight(self.level(), state, pos, tag);
                }
                return -1;
            }).filter(l -> l > 0).findFirst().ifPresent(level -> {
                fluidHeight.put(tag, level);
                setTouchingWater(true);
                info.setReturnValue(true);
            });
        }
    }
}
