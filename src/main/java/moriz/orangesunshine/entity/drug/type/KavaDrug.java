/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.type;

import moriz.orangesunshine.PSDamageTypes;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.util.MathUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * Created by Sollace on Feb 17 2023.
 *
 * Drowsiness, slurred speech, poor concentration, confusion, dizziness, problems with movement and memory, lowered blood pressure, slowed breathing.
 *
 */
public class KavaDrug extends SimpleDrug {
    public KavaDrug(double decSpeed, double decSpeedPlus) {
        super(DrugType.KAVA, decSpeed, decSpeedPlus);
    }

    @Override
    public float viewWobblyness() {
        return (float)getActiveValue() * 0.5F;
    }

    @Override
    public float drowsyness() {
        return (float)getActiveValue() * 0.4F;
    }

    @Override
    public float soundVolumeModifier() {
        return 1 - (float)getActiveValue();
    }

    @Override
    public float speedModifier() {
        return (float) getActiveValue() * -0.2F;
    }

    @Override
    public float digSpeedModifier() {
        return (float) getActiveValue() * -0.2F;
    }

    @Override
    public float desaturationHallucinationStrength() {
        float strength = (Mth.clamp(getTicksActive(), 50, 250) - 50) / 200F;
        return (float)getActiveValue() * 0.75f * strength;
    }

    @Override
    public float heartbeatVolume() {
        float strength = (Mth.clamp(getTicksActive(), 50, 250) - 50) / 200F;
        return MathUtils.inverseLerp((float) getActiveValue(), 0.4F, 1) * 1.2F * strength;
    }

    @Override
    public void update(DrugProperties drugProperties) {
        super.update(drugProperties);

        if (getActiveValue() > 0) {
            Player entity = drugProperties.asEntity();
            RandomSource random = entity.getRandom();

            double activeValue = getActiveValue();

            if ((entity.tickCount % 20) == 0 && getTicksActive() > 100) {
                double damageChance = (activeValue - 1.3F) * 2;

                if (entity.tickCount % 10 == 0 && random.nextFloat() < damageChance) {
                    entity.hurt(drugProperties.damageOf(PSDamageTypes.HEART_FAILURE), (int)((activeValue - 0.9f) * 50.0f + 4.0f));
                }
            }

            double motionEffect = Math.min(activeValue, 0.8);

            rotateEntityPitch(entity, Mth.sin(entity.tickCount / 600F * (float)Math.PI) / 2F * motionEffect * (random.nextFloat() + 0.5F));
            rotateEntityYaw(entity, Mth.cos(entity.tickCount / 500F * (float)Math.PI) / 1.3F * motionEffect * (random.nextFloat() + 0.5F));

            rotateEntityPitch(entity, Mth.sin(entity.tickCount / 180F * (float)Math.PI) / 3F * motionEffect * (random.nextFloat() + 0.5F));
            rotateEntityYaw(entity, Mth.cos(entity.tickCount / 150F * (float)Math.PI) / 2F * motionEffect * (random.nextFloat() + 0.5F));
        }
    }
}
