package moriz.orangesunshine.client.render.shader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import moriz.orangesunshine.OrangeSunshine;
import org.apache.commons.io.IOUtils;
import moriz.orangesunshine.client.render.RenderPhase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.opengl.Uniform;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class GeometryShader {
    private static final String GEO_DIRECTORY = "shaders/geometry/";
    private static final Pattern PS_VARIABLE_PATTERN = Pattern.compile("(^|\\n)ps_([a-z]+ +[a-zA-Z0-9]+) +([^;]+);");
    private static final Identifier BASIC = OrangeSunshine.id("basic");

    public static final GeometryShader INSTANCE = new GeometryShader();

    private Identifier name;
    private Program.Type type;

    private final Minecraft client = Minecraft.getInstance();
    private final ResourceManager manager = client.getResourceManager();

    private final Map<Identifier, Optional<String>> loadedPrograms = new HashMap<>();


    private final Map<String, Supplier<Object>> samplers = Util.make(new HashMap<>(), map -> {
        map.put("PS_DepthSampler", () -> Minecraft.getInstance().getMainRenderTarget().getDepthTextureId());
        map.put("PS_SurfaceFractalSampler", () -> Minecraft.getInstance().getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS));
    });


    public void setup(Program.Type type, String name, InputStream stream, String domain) {
        this.name = Identifier.fromNamespaceAndPath(domain, name);
        this.type = type;
    }

    public boolean isEnabled() {
        return RenderPhase.current() != RenderPhase.NORMAL && client.level != null && client.player != null;
    }

    public boolean isWorld() {
        return (RenderPhase.current() == RenderPhase.WORLD || RenderPhase.current() == RenderPhase.CLOUDS) && client.level != null && client.player != null;
    }

    /*
    public void addUniforms(ShaderProgramSetupView program, Consumer<Uniform> register) {
        register.accept(new BoundUniform("PS_SurfaceFractalStrength", Uniform.getTypeIndex("float"), 1, program, uniform -> {
            uniform.set(isEnabled() ? Mth.clamp(ShaderContext.hallucinations().getSurfaceFractalStrength(ShaderContext.tickDelta()), 0, 1) : 0);
        }));
        // ... rest of uniforms
    }
    */

    public Map<String, Supplier<Object>> getSamplers() {
        return samplers;
    }

    public String injectShaderSources(String source) {
        if (type == Program.Type.VERTEX) {
            return loadProgram(name.withPath(p -> GEO_DIRECTORY + p + ".gvsh")).or(() -> {
                return loadProgram(BASIC.withPath(p -> GEO_DIRECTORY + p + ".gvsh"));
            }).map(geometryShaderSources -> {
                return combineSources(source, geometryShaderSources);
            }).orElse(source);
        }

        if (type == Program.Type.FRAGMENT) {
            return loadProgram(name.withPath(p -> p + ".gfsh")).or(() -> {
                return loadProgram(BASIC.withPath(p -> GEO_DIRECTORY + p + ".gfsh"));
            }).map(geometryShaderSources -> {
                return combineSources(source, geometryShaderSources);
            }).orElse(source);
        }

        return source;
    }

    private String combineSources(String vertexSources, String geometrySources) {
        geometrySources = PS_VARIABLE_PATTERN.matcher(geometrySources).replaceAll(match -> {
            String fieldSlug = Arrays.stream(match.group(3).split(","))
                    .map(String::trim)
                    .filter(field -> !vertexSources.contains(field))
                    .collect(Collectors.joining(", "));
            return fieldSlug.isEmpty() ? "/* " + match.group(0) + "*/" : match.group(2) + " " + fieldSlug + ";";
        });

        String newline = System.lineSeparator();
        return vertexSources.replace("void main()", "void i_parent_shaders_main()" + newline) + newline + geometrySources;
    }

    private Optional<String> loadProgram(Identifier id) {
        loadedPrograms.clear();
        return loadedPrograms.computeIfAbsent(id, i -> {
            return manager.getResource(i).map(res -> {
                try (var stream = res.open()) {
                    return IOUtils.toString(stream, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    return null;
                }
            });
        });
    }

    /*
    static class BoundUniform extends Uniform {
        private final Consumer<Uniform> valueGetter;

        public BoundUniform(String name, int dataType, int count, ShaderProgramSetupView program, Consumer<Uniform> valueGetter) {
            super(name, dataType, count, program);
            this.valueGetter = valueGetter;
        }

        @Override
        public void upload() {
            if (!Minecraft.getInstance().isPaused()) {
                valueGetter.accept(this);
            }
            super.upload();
        }
    }
    */
}
