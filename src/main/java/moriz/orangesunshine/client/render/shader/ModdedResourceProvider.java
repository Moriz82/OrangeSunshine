package moriz.orangesunshine.client.render.shader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

import moriz.orangesunshine.OrangeSunshine;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.resources.Identifier;

class ModdedResourceProvider implements ResourceProvider {
    private final ResourceProvider parent;
    private final String defaultNamespace;

    private final Map<Identifier, List<String>> loadedImports = new HashMap<>();

    public ModdedResourceProvider(ResourceProvider parent, String defaultNamespace) {
        this.parent = parent;
        this.defaultNamespace = defaultNamespace;
    }

    @Override
    public Optional<Resource> getResource(Identifier id) {
        final Identifier overrideId = Identifier.fromNamespaceAndPath(defaultNamespace, id.getPath());
        return parent.getResource(overrideId).map(r -> loadShader(r, overrideId))
             .or(() -> parent.getResource(id).map(r -> loadShader(r, id)));
    }

    private Resource loadShader(Resource resource, Identifier id) {
        if (id.getPath().endsWith(".vsh") || id.getPath().endsWith(".fsh")) {
            return new Resource(resource.source(), () -> {
                try (var reader = resource.openAsReader()) {
                    return new ByteArrayInputStream(reader.lines().toList().stream()
                            .flatMap(this::processImport)
                            .collect(ByteArrayOutputStream::new, (buf, line) -> {
                                try {
                                    buf.write(line.getBytes(StandardCharsets.UTF_8));
                                    buf.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
                                } catch (IOException e) {
                                    throw new UncheckedIOException(e);
                                }
                            }, (a, b) -> {})
                            .toByteArray());
                }
            }, resource::metadata);
        }
        return resource;
    }

    private Stream<String> processImport(String line) {
        if (line.startsWith("#moj_import <") && line.endsWith(">")) {
            Identifier importPath = Identifier.tryParse(line.split("#moj_import <")[1].replace(">", ""));
            if (importPath != null) {
                importPath = importPath.withPrefix("shaders/include/");
                final Identifier finalImportPath = importPath;
                return loadedImports.computeIfAbsent(importPath, p -> {
                    return parent.getResource(finalImportPath).or(() -> {
                        OrangeSunshine.LOGGER.error("Failed to locate import: {}", line);
                        return Optional.empty();
                    }).stream().filter(Objects::nonNull).flatMap(resource -> {
                        try (var reader = resource.openAsReader()) {
                            return reader.lines().map(line2 -> line2.startsWith("#version") ? "/* " + line2 + " */" : line2).toList().stream();
                        } catch (IOException e) {
                            return Stream.empty();
                        }
                    }).toList();
                }).stream();
            }
            System.out.println("Failed to locate import: " + line);
            return Stream.of("/*" + line + "*/");
        }
        return Stream.of(line);
    }
}
