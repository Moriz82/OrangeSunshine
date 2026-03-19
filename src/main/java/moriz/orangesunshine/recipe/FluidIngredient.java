package moriz.orangesunshine.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.fluid.container.MutableFluidContainer;
import moriz.orangesunshine.fluid.container.Resovoir;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record FluidIngredient(SimpleFluid fluid, int level, CompoundTag attributes) {
    private static final MapCodec<FluidIngredient> FULL_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SimpleFluid.CODEC.fieldOf("fluid").forGetter(FluidIngredient::fluid),
            Codec.INT.optionalFieldOf("level", -1).forGetter(FluidIngredient::level),
            CompoundTag.CODEC.optionalFieldOf("attributes", new CompoundTag()).forGetter(FluidIngredient::attributes)
    ).apply(instance, FluidIngredient::new));

    public static final Codec<FluidIngredient> CODEC = Codec.either(
            SimpleFluid.CODEC,
            FULL_CODEC.codec()
    ).xmap(either -> either.map(
            fluid -> new FluidIngredient(fluid, -1, new CompoundTag()),
            ingredient -> ingredient
    ), ingredient -> ingredient.level() == -1 && ingredient.attributes().isEmpty()
            ? Either.left(ingredient.fluid())
            : Either.right(ingredient));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidIngredient> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            ingredient -> ingredient.fluid().getId(),
            ByteBufCodecs.VAR_INT,
            FluidIngredient::level,
            ByteBufCodecs.COMPOUND_TAG,
            FluidIngredient::attributes,
            (id, level, attributes) -> new FluidIngredient(SimpleFluid.byId(id), level, attributes)
    );

    public boolean test(Resovoir tank) {
        return test(tank.getContents());
    }

    public boolean test(MutableFluidContainer container) {
        boolean result = true;
        result &= fluid.isEmpty() || container.getFluid() == fluid;
        result &= attributes.isEmpty() || NbtUtils.compareNbt(attributes, container.getAttributes(), true);
        result &= level <= 0 || container.getLevel() >= level;
        return result;
    }

    public boolean test(ItemStack stack) {
        return test(MutableFluidContainer.of(stack));
    }
}
