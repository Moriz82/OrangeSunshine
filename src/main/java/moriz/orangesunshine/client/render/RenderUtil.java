package moriz.orangesunshine.client.render;

import java.util.Random;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ARGB;

public final class RenderUtil {
    private RenderUtil() {
    }

    public static void drawRepeatingSprite(GuiGraphics context, TextureAtlasSprite sprite, int x, int y, int width, int height, float r, float g, float b, float a) {
        final int tileSize = 16;
        int color = ARGB.colorFromFloat(a, r, g, b);
        for (int tileX = 0; tileX < width; tileX += tileSize) {
            for (int tileY = 0; tileY < height; tileY += tileSize) {
                int tileWidth = Math.min(tileSize, width - tileX);
                int tileHeight = Math.min(tileSize, height - tileY);
                context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x + tileX, y + tileY, tileWidth, tileHeight, color);
            }
        }
    }

    public static Random random(long seed) {
        return new Random(seed);
    }
}
