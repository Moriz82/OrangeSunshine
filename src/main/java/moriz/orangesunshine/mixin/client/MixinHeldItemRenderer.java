package moriz.orangesunshine.mixin.client;

import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemInHandRenderer.class)
abstract class MixinHeldItemRenderer {
}
