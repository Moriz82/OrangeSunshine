package moriz.orangesunshine.client.render.shader;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.PackResources;
import net.minecraft.resources.Identifier;

class ModdedResourceManager implements ResourceManager {
    private final ResourceManager parent;
    private final String defaultNamespace;
    private final ModdedResourceProvider proxy;

    public ModdedResourceManager(ResourceManager parent, String defaultNamespace) {
        this.parent = parent;
        this.defaultNamespace = defaultNamespace;
        this.proxy = new ModdedResourceProvider(parent, defaultNamespace);
    }

    @Override
    public Optional<Resource> getResource(Identifier id) {
        return proxy.getResource(id);
    }

    @Override
    public Set<String> getNamespaces() {
        return parent.getNamespaces();
    }

    @Override
    public List<Resource> getResourceStack(Identifier id) {
        List<Resource> resources = parent.getResourceStack(Identifier.fromNamespaceAndPath(defaultNamespace, id.getPath()));
        if (resources.isEmpty()) {
            return parent.getResourceStack(id);
        }
        return resources;
    }

    @Override
    public Map<Identifier, Resource> listResources(String path, Predicate<Identifier> filter) {
        return parent.listResources(path, filter);
    }

    @Override
    public Map<Identifier, List<Resource>> listResourceStacks(String path, Predicate<Identifier> filter) {
        return parent.listResourceStacks(path, filter);
    }

    @Override
    public Stream<PackResources> listPacks() {
        return parent.listPacks();
    }
}
