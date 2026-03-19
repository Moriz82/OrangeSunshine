package moriz.orangesunshine.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import moriz.orangesunshine.util.CodecUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record OptionalFluidIngredient(Optional<FluidIngredient> fluid, Optional<Ingredient> receptical)
        implements Predicate<ItemStack> {
    public static final OptionalFluidIngredient EMPTY = new OptionalFluidIngredient(Optional.empty(), Optional.empty());

    private static final Codec<Ingredient> OPTIONAL_RECEPTICAL_CODEC = Codec.of(new Encoder<>() {
            @Override
            public <T> DataResult<T> encode(Ingredient ingredient, DynamicOps<T> ops, T prefix) {
                return ingredient.isEmpty()
                        ? DataResult.success(ops.emptyMap())
                        : Ingredient.CODEC.encode(ingredient, ops, prefix);
            }
        },
            Ingredient.CODEC
    );

    public static final Codec<OptionalFluidIngredient> CODEC = CodecUtils.extend(
            OPTIONAL_RECEPTICAL_CODEC,
            FluidIngredient.CODEC.fieldOf("fluid")
    ).xmap(
            pair -> new OptionalFluidIngredient(pair.getSecond(), pair.getFirst()),
            ingredient -> Pair.of(ingredient.receptical(), ingredient.fluid())
    );

    public static final Codec<NonNullList<OptionalFluidIngredient>> LIST_CODEC = CODEC.listOf().xmap(
            values -> {
                NonNullList<OptionalFluidIngredient> list = NonNullList.createWithCapacity(values.size());
                list.addAll(values);
                return list;
            },
            List::copyOf
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, OptionalFluidIngredient> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(FluidIngredient.STREAM_CODEC),
            OptionalFluidIngredient::fluid,
            ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC),
            OptionalFluidIngredient::receptical,
            OptionalFluidIngredient::new
    );

    public boolean isEmpty() {
        return fluid.isEmpty() && receptical.isEmpty();
    }

    @Override
    public boolean test(ItemStack stack) {
        return fluid.map(value -> value.test(stack)).orElse(true)
                && receptical.map(value -> value.test(stack)).orElse(true);
    }
}
