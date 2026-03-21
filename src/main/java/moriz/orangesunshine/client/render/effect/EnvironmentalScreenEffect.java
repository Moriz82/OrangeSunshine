package moriz.orangesunshine.client.render.effect;

import com.mojang.blaze3d.systems.RenderSystem;

import moriz.orangesunshine.entity.drug.DrugProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.util.ARGB;

public class EnvironmentalScreenEffect implements ScreenEffect {

    private float heatDistortion;
    private float waterDistortion;

    public float getHeatDistortion() {
        return heatDistortion;
    }

    public float getWaterDistortion() {
        return waterDistortion;
    }

    @Override
    public void update(float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Level world = mc.level;

        if (player == null || world == null) {
            return;
        }

        waterDistortion = player.isUnderWater() ? 1 : 0;
    }

    @Override
    public void render(GuiGraphics context, MultiBufferSource vertices, int screenWidth, int screenHeight, float ticks, PingPong pingPong) {
        if (waterDistortion > 0) {
            context.fill(RenderPipelines.GUI, 0, 0, screenWidth, screenHeight, ARGB.colorFromFloat(0.3f, 0, 0.4f, 0.9f));
        }
    }

    @Override
    public void close() throws Exception {
    }

}
