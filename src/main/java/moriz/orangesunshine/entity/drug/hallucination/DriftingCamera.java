package moriz.orangesunshine.entity.drug.hallucination;

import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;

public class DriftingCamera {

    private float intensity;

    private double distance;
    private double totalRotation;

    private Vec3 prevPosition = Vec3.ZERO;
    private Vec3 prevRotation = Vec3.ZERO;

    private Vec3 position = Vec3.ZERO;
    private Vec3 velocity = Vec3.ZERO;
    private Vec3 rotation = Vec3.ZERO;

    private double accellerationX;
    private double accellerationY;
    private double accellerationZ;

    public Vec3 getPosition() {
        return position.scale(intensity);
    }

    public Vec3 getRotation() {
        return rotation.scale(intensity);
    }

    public Vec3 getPrevPosition() {
        return prevPosition.scale(intensity);
    }

    public Vec3 getPrevRotation() {
        return prevRotation.scale(intensity);
    }

    public void update(DrugProperties properties) {
        prevPosition = position;
        prevRotation = rotation;

        intensity = Mth.clamp(properties.getModifier(Drug.WEIGHTLESSNESS), 0, 1);
        float weightlessness = properties.getModifier(Drug.WEIGHTLESSNESS) * Mth.sin(properties.getAge() / 100F);

        if (weightlessness != 0) {
            Player player = properties.asEntity();

            Vec3 entityVel = properties.asEntity().getDeltaMovement();
            velocity = velocity.add(0, -0.03, 0);
            velocity = velocity.subtract(
                    Mth.clamp(entityVel.x * 0.001F, -0.2, 0.2),
                    0,
                    Mth.clamp(entityVel.z * 0.001F, -0.2, 0.2)
            ).add(accellerationX, accellerationY, accellerationZ);
            RandomSource random = player.getRandom();

            if (distance > 10) {
                accellerationX = 0;
                accellerationY = (-0.5F / distance) - 0.0001F;
                accellerationZ = 0;
            } else {
                accellerationY = 0;
                if (random.nextFloat() < 0.02) {
                    accellerationX = Math.sin((random.nextFloat() - 0.5) * weightlessness * 2 * Mth.PI) * random.nextFloat() / 3F;
                } else if (random.nextFloat() < 0.02) {
                    accellerationZ = Math.cos((random.nextFloat() - 0.5) * weightlessness * 2 * Mth.PI) * random.nextFloat() / 3F;
                }
            }

            rotation = new Vec3(
                    rotation.x % Mth.PI,
                    rotation.y % Mth.PI,
                    rotation.z % Mth.PI
            ).add(
                    0.001 * Mth.sin(player.tickCount / 200F),
                    0.001 * Mth.sin(player.tickCount / 300F),
                    0.001 * Mth.sin(player.tickCount / 400F)
            );
            totalRotation = rotation.lengthSqr();

            position = position.add(velocity.scale(weightlessness));
            if (position.y > 0) {
                position = position.multiply(1, 0, 1);
            }
            distance = position.lengthSqr();
            velocity = velocity.scale(0.999 / Math.max(distance / 10, 1));
        } else {
            velocity = Vec3.ZERO;
            accellerationX = 0;
            accellerationZ = 0;
            if (distance > 0) {
                position = position.scale(0.9);
                distance = position.lengthSqr();
            }
            if (totalRotation > 0) {
                rotation = rotation.scale(0.9);
                totalRotation = rotation.lengthSqr();
            }
        }
    }
}
