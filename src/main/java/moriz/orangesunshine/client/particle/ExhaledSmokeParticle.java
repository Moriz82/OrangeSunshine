package moriz.orangesunshine.client.particle;

import moriz.orangesunshine.particle.ExhaledSmokeParticleEffect;
import org.joml.Vector3f;

import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;

public class ExhaledSmokeParticle extends SmokeParticle {
    public ExhaledSmokeParticle(ExhaledSmokeParticleEffect effect, SpriteSet spriteProvider, ClientLevel world,
                                double x, double y, double z,
                                double vX, double vY, double vZ,
                                RandomSource random) {
        super(world, x, y, z, vX, vY, vZ, 1, spriteProvider);
        Vector3f color = effect.getColor();
        rCol = color.x;
        gCol = color.y;
        bCol = color.z;
    }
}
