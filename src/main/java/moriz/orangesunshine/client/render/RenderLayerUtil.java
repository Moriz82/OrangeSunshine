package moriz.orangesunshine.client.render;

import java.util.Optional;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.Identifier;

public interface RenderLayerUtil {
    static Optional<Identifier> getTexture(RenderLayer layer) {
        if (layer instanceof RenderLayer.MultiPhase multiphase) {
            return multiphase.getPhases().texture.getId();
        }
        return Optional.empty();
    }
}
