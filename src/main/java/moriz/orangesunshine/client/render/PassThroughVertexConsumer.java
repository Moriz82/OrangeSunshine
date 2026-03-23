package moriz.orangesunshine.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * @author Sollace
 */
public class PassThroughVertexConsumer implements VertexConsumer {
    private static final ColorFix COLOR = VertexConsumer::setColor;
    private static final FUvFix TEXTURE = VertexConsumer::setUv;
    private static final IUvFix OVERLAY = VertexConsumer::setOverlay;
    private static final IUvFix LIGHT = VertexConsumer::setLight;

    private final VertexConsumer parent;

    private final ColorFix colorFix;
    private final FUvFix textureFix;
    private final IUvFix overlayFix;
    private final IUvFix lightFix;

    public static VertexConsumer of(VertexConsumer parent, Parameters parameters) {
        return new PassThroughVertexConsumer(parent, parameters);
    }

    PassThroughVertexConsumer(VertexConsumer parent, Parameters parameters) {
        this.parent = parent;
        colorFix = parameters.colorFix;
        textureFix = parameters.textureFix;
        overlayFix = parameters.overlayFix;
        lightFix = parameters.lightFix;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        parent.addVertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        colorFix.apply(parent, r, g, b, a);
        return this;
    }

    @Override
    public VertexConsumer setColor(int argb) {
        parent.setColor(argb);
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        textureFix.apply(parent, u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        overlayFix.apply(parent, u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        lightFix.apply(parent, u, v);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        parent.setNormal(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setLineWidth(float lineWidth) {
        parent.setLineWidth(lineWidth);
        return this;
    }

    public static class Parameters {
        private ColorFix colorFix = COLOR;
        private FUvFix textureFix = TEXTURE;
        private IUvFix overlayFix = OVERLAY;
        private IUvFix lightFix = LIGHT;

        public Parameters color(ColorFix fix) {
            colorFix = fix;
            return this;
        }

        public Parameters texture(FUvFix fix) {
            textureFix = fix;
            return this;
        }

        public Parameters overlay(IUvFix fix) {
            overlayFix = fix;
            return this;
        }

        public Parameters light(IUvFix fix) {
            lightFix = fix;
            return this;
        }
    }

    public interface PosFix {
        void apply(VertexConsumer consumer, float x, float y, float z);
    }
    public interface ColorFix {
        void apply(VertexConsumer consumer, int r, int g, int b, int a);
    }
    public interface FUvFix {
        void apply(VertexConsumer consumer, float u, float v);
    }
    public interface IUvFix {
        default void apply(VertexConsumer consumer, int u, int v) {
            apply(consumer, u | (v << 16));
        }

        void apply(VertexConsumer consumer, int uv);
    }
}
