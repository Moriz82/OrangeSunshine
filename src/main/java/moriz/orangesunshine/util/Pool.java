package moriz.orangesunshine.util;

import net.minecraft.util.RandomSource;

public interface Pool<T> {

    @SafeVarargs
    static <T> Pool<T> create(T... options) {
        return rng -> options[rng.nextInt(options.length)];
    }

    T get(RandomSource rng);
}
