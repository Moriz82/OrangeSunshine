package moriz.orangesunshine.client.render.shader;

import java.util.*;

import moriz.orangesunshine.client.OrangeSunshineClient;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.opengl.GlStateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostEffectRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostEffectRenderer.class);
    private List<LoadedShader> shaders = new ArrayList<>();

    public void render(float tickDelta) {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        if (!OrangeSunshineClient.getConfig().visual.shader2DEnabled) {
            return;
        }

        GlStateManager._disableBlend();
        GlStateManager._disableDepthTest();

        for (LoadedShader shader : shaders) {
            shader.render(tickDelta);
        }
    }

    public int getShaderCount() {
        return shaders.size();
    }

    public List<String> getDebugSnapshots() {
        return shaders.stream().map(LoadedShader::getDebugSnapshot).toList();
    }

    public void setupDimensions(int width, int height) {
        shaders.forEach(shader -> shader.setupDimensions(width, height));
    }

    public void onShadersLoaded(List<LoadedShader> shaders) {
        List<LoadedShader> oldShaders = this.shaders;
        this.shaders = shaders;
        oldShaders.forEach(LoadedShader::close);
    }
}
