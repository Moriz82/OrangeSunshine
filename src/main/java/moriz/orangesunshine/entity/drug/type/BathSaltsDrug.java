/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.type;

import moriz.orangesunshine.PSDamageTypes;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.util.MathUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * Created by Sollace on Feb 6 2023.
 */
public class BathSaltsDrug extends SimpleDrug {
    public BathSaltsDrug(double decSpeed, double decSpeedPlus) {
        super(DrugType.BATH_SALTS, decSpeed, decSpeedPlus);
    }

    @Override
    public void update(DrugProperties drugProperties) {
        super.update(drugProperties);

        if (getActiveValue() > 0) {
            Player entity = drugProperties.asEntity();
            RandomSource random = entity.level().random;

            if (!entity.level().isClientSide()) {
                double chance = (getActiveValue() - 0.8F) * 0.051F;

                if (entity.tickCount % 20 == 0 && random.nextFloat() < chance) {
                    if (random.nextFloat() < 0.4F) {
                        entity.hurt(drugProperties.damageOf(PSDamageTypes.STROKE), Integer.MAX_VALUE);
                    } else if (random.nextFloat() < 0.5F) {
                        entity.hurt(drugProperties.damageOf(PSDamageTypes.HEART_FAILURE), Integer.MAX_VALUE);
                    } else if (random.nextFloat() < 0.5F) {
                        entity.hurt(drugProperties.damageOf(PSDamageTypes.RESPIRATORY_FAILURE), Integer.MAX_VALUE);
                    } else if (random.nextFloat() < 0.5F) {
                        entity.hurt(drugProperties.damageOf(PSDamageTypes.KIDNEY_FAILURE), Integer.MAX_VALUE);
                    }
                }
            }
        }
    }

    @Override
    public void onWakeUp(DrugProperties drugProperties) {
        if (getActiveValue() > 0) {
            RandomSource random = drugProperties.asEntity().level().random;

            if (random.nextFloat() < 0.5) {
                drugProperties.asEntity().hurt(
                        drugProperties.damageOf(random.nextFloat() < 0.002 ? PSDamageTypes.KIDNEY_FAILURE : PSDamageTypes.IN_SLEEP),
                        Integer.MAX_VALUE
                );
            } else {
                drugProperties.asEntity().addEffect(new MobEffectInstance(MobEffects.NAUSEA, 300, 0, false, false, false));
                super.onWakeUp(drugProperties);
            }
        } else {
            super.onWakeUp(drugProperties);
        }
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
    public float colorHallucinationStrength() {
        return (float) getActiveValue() * 0.8F;
    }

    @Override
    public float movementHallucinationStrength() {
        return (float) getActiveValue();
    }

    @Override
    public float bloomHallucinationStrength() {
        return (float) getActiveValue() * 0.12F;
    }

    @Override
    public float colorInversionHallucinationStrength() {
        float value = (float) getActiveValue();
        value *= value;
        return Mth.clamp(value * 5.3F, 0, 1.5F);
    }
}
