package moriz.orangesunshine.particle;

import moriz.orangesunshine.OrangeSunshine;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;

public interface PSParticles {
    ParticleType<ExhaledSmokeParticleEffect> EXHALED_SMOKE = register("exhaled_smoke",
            FabricParticleTypes.complex(ExhaledSmokeParticleEffect.CODEC, ExhaledSmokeParticleEffect.STREAM_CODEC));
    ParticleType<BubbleParticleEffect> BUBBLE = register("bubble",
            FabricParticleTypes.complex(BubbleParticleEffect.CODEC, BubbleParticleEffect.STREAM_CODEC));

    static <T extends ParticleType<?>> T register(String name, T type) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, OrangeSunshine.id(name), type);
    }

    static void bootstrap() {}
}
