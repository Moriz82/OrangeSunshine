package moriz.orangesunshine.client.render.shader;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

import com.google.gson.*;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import moriz.orangesunshine.client.OrangeSunshineClient;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostChainConfig;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.ShaderManager;
import moriz.orangesunshine.mixin.client.AccessorPostChain;
import moriz.orangesunshine.mixin.client.AccessorPostPass;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

class LoadedShader implements AutoCloseable {
    private static final Gson GSON = new Gson();
    private static final int UBO_DESTINATION_USAGE = GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_MAP_WRITE;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadedShader.class);

    private final UniformBinding.Set bindings;
    /** Resource id of the post JSON; used for GPU buffer debug labels. */
    private final Identifier resourceId;
    private final PostChain chain;
    private final List<PostPass> passes;
    private final CachedOrthoProjectionMatrixBuffer projMatrix;

    /** Fragment shader last path component for each pass, parallel to {@code passes}. */
    private final List<String> passKeys;

    /**
     * Pre-allocated write buffers keyed by "passKey.blockName", sized to the
     * UBO layout so we avoid direct-buffer allocation on every frame.
     */
    private final Map<String, ByteBuffer> uboBuffers;

    private int width;
    private int height;
    private float time;
    private float lastTickDelta;
    private String lastDebugSnapshot = "idle";

    LoadedShader(Minecraft client, Identifier id, UniformBinding.Set bindings) throws IOException {
        this.bindings = bindings;
        this.resourceId = id;

        Optional<Resource> resource = client.getResourceManager().getResource(id);
        if (resource.isEmpty()) {
            throw new IOException("Shader resource not found: " + id);
        }

        JsonObject json;
        try (Reader reader = resource.get().openAsReader()) {
            json = GSON.fromJson(reader, JsonObject.class);
        }

        DataResult<Pair<PostChainConfig, JsonElement>> decoded =
                PostChainConfig.CODEC.decode(JsonOps.INSTANCE, json);
        if (decoded.isError()) {
            throw new IOException("Failed to decode PostChainConfig for " + id + ": "
                    + decoded.error().map(e -> e.message()).orElse("unknown"));
        }
        PostChainConfig config = decoded.getOrThrow().getFirst();

        this.projMatrix = new CachedOrthoProjectionMatrixBuffer("orangesunshine_post", -1f, 1f, false);

        try {
            this.chain = PostChain.load(
                config,
                client.getTextureManager(),
                Set.of(PostChain.MAIN_TARGET_ID),
                PostChain.MAIN_TARGET_ID,
                projMatrix
            );
        } catch (ShaderManager.CompilationException e) {
            projMatrix.close();
            throw new IOException("Failed to compile PostChain for " + id + ": " + e.getMessage(), e);
        }

        this.passes = ((AccessorPostChain) (Object) chain).orangesunshine$getPasses();

        this.passKeys = new ArrayList<>(config.passes().size());
        for (PostChainConfig.Pass p : config.passes()) {
            String fragPath = p.fragmentShaderId().getPath();
            passKeys.add(fragPath.substring(fragPath.lastIndexOf('/') + 1));
        }

        // Pre-allocate one ByteBuffer per UBO layout so render() can reuse them.
        this.uboBuffers = new HashMap<>();
        for (Map.Entry<String, List<UniformBinding.UboField>> entry : bindings.uboLayouts.entrySet()) {
            int size = computeUboSize(entry.getValue());
            uboBuffers.put(entry.getKey(), ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()));
        }

        // PostPass allocates UBO GpuBuffers with USAGE_UNIFORM only; writeToBuffer needs USAGE_COPY_DST.
        upgradeCustomUniformBuffersForCpuUpload();
    }

    /**
     * Replace vanilla post-pass UBO buffers so {@link CommandEncoder#writeToBuffer} can upload each frame.
     * Vanilla uses {@link GpuBuffer#USAGE_UNIFORM} (128) without {@link GpuBuffer#USAGE_COPY_DST}.
     */
    private void upgradeCustomUniformBuffersForCpuUpload() {
        var device = RenderSystem.getDevice();
        for (PostPass postPass : passes) {
            Map<String, GpuBuffer> customUniforms =
                    ((AccessorPostPass) (Object) postPass).orangesunshine$getCustomUniforms();
            if (customUniforms == null || customUniforms.isEmpty()) {
                continue;
            }
            for (Map.Entry<String, GpuBuffer> entry : customUniforms.entrySet()) {
                GpuBuffer previous = entry.getValue();
                if (previous == null) {
                    continue;
                }
                if ((previous.usage() & UBO_DESTINATION_USAGE) == UBO_DESTINATION_USAGE) {
                    continue;
                }
                String blockName = entry.getKey();
                GpuBuffer upgraded = device.createBuffer(
                        () -> resourceId + " dynamic UBO / " + blockName,
                        UBO_DESTINATION_USAGE,
                        previous.size());
                entry.setValue(upgraded);
                previous.close();
            }
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

        boolean shouldRender = false;

        int winWidth = Minecraft.getInstance().getWindow().getWidth();
        int winHeight = Minecraft.getInstance().getWindow().getHeight();
        int screenWidth = width > 0 ? width : winWidth;
        int screenHeight = height > 0 ? height : winHeight;

        CollectingUniformSetter globalSetter = new CollectingUniformSetter();
        bindings.global.bindUniforms(globalSetter, tickDelta, screenWidth, screenHeight, globalSetter.passRunnable);

        CommandEncoder encoder = null;
        List<String> activePasses = new ArrayList<>();
        String firstUniformSnapshot = "";

        for (int i = 0; i < passes.size() && i < passKeys.size(); i++) {
            PostPass postPass = passes.get(i);
            String passKey = passKeys.get(i);

            CollectingUniformSetter setter = new CollectingUniformSetter();
            setter.values.putAll(globalSetter.values);
            setter.shouldRender = globalSetter.shouldRender;

            UniformBinding programBinding = bindings.programBindings.get(passKey);
            if (programBinding != null) {
                programBinding.bindUniforms(setter, tickDelta, screenWidth, screenHeight, setter.passRunnable);
            }

            shouldRender |= setter.shouldRender;
            if (setter.shouldRender) {
                activePasses.add(passKey);
                if (firstUniformSnapshot.isEmpty() && !setter.values.isEmpty()) {
                    firstUniformSnapshot = buildUniformSnapshot(setter.values);
                }
            }

            Map<String, GpuBuffer> customUniforms = ((AccessorPostPass) (Object) postPass).orangesunshine$getCustomUniforms();
            if (customUniforms != null
                    && !customUniforms.isEmpty()
                    && setter.shouldRender) {
                if (encoder == null) {
                    encoder = RenderSystem.getDevice().createCommandEncoder();
                }
                for (Map.Entry<String, GpuBuffer> entry : customUniforms.entrySet()) {
                    String layoutKey = passKey + "." + entry.getKey();
                    List<UniformBinding.UboField> layout = bindings.uboLayouts.get(layoutKey);
                    ByteBuffer buf = uboBuffers.get(layoutKey);
                    if (layout != null && buf != null) {
                        fillUboBuffer(layout, setter.values, buf);
                        encoder.writeToBuffer(entry.getValue().slice(), buf);
                    }
                }
            }
        }

        if (shouldRender) {
            chain.process(
                Minecraft.getInstance().getMainRenderTarget(),
                GraphicsResourceAllocator.UNPOOLED
            );
        }

        lastDebugSnapshot = shouldRender
                ? ("active passes=" + activePasses + (firstUniformSnapshot.isEmpty() ? "" : " ubo=" + firstUniformSnapshot))
                : "inactive";

        if (OrangeSunshineClient.getConfig().visual.visualDebugLogging) {
            long tick = ShaderContext.time();
            if (tick % 40 == 0) {
                LOGGER.info(
                        "[OrangeSunshine] shader {} -> {}",
                        resourceId,
                        lastDebugSnapshot
                );
            }
        }
    }

    public void setupDimensions(int targetsWidth, int targetsHeight) {
        this.width = targetsWidth;
        this.height = targetsHeight;
    }

    public int getPassCount() {
        return passes.size();
    }

    public String getDebugSnapshot() {
        return resourceId.getPath() + ": " + lastDebugSnapshot;
    }

    @Override
    public void close() {
        chain.close();
        projMatrix.close();
    }

    // -------------------------------------------------------------------------
    // UBO buffer building
    // -------------------------------------------------------------------------

    private static int computeUboSize(List<UniformBinding.UboField> fields) {
        Std140SizeCalculator calc = new Std140SizeCalculator();
        for (UniformBinding.UboField field : fields) {
            addToCalculator(calc, field.type());
        }
        calc.align(16);
        return calc.get();
    }

    private static void fillUboBuffer(
            List<UniformBinding.UboField> fields,
            Map<String, float[]> values,
            ByteBuffer buf) {
        buf.clear();
        Std140Builder builder = Std140Builder.intoBuffer(buf);
        for (UniformBinding.UboField field : fields) {
            float[] vals = values.getOrDefault(field.name(), new float[field.type().components]);
            writeField(builder, field.type(), vals);
        }
        buf.flip();
    }

    private static void addToCalculator(Std140SizeCalculator calc, UniformBinding.UboFieldType type) {
        switch (type) {
            case FLOAT -> calc.putFloat();
            case INT   -> calc.putInt();
            case VEC2  -> calc.putVec2();
            case VEC3  -> calc.putVec3();
            case VEC4  -> calc.putVec4();
        }
    }

    private static void writeField(Std140Builder builder, UniformBinding.UboFieldType type, float[] vals) {
        switch (type) {
            case FLOAT -> builder.putFloat(vals.length > 0 ? vals[0] : 0f);
            case INT   -> builder.putInt((int)(vals.length > 0 ? vals[0] : 0f));
            case VEC2  -> builder.putVec2(
                vals.length > 0 ? vals[0] : 0f,
                vals.length > 1 ? vals[1] : 0f);
            case VEC3  -> builder.putVec3(
                vals.length > 0 ? vals[0] : 0f,
                vals.length > 1 ? vals[1] : 0f,
                vals.length > 2 ? vals[2] : 0f);
            case VEC4  -> builder.putVec4(
                vals.length > 0 ? vals[0] : 0f,
                vals.length > 1 ? vals[1] : 0f,
                vals.length > 2 ? vals[2] : 0f,
                vals.length > 3 ? vals[3] : 0f);
        }
    }

    private static String buildUniformSnapshot(Map<String, float[]> values) {
        StringBuilder out = new StringBuilder("{");
        int written = 0;
        for (Map.Entry<String, float[]> entry : values.entrySet()) {
            if (written >= 3) {
                out.append(", …");
                break;
            }
            if (written > 0) {
                out.append(", ");
            }
            out.append(entry.getKey()).append("=");
            float[] v = entry.getValue();
            if (v.length == 1) {
                out.append(String.format(java.util.Locale.ROOT, "%.3f", v[0]));
            } else {
                out.append("[");
                for (int i = 0; i < Math.min(3, v.length); i++) {
                    if (i > 0) out.append(",");
                    out.append(String.format(java.util.Locale.ROOT, "%.3f", v[i]));
                }
                if (v.length > 3) out.append(",…");
                out.append("]");
            }
            written++;
        }
        out.append("}");
        return out.toString();
    }

    // -------------------------------------------------------------------------
    // CollectingUniformSetter
    // -------------------------------------------------------------------------

    private static class CollectingUniformSetter implements UniformBinding.UniformSetter {
        final Map<String, float[]> values = new LinkedHashMap<>();
        boolean shouldRender = false;

        final Runnable passRunnable = () -> shouldRender = true;

        @Override
        public void set(String name, float value) {
            values.put(name, new float[]{value});
        }

        @Override
        public void set(String name, float... vs) {
            values.put(name, vs.clone());
        }

        @Override
        public void set(String name, Vector3f v) {
            values.put(name, new float[]{v.x, v.y, v.z});
        }
    }
}
