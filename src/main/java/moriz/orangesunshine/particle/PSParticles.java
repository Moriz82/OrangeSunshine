package moriz.orangesunshine.particle;

import com.mojang.serialization.MapCodec;
import moriz.orangesunshine.OrangeSunshine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface PSParticles {
    ParticleType<ExhaledSmokeParticleEffect> EXHALED_SMOKE = register("exhaled_smoke",
            complexType(ExhaledSmokeParticleEffect.CODEC, ExhaledSmokeParticleEffect.STREAM_CODEC));
    ParticleType<BubbleParticleEffect> BUBBLE = register("bubble",
            complexType(BubbleParticleEffect.CODEC, BubbleParticleEffect.STREAM_CODEC));

    static <T extends ParticleOptions> ParticleType<T> complexType(
            MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return new ParticleType<>(false) {
            @Override public MapCodec<T> codec() { return codec; }
            @Override public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() { return streamCodec; }
        };
    }

    static <T extends ParticleType<?>> T register(String name, T type) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, OrangeSunshine.id(name), type);
    }

    static void bootstrap() {}
}
