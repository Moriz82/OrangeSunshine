package moriz.orangesunshine.client.particle;

import moriz.orangesunshine.particle.PSParticles;
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.PendingParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;

/**
 * @author Sollace
 * @since 1 Jan 2023
 */
public interface PSParticleFactories {
    static void bootstrap() {
        ParticleFactoryRegistry.getInstance().register(PSParticles.EXHALED_SMOKE, createFactory(ExhaledSmokeParticle::new));
        ParticleFactoryRegistry.getInstance().register(PSParticles.BUBBLE, createFactory(BubbleParticle::new));
    }

    private static <T extends ParticleOptions> PendingParticleFactory<T> createFactory(ParticleSupplier<T> supplier) {
        return provider -> (effect, world, x, y, z, dx, dy, dz, random) -> supplier.get(effect, provider, world, x, y, z, dx, dy, dz, random);
    }

    interface ParticleSupplier<T extends ParticleOptions> {
        Particle get(T effect, FabricSpriteProvider provider, ClientLevel world, double x, double y, double z, double dx, double dy, double dz, RandomSource random);
    }
}
