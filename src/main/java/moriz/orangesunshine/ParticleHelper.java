package moriz.orangesunshine;

import java.util.function.Supplier;

import org.joml.Vector3f;

import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import moriz.orangesunshine.particle.ExhaledSmokeParticleEffect;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface ParticleHelper {

    static void spawnColoredParticle(Entity entity, Vector3f color, Vec3 direction, float speed, float size) {
        Vec3 velocity = entity.getDeltaMovement().add(direction.normalize().scale(speed));
        Vec3 pos = entity.getEyePosition();
        entity.level().addParticle(new ExhaledSmokeParticleEffect(color, 1),
                pos.x, pos.y - 0.1F, pos.z,
                velocity.x, velocity.y + 0.03F, velocity.z);
    }

    static void spawnParticles(Level world, ParticleOptions effect, Supplier<Vec3> pos, Supplier<Vec3> vel, int count) {
        for (int i = 0; i < count; i++) {
            Vec3 position = pos.get();
            Vec3 velocity = vel.get();
            if (world instanceof ServerLevel sw) {
                sw.sendParticles(effect, position.x, position.y, position.z, 1, velocity.x, velocity.y, velocity.z, 0);
            } else {
                world.addParticle(effect, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
            }
        }
    }

    static Vec3 apply(Vec3 vector, Double2DoubleFunction function) {
        return new Vec3(
                function.applyAsDouble(vector.x),
                function.applyAsDouble(vector.y),
                function.applyAsDouble(vector.z)
        );
    }
}
