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
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.Identifier;

/**
 * Geometry shader injector for world-space visual effects.
 * <p>
 * In 1.21.11 the core shader system was replaced with RenderPipeline, so the old
 * Program.Type-based injection no longer compiles. The source combination logic is
 * preserved for when the pipeline API is ported.
 */
public class GeometryShader {
    private static final String GEO_DIRECTORY = "shaders/geometry/";
    private static final Pattern PS_VARIABLE_PATTERN = Pattern.compile("(^|\\n)ps_([a-z]+ +[a-zA-Z0-9]+) +([^;]+);");
    private static final Identifier BASIC = OrangeSunshine.id("basic");

    public static final GeometryShader INSTANCE = new GeometryShader();

    private Identifier name;
    private boolean isVertex;

    private final Minecraft client = Minecraft.getInstance();
    private final ResourceManager manager = client.getResourceManager();

    private final Map<Identifier, Optional<String>> loadedPrograms = new HashMap<>();

    private final Map<String, Supplier<Object>> samplers = new HashMap<>();

    public void setup(boolean isVertex, String name, InputStream stream, String domain) {
        this.name = Identifier.fromNamespaceAndPath(domain, name);
        this.isVertex = isVertex;
    }

    public boolean isEnabled() {
        return RenderPhase.current() != RenderPhase.NORMAL && client.level != null && client.player != null;
    }

    public boolean isWorld() {
        return (RenderPhase.current() == RenderPhase.WORLD || RenderPhase.current() == RenderPhase.CLOUDS) && client.level != null && client.player != null;
    }

    public Map<String, Supplier<Object>> getSamplers() {
        return samplers;
    }

    public String injectShaderSources(String source) {
        if (isVertex) {
            return loadProgram(name.withPath(p -> GEO_DIRECTORY + p + ".gvsh")).or(() -> {
                return loadProgram(BASIC.withPath(p -> GEO_DIRECTORY + p + ".gvsh"));
            }).map(geometryShaderSources -> {
                return combineSources(source, geometryShaderSources);
            }).orElse(source);
        }

        return loadProgram(name.withPath(p -> p + ".gfsh")).or(() -> {
            return loadProgram(BASIC.withPath(p -> GEO_DIRECTORY + p + ".gfsh"));
        }).map(geometryShaderSources -> {
            return combineSources(source, geometryShaderSources);
        }).orElse(source);
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
}
