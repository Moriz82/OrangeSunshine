package com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.post;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.ShaderRenderer;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class PostShaders {
    public static boolean useShaders = false;
    private static final List<PostShader> postShaders = new ArrayList<>();
    private static final Object2ObjectOpenHashMap<String, PostShaderSupplier> supplierMap = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, PostShader> activeShaders = new Object2ObjectOpenHashMap<>();

    public static void setup() throws IOException {
        register(Depth::new);
        register(Color::new);
        register(Bumpy::new);
        register(Recursion::new);
        register(Kaleidoscope::new);
        register(WaterDistort::new);
        register(Bloom::new);

        MinecraftForge.EVENT_BUS.register(PostShaders.EventHandler.class);

        useShaders = true;
    }

    private static void register(PostShaderSupplier supplier) throws IOException {
        PostShader shader = supplier.get();
        shader.close();
        postShaders.add(shader);
        supplierMap.put(shader.toString(), supplier);
    }

    public static void cleanup() {
        MinecraftForge.EVENT_BUS.unregister(PostShaders.EventHandler.class);

        useShaders = false;

        postShaders.clear();
        supplierMap.clear();
        activeShaders.forEach((name, shader) -> shader.close());
        activeShaders.clear();
    }

    public static void processShaders(float partialTicks) {
        for (PostShader postShader : postShaders) {
            if (postShader.shouldRender()) {
                PostShader shader = getShader(postShader.toString());
                shader.render(partialTicks);
            } else {
                dealloc(postShader.toString());
            }
        }
    }

    private static PostShader getShader(String name) {
        if (activeShaders.containsKey(name)) {
            return activeShaders.get(name);
        } else {
            try {
                PostShader shader = supplierMap.get(name).get();
                activeShaders.put(name, shader);
                return shader;
            } catch (IOException e) {
                throw new RuntimeException("Couldn't create post shader", e);
            }
        }
    }

    private static void dealloc(String name) {
        if (activeShaders.containsKey(name)) activeShaders.remove(name).close();
    }

    @FunctionalInterface
    private interface PostShaderSupplier {
        PostShader get() throws IOException, JsonSyntaxException;
    }

    static class EventHandler {
        @SubscribeEvent
        public static void renderPostWorld(RenderWorldLastEvent event) {
            ShaderRenderer.processPostShaders(event.getPartialTicks());
        }
    }
}
