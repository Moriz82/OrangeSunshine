package moriz.orangesunshine.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public interface MixinLivingEntity {
    @Invoker("jumpFromGround")
    void invokeJump();
}
