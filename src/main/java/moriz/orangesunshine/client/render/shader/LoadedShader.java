package moriz.orangesunshine.client.render.shader;

import java.io.IOException;
import java.util.*;

import org.joml.Vector3f;
import com.google.gson.JsonSyntaxException;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import moriz.orangesunshine.client.render.shader.UniformBinding.UniformSetter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import com.mojang.blaze3d.shaders.EffectProgram;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.resources.Identifier;

class LoadedShader extends PostChain {
    private final UniformBinding.Set bindings;

    private int width;
    private int height;
    private float time;
    private float lastTickDelta;

    private List<Pass> passes;

    public LoadedShader(Minecraft client, Identifier id, UniformBinding.Set bindings) throws IOException, JsonSyntaxException {
        super(Map.of(), List.of());
        this.bindings = bindings;
        if (passes == null) {
            passes = new ArrayList<>();
        }
    }

    public void render(float tickDelta) {
        if (tickDelta < lastTickDelta) {
            time += 1 - lastTickDelta;
            time += tickDelta;
        } else {
            time += tickDelta - lastTickDelta;
        }
        this.lastTickDelta = tickDelta;
        while (time > 20) {
            time -= 20;
        }

        boolean rendered = false;
        final float passRenderFrame = time / 20F;
        for (Pass pass : passes) {
            rendered |= pass.render(passRenderFrame, tickDelta, rendered);
        }
    }

    public void setupDimensions(int targetsWidth, int targetsHeight) {
        this.width = targetsWidth;
        this.height = targetsHeight;
    }

    class Pass implements UniformSetter {
        private final EffectProgram program;
        private final PostPass pass;

        private final List<FloatConsumer> replay = new ArrayList<>();
        private int updateCount;

        private boolean rendered;

        public Pass(PostPass pass) {
            this.pass = pass;
            this.program = null; // pass.getProgram() is not available or named differently
        }

        public boolean render(float passRenderTime, float tickDelta, boolean rendered) {
            return false;
        }

        @Override
        public void set(String name, float value) {
        }

        @Override
        public void set(String name, float... values) {
        }

        @Override
        public void set(String name, Vector3f values) {
        }
    }
}
