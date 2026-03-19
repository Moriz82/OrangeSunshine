package moriz.orangesunshine.fluid.alcohol;

import java.util.function.Predicate;

import moriz.orangesunshine.fluid.AlcoholicFluid;
import net.minecraft.advancements.criterion.MinMaxBounds.Ints;
import net.minecraft.util.TriState;
import net.minecraft.world.item.ItemStack;

public record StatePredicate (
        Ints fermentationRange,
        Ints maturationRange,
        Ints distillationRange,
        TriState vinegar
) implements Predicate<ItemStack> {
    public static final StatePredicate ANY_DISTILLED = StatePredicate.builder().distilled().build();
    public static final StatePredicate ANY_FERMENTED = StatePredicate.builder().fermented().build();
    public static final StatePredicate ANY_VINEGAR = StatePredicate.builder().vinegar().build();

    public interface Standard {
        StatePredicate ANY = StatePredicate.builder().vinegar(TriState.DEFAULT).build();
        StatePredicate BASE = StatePredicate.builder().undistilled().unfermented().unmatured().build();
        StatePredicate VINEGAR = StatePredicate.builder().vinegar().build();
        StatePredicate DISTILLED = StatePredicate.builder().distilled().build();
        StatePredicate MATURED = StatePredicate.builder().matured().build();
        StatePredicate FERMENTED_1 = StatePredicate.builder().fermentation(Ints.exactly(1)).build();
        StatePredicate FERMENTED_2 = StatePredicate.builder().fermentation(Ints.atLeast(2)).build();
    }

    @Override
    public boolean test(ItemStack stack) {
        return test(
                AlcoholicFluid.FERMENTATION.get(stack),
                AlcoholicFluid.DISTILLATION.get(stack),
                AlcoholicFluid.MATURATION.get(stack),
                AlcoholicFluid.VINEGAR.get(stack)
        );
    }

    public boolean test(int fermentation, int distillation, int maturation, boolean vinegar) {
        return fermentationRange.matches(fermentation)
                && distillationRange.matches(distillation)
                && maturationRange.matches(maturation)
                && this.vinegar.toBoolean(vinegar) == vinegar;
    }

    public static StatePredicate.Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Ints fermentationRange = Ints.ANY;
        private Ints maturationRange = Ints.ANY;
        private Ints distillationRange = Ints.ANY;
        private TriState vinegar = TriState.FALSE;

        public StatePredicate.Builder vinegar() {
            return vinegar(TriState.TRUE);
        }

        public StatePredicate.Builder vinegar(TriState vinegar) {
            this.vinegar = vinegar;
            return this;
        }

        public StatePredicate.Builder fermentation(Ints range) {
            fermentationRange = range;
            return this;
        }

        public StatePredicate.Builder fermented() {
            return fermentation(Ints.atLeast(1));
        }

        public StatePredicate.Builder unfermented() {
            return fermentation(Ints.exactly(0));
        }

        public StatePredicate.Builder maturation(Ints range) {
            maturationRange = range;
            return this;
        }

        public StatePredicate.Builder matured() {
            return maturation(Ints.atLeast(1));
        }

        public StatePredicate.Builder unmatured() {
            return maturation(Ints.exactly(0));
        }

        public StatePredicate.Builder distillation(Ints range) {
            distillationRange = range;
            return this;
        }

        public StatePredicate.Builder distilled() {
            return distillation(Ints.atLeast(1));
        }

        public StatePredicate.Builder undistilled() {
            return distillation(Ints.exactly(0));
        }

        public StatePredicate build() {
            return new StatePredicate(fermentationRange, maturationRange, distillationRange, vinegar);
        }
    }
}
