package moriz.orangesunshine.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Locale;
import java.util.Map;
import moriz.orangesunshine.fluid.container.FluidContainer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

public record FluidModifyingResult(Map<String, Modification> attributes, ItemStack result) {
    public static final Codec<FluidModifyingResult> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, Modification.CODEC)
                    .optionalFieldOf("attributes", Map.of())
                    .forGetter(FluidModifyingResult::attributes),
            ItemStack.OPTIONAL_CODEC.optionalFieldOf("result", ItemStack.EMPTY).forGetter(FluidModifyingResult::result)
    ).apply(instance, FluidModifyingResult::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidModifyingResult> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistriesTrusted(CODEC);

    public ItemStack applyTo(ItemStack input) {
        ItemStack stack = result.isEmpty() ? input.copyWithCount(1) : result.copy();
        if (!attributes.isEmpty()) {
            FluidContainer.updateFluidAttributes(stack, tag ->
                    attributes.forEach((key, modifier) -> tag.putInt(key, modifier.applyAsInt(tag.getIntOr(key, 0))))
            );
        }
        return stack;
    }

    interface Op {
        int apply(int a, int b);
    }

    public enum Ops implements Op, StringRepresentable {
        SET((a, b) -> b),
        ADD((a, b) -> a + b),
        SUBTRACT((a, b) -> a - b),
        MULTIPLY((a, b) -> a * b),
        DIVIDE((a, b) -> a / b);

        private static final Codec<Ops> CODEC = StringRepresentable.fromEnum(Ops::values);

        private final String serializedName;
        private final Op operation;

        Ops(Op operation) {
            this.serializedName = name().toLowerCase(Locale.ROOT);
            this.operation = operation;
        }

        @Override
        public int apply(int a, int b) {
            return operation.apply(a, b);
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }
    }

    public record Modification(int value, Ops type) implements Int2IntFunction {
        private static final Codec<Modification> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("value").forGetter(Modification::value),
                Ops.CODEC.optionalFieldOf("type", Ops.ADD).forGetter(Modification::type)
        ).apply(instance, Modification::new));

        @Override
        public int get(int currentValue) {
            return type.apply(currentValue, value);
        }
    }
}
