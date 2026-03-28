/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.type;

import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.util.MathUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * Ketamine - dissociative anesthetic.
 * Low doses: slowed movement, double vision, motion blur
 * High doses: k-hole (desaturation, drowsiness, darkness)
 */
public class KetamineDrug extends SimpleDrug {
    public KetamineDrug(double decSpeed, double decSpeedPlus) {
        super(DrugType.KETAMINE, decSpeed, decSpeedPlus);
    }

    @Override
    public void update(DrugProperties drugProperties) {
        super.update(drugProperties);

        if (getActiveValue() > 0.85) {
            if (!drugProperties.asEntity().level().isClientSide()) {
                drugProperties.asEntity().addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, false, false, false));
            }
        }
    }

    @Override
    public float speedModifier() {
        return 1 - (float) getActiveValue() * 0.4F;
    }

    @Override
    public float digSpeedModifier() {
        return 1 - (float) getActiveValue() * 0.3F;
    }

    @Override
    public float doubleVision() {
        return MathUtils.inverseLerp((float) getActiveValue(), 0.3F, 1) * 0.6F;
    }

    @Override
    public float motionBlur() {
        return MathUtils.inverseLerp((float) getActiveValue(), 0.4F, 1) * 0.5F;
    }

    @Override
    public float viewWobblyness() {
        return (float) getActiveValue() * 0.3F;
    }

    @Override
    public float headMotionInertness() {
        return (float) getActiveValue() * 12;
    }

    @Override
    public float desaturationHallucinationStrength() {
        return MathUtils.inverseLerp((float) getActiveValue(), 0.6F, 1) * 0.8F;
    }

    @Override
    public float drowsyness() {
        return MathUtils.inverseLerp((float) getActiveValue(), 0.7F, 1) * 0.6F;
    }
}
