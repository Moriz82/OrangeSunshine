package moriz.orangesunshine.mixin;

import moriz.orangesunshine.item.SmokeableItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(AbstractFurnaceBlockEntity.class)
abstract class MixinAbstractFurnaceBlockEntity {
    @Inject(method = "serverTick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"
        )
    )
    private static void onTick(ServerLevel world, BlockPos pos, BlockState state,
            AbstractFurnaceBlockEntity blockEntity, CallbackInfo info) {
        ItemStack fuel = blockEntity.getItem(1);
        if (fuel.getItem() instanceof SmokeableItem smokeable) {
            smokeable.onIncinerated(fuel, world, pos, blockEntity);
        }
    }
}
