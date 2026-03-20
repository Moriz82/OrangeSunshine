package moriz.orangesunshine.mixin.client;

import moriz.orangesunshine.client.render.RenderPhase;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Tracks which render phase (sky, clouds, world, etc.) is currently active so that
 * the geometry-shader system can apply effects selectively.
 */
@Mixin(LevelRenderer.class)
abstract class MixinWorldRenderer {

    @Inject(method = "renderSky", at = @At("HEAD"), require = 0)
    private void beforeRenderSky(CallbackInfo info) {
        RenderPhase.SKY.push();
    }

    @Inject(method = "renderSky", at = @At("RETURN"), require = 0)
    private void afterRenderSky(CallbackInfo info) {
        RenderPhase.pop();
    }

    @Inject(method = "renderClouds", at = @At("HEAD"), require = 0)
    private void beforeRenderClouds(CallbackInfo info) {
        RenderPhase.CLOUDS.push();
    }

    @Inject(method = "renderClouds", at = @At("RETURN"), require = 0)
    private void afterRenderClouds(CallbackInfo info) {
        RenderPhase.pop();
    }
}
