/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.type;

import java.util.Optional;

import moriz.orangesunshine.PSDamageTypes;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.util.MathUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

/**
 * Created by lukas on 01.11.14.
 */
public class CocaineDrug extends SimpleDrug {
    static final Optional<Component> SLEEP_STATUS = Optional.of(Component.translatable("orangesunshine.sleep.fail.coccaine"));

    public CocaineDrug(double decSpeed, double decSpeedPlus) {
        super(DrugType.COCAINE, decSpeed, decSpeedPlus);
    }

    @Override
    public void update(DrugProperties drugProperties) {
        super.update(drugProperties);

        if (getActiveValue() > 0) {
            Player entity = drugProperties.asEntity();
            RandomSource random = entity.level().random;
            if (!entity.level().isClientSide()) {
                double chance = (getActiveValue() - 0.8F) * 0.1F;

                if (entity.tickCount % 20 == 0 && random.nextFloat() < chance) {
                    entity.hurt(drugProperties.damageOf(random.nextFloat() < 0.4F
                            ? PSDamageTypes.STROKE
                            : random.nextFloat() < 0.5F
                            ? PSDamageTypes.HEART_FAILURE
                            : PSDamageTypes.RESPIRATORY_FAILURE), Integer.MAX_VALUE);
                }
            }
        }
    }

    @Override
    public float heartbeatVolume() {
        return MathUtils.inverseLerp((float) getActiveValue(), 0.4F, 1) + (getTicksActive() * 0.0001F) * 1.2F;
    }

    @Override
    public float heartbeatSpeed() {
        return (float) getActiveValue() * 0.1F + (getTicksActive() * 0.0001F);
    }

    @Override
    public float breathVolume() {
        return MathUtils.inverseLerp((float) getActiveValue(), 0.4f, 1.0f) * 1.5F;
    }

    @Override
    public float breathSpeed() {
        return (float) getActiveValue() * 0.8F;
    }

    @Override
    public float randomJumpChance() {
        return MathUtils.inverseLerp((float) getActiveValue(), 0.6F, 1) * 0.03F;
    }

    @Override
    public float randomPunchChance() {
        return MathUtils.inverseLerp((float) getActiveValue(), 0.5F, 1) * 0.02F;
    }

    @Override
    public float speedModifier() {
        return 1.0F + (float) getActiveValue() * 0.15F;
    }

    @Override
    public float digSpeedModifier() {
        return 1.0F + (float) getActiveValue() * 0.15F;
    }

    @Override
    public Optional<Component> trySleep(BlockPos pos) {
        return getActiveValue() > 0.4
                ? SLEEP_STATUS
                : Optional.empty();
    }

    @Override
    public float desaturationHallucinationStrength() {
        return (float)getActiveValue() * 0.75f;
    }

    @Override
    public float handTrembleStrength() {
        return MathUtils.inverseLerp((float)getActiveValue(), 0.6F, 1);
    }

    @Override
    public float viewTrembleStrength() {
        return MathUtils.inverseLerp((float)getActiveValue(), 0.8F, 1);
    }

    @Override
    public float headMotionInertness() {
        return (float)getActiveValue() * 10;
    }

    @Override
    public float bloomHallucinationStrength() {
        return MathUtils.inverseLerp((float)getActiveValue(), 0, 0.6F) * 1.5F;
    }

    @Override
    public float colorHallucinationStrength() {
        return MathUtils.inverseLerp((float) getActiveValue() * 1.3F, 0.7F, 1) * 0.05F;
    }

    @Override
    public float movementHallucinationStrength() {
        return MathUtils.inverseLerp((float) getActiveValue() * 1.3F, 0.7F, 1) * 0.05F;
    }

    @Override
    public float contextualHallucinationStrength() {
        return MathUtils.inverseLerp((float) getActiveValue() * 1.3F, 0.7F, 1) * 0.05F;
    }
}
