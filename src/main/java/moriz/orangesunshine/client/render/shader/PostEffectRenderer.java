package moriz.orangesunshine.client.render.shader;

import java.util.*;

import moriz.orangesunshine.client.OrangeSunshineClient;

public class PostEffectRenderer {
    private List<LoadedShader> shaders = new ArrayList<>();

    public void render(float tickDelta) {
        if (OrangeSunshineClient.getConfig().visual.shader2DEnabled) {
            if (shaders.size() == 1) {
                shaders.get(0).render(tickDelta);
            } else {
                shaders.forEach(shader -> shader.render(tickDelta));
            }
        }
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
