package moriz.orangesunshine.entity.drug.hallucination;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import moriz.orangesunshine.util.Pool;
import org.jetbrains.annotations.Nullable;

import moriz.orangesunshine.entity.drug.DrugType;
import net.minecraft.advancements.criterion.MinMaxBounds.Doubles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;

public record EntityHallucinationType (Identifier id, Function<Player, Hallucination> factory, float chance, Doubles strengthRange, @Nullable Predicate<EntityHallucinationList> condition) {
    public static final Map<Identifier, EntityHallucinationType> REGISTRY = new HashMap<>();

    static {
        register(HallucinationTypeKeys.RASTA_HEAD, RastaHeadHallucination::new, 0.1F, Doubles.ANY, list -> {
            return list.getNumberOfHallucinations(a -> a instanceof RastaHeadHallucination) == 0 && list.getProperties().getDrugValue(DrugType.CANNABIS) > 0.4F;
        });
        register(HallucinationTypeKeys.MULTIPLE_ENTITY, MultipleEntityHallucination::new, 0.5F, Doubles.ANY);
        register(HallucinationTypeKeys.SINGLE_ENTITY, EntityHallucination::new, 1, Doubles.ANY);
        register(HallucinationTypeKeys.HOSTILE_VILLAGERS, (player -> new EntityIdentitySwapHallucination(player, EntityType.VILLAGER, Pool.create(EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER))), 1, Doubles.atLeast(0.8F));
        register(HallucinationTypeKeys.FRIENDLY_ZOMBIES, (player -> new EntityIdentitySwapHallucination(player, EntityType.ZOMBIE, Pool.create(EntityType.VILLAGER))), 1, Doubles.atLeast(0.8F));
    }

    public static Stream<EntityHallucinationType> getCandidates(EntityHallucinationList list) {
        RandomSource weight = list.getProperties().asEntity().getRandom();
        return REGISTRY.values().stream()
                .filter(i -> i.strengthRange().matches(list.getManager().getHallucinationStrength(1))
                            && weight.nextFloat() <= i.chance()
                            && (i.condition() == null || i.condition().test(list)
                ));
    }

    static void register(Identifier id, Function<Player, Hallucination> factory, float chance, Doubles strengthRange) {
        register(id, factory, chance, strengthRange, null);
    }

    static void register(Identifier id, Function<Player, Hallucination> factory, float chance, Doubles strengthRange, @Nullable Predicate<EntityHallucinationList> condition) {
        REGISTRY.put(id, new EntityHallucinationType(id, factory, chance, strengthRange, condition));
    }
}
