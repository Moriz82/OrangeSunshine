package moriz.orangesunshine.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import moriz.orangesunshine.client.render.DrugRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
abstract class MixinInGameHud {
    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;F)V", at = @At("HEAD"))
    private void onRender(DrawContext context, float tickDelta, CallbackInfo info) {
        DrugRenderer.INSTANCE.onRenderOverlay(context, tickDelta);
    }
}
