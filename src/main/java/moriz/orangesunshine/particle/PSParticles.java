package moriz.orangesunshine.particle;

import moriz.orangesunshine.OrangeSunshine;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface PSParticles {
    ParticleType<ExhaledSmokeParticleEffect> EXHALED_SMOKE = register("exhaled_smoke", FabricParticleTypes.complex(ExhaledSmokeParticleEffect.FACTORY));
    ParticleType<BubbleParticleEffect> BUBBLE = register("bubble", FabricParticleTypes.complex(BubbleParticleEffect.FACTORY));

    static <T extends ParticleType<?>> T register(String name, T type) {
        return Registry.register(Registries.PARTICLE_TYPE, OrangeSunshine.id(name), type);
    }

    static void bootstrap() {}
}
