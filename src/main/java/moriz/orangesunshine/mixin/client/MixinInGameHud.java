package moriz.orangesunshine.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import moriz.orangesunshine.client.render.DrugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
abstract class MixinInGameHud {

    @Inject(method = "render", at = @At("TAIL"), require = 0)
    private void onRenderHud(GuiGraphics context, DeltaTracker deltaTracker, CallbackInfo ci) {
        DrugRenderer.INSTANCE.onRenderOverlay(context, deltaTracker.getGameTimeDeltaTicks());
    }
}
