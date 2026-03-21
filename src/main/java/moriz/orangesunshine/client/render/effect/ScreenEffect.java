package moriz.orangesunshine.client.render.effect;

import net.minecraft.client.gui.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public interface ScreenEffect extends AutoCloseable {
    default boolean shouldApply(float tickDelta) {
        return true;
    }

    void update(float tickDelta);

    void render(GuiGraphics context, MultiBufferSource vertices, int screenWidth, int screenHeight, float ticks, PingPong pingPong);

    static void drawScreen(GuiGraphics context, int screenWidth, int screenHeight) {
    }

    interface PingPong {
        void pingPong();
    }
}
