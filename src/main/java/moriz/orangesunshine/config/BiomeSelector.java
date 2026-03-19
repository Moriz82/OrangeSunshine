package moriz.orangesunshine.config;

import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;

public interface BiomeSelector {
    Predicate<BiomeSelectionContext> ALL = BiomeSelectors.all();
    Predicate<BiomeSelectionContext> NONE = ctx -> false;
    Predicate<BiomeSelectionContext> COLD = ctx -> ctx.getBiome().getBaseTemperature() < 0.15F;
    Predicate<BiomeSelectionContext> DRY = ctx -> !ctx.getBiome().hasPrecipitation();

    static Predicate<BiomeSelectionContext> compile(String[] included, String[] excluded, Predicate<BiomeSelectionContext> dynamicInclusion) {
        var include = compile(included, NONE, Stream::allMatch).or(dynamicInclusion);
        var exclude = compile(excluded, ALL, Stream::noneMatch);

        return include.and(exclude);
    }

    static Predicate<BiomeSelectionContext> compile(String[] predicates, Predicate<BiomeSelectionContext> fallback,
            BiPredicate<Stream<Predicate<BiomeSelectionContext>>, Predicate<Predicate<BiomeSelectionContext>>> combiner) {

        if (predicates == null || predicates.length == 0) {
            return fallback;
        }

        var selectors = Arrays.stream(predicates).map(BiomeSelector::compile).toList();

        if (selectors.size() == 1) {
            return selectors.get(0);
        }
        return ctx -> combiner.test(selectors.stream(), a -> a.test(ctx));
    }

    static Predicate<BiomeSelectionContext> compile(String selector) {
        if (selector.startsWith("#")) {
            return BiomeSelectors.tag(TagKey.create(Registries.BIOME, Identifier.parse(selector.substring(1))));
        }

        return ofId(Identifier.parse(selector));
    }

    static Predicate<BiomeSelectionContext> ofId(Identifier biomeId) {
        return ctx -> ctx.getBiomeRegistryEntry().is(biomeId);
    }
}
