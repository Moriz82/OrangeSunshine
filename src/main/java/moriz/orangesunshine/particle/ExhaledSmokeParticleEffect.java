package moriz.orangesunshine.particle;

import org.joml.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

public class ExhaledSmokeParticleEffect extends DustParticleOptions {
    public static final MapCodec<ExhaledSmokeParticleEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(ExhaledSmokeParticleEffect::packedColor),
            Codec.FLOAT.fieldOf("scale").forGetter(ExhaledSmokeParticleEffect::getScale)
    ).apply(instance, ExhaledSmokeParticleEffect::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExhaledSmokeParticleEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ExhaledSmokeParticleEffect::packedColor,
            ByteBufCodecs.FLOAT,
            ExhaledSmokeParticleEffect::getScale,
            ExhaledSmokeParticleEffect::new
    );

    private final int packedColor;

    public ExhaledSmokeParticleEffect(int packedColor, float scale) {
        super(packedColor, scale);
        this.packedColor = packedColor;
    }

    public ExhaledSmokeParticleEffect(Vector3f color, float scale) {
        this(pack(color), scale);
    }

    public int packedColor() {
        return packedColor;
    }

    private static int pack(Vector3f color) {
        int red = Mth.clamp((int)(color.x() * 255.0F), 0, 255);
        int green = Mth.clamp((int)(color.y() * 255.0F), 0, 255);
        int blue = Mth.clamp((int)(color.z() * 255.0F), 0, 255);
        return (red << 16) | (green << 8) | blue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ParticleType<DustParticleOptions> getType() {
        return (ParticleType<DustParticleOptions>)(ParticleType<?>)PSParticles.EXHALED_SMOKE;
    }
}
