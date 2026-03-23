package moriz.orangesunshine.client.render;

import java.util.Optional;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

/**
 * 1.21+ no longer exposes {@code RenderType.MultiPhase} the same way; texture introspection is stubbed.
 */
public interface RenderLayerUtil {
    static Optional<Identifier> getTexture(RenderType layer) {
        return Optional.empty();
    }
}
