package moriz.orangesunshine.mixin.client;

import java.util.Objects;

import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.hallucination.EntityIdentitySwapHallucination;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderDispatcher.class)
abstract class MixinEntityRenderDispatcher {
    /**
     * Swap one entity for another when an EntityIdentitySwapHallucination is active,
     * making the player perceive a different entity than what is actually there.
     */
    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true, require = 0)
    private Entity swapEntity(Entity entity) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return entity;
        return DrugProperties.of((Entity) mc.player)
                .stream()
                .flatMap(properties -> properties.getHallucinations()
                        .getEntities().<EntityIdentitySwapHallucination>getHallucinations(EntityIdentitySwapHallucination.class).stream())
                .map(h -> h.matchOrAttach(entity))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(entity);
    }
}
