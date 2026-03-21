package moriz.orangesunshine.client.render.bezier;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.FormattedBidiReorder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.*;
import net.minecraft.util.FormattedCharSequence;
import com.mojang.math.Axis;

public class BezierLabelRenderer {
    public static final BezierLabelRenderer INSTANCE = new BezierLabelRenderer();

    private final Font font = Minecraft.getInstance().font;

    private float length;
    private int i;

    private final float scale = -1/12F;

    private int activeIndex;
    private int activeCodePoint;
    private net.minecraft.network.chat.Style activeStyle;
    private final FormattedCharSequence singleCharOrderedText = visitor -> {
        return visitor.accept(activeIndex, activeStyle, activeCodePoint);
    };

    public void render(PoseStack matrices, MultiBufferSource vertices, int light, Bezier bezier, Style style, Component text) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        length = text.getString().length();
        i = 0;
        Path path = bezier.getPath();
        FormattedBidiReorder.reorder(text, !style.inwards).accept((charIndex, charStyle, character) -> {
            if (character != ' ') {
                double totalProgress = (style.spread ? (i / length) : (i * 0.5)) + style.shift;
                double finalProgress = ((totalProgress % 1) + 1) % 1;

                if (finalProgress >= style.bottomCap && finalProgress <= style.capTop) {
                    Path.Intermediate step = path.getStep(finalProgress);
                    Vector3d position = step.position();
                    Vector3d rotation = path.getNaturalRotation(step, 0.01);

                    float textSize = scale * step.fontSize();

                    matrices.pushPose();
                    matrices.translate(position.x, position.y, position.z);
                    matrices.scale(textSize, textSize, textSize);
                    matrices.mulPose(Axis.YP.rotationDegrees((float)rotation.x + (style.inwards ? 0 : 180)));
                    matrices.mulPose(Axis.XP.rotationDegrees((float)rotation.y));

                    activeIndex = charIndex;
                    activeStyle = charStyle;
                    activeCodePoint = character;

                    @Nullable TextColor color = charStyle.getColor();
                    Matrix4f positionMatrix = matrices.last().pose();
                    font.drawInBatch(singleCharOrderedText, 0, 0, color == null ? 0xFFFFFFFF : color.getValue(), false, positionMatrix, vertices, Font.DisplayMode.NORMAL, 0, light);
                    matrices.popPose();
                }
            }
            i++;
            return true;
        });

        RenderSystem.disableBlend();
    }

    public static class Style {
        float capTop;
        float bottomCap;
        boolean inwards;
        boolean spread;
        float shift;

        public Style spread(boolean spread) {
            this.spread = spread;
            return this;
        }

        public Style shift(float shift) {
            this.shift = shift;
            return this;
        }

        public Style inwards(boolean inwards) {
            this.inwards = inwards;
            return this;
        }

        public Style bottomCap(float capBottom) {
            this.bottomCap = capBottom;
            return this;
        }

        public Style topCap(float capTop) {
            this.capTop = capTop;
            return this;
        }
    }
}
