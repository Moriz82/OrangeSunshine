package moriz.orangesunshine.client.particle;

import moriz.orangesunshine.particle.BubbleParticleEffect;
import org.joml.Vector3f;

import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;

public class BubbleParticle extends SingleQuadParticle {
    private final SpriteSet sprites;

    public BubbleParticle(BubbleParticleEffect effect, SpriteSet spriteProvider, ClientLevel world,
                   double x, double y, double z,
                   double vX, double vY, double vZ,
                   RandomSource random) {
        super(world, x, y, z, spriteProvider.first());
        this.sprites = spriteProvider;
        setSpriteFromAge(spriteProvider);
        setSize(0.02F, 0.02F);
        quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
        xd = vX * 0.2F + (Math.random() * 2 - 1) * 0.02F;
        yd = vY * 0.2F + (Math.random() * 2 - 1) * 0.02F;
        zd = vZ * 0.2F + (Math.random() * 2 - 1) * 0.02F;
        lifetime = (int)(8 / (Math.random() * 0.8 + 0.2));

        Vector3f color = effect.getColor();
        rCol = color.x;
        gCol = color.y;
        bCol = color.z;
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (age++ >= lifetime) {
            remove();
            return;
        }
        yd += 0.002;
        move(xd, yd, zd);
        xd *= 0.85F;
        yd *= 0.85F;
        zd *= 0.85F;
        setSpriteFromAge(sprites);
    }

    @Override
    protected Layer getLayer() {
        return Layer.OPAQUE;
    }
}
