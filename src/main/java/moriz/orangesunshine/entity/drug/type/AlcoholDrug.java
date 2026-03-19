/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.type;

import moriz.orangesunshine.PSDamageTypes;
import moriz.orangesunshine.advancement.PSCriteria;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.util.MathUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * Created by lukas on 01.11.14.
 */
public class AlcoholDrug extends SimpleDrug {
    public AlcoholDrug(DrugType type, double decSpeed, double decSpeedPlus) {
        super(type, decSpeed, decSpeedPlus);
    }

    @Override
    public float viewWobblyness() {
        return (float)getActiveValue() * 0.5F;
    }

    @Override
    public float doubleVision() {
        return MathUtils.inverseLerp((float)getActiveValue(), 0.25f, 1);
    }

    @Override
    public float motionBlur() {
        return MathUtils.inverseLerp((float)getActiveValue(), 0.5f, 1) * 0.3F;
    }

    @Override
    public void update(DrugProperties drugProperties) {
        super.update(drugProperties);

        if (getActiveValue() > 0) {
            Player entity = drugProperties.asEntity();
            RandomSource random = entity.getRandom();

            double activeValue = getActiveValue();

            if ((entity.tickCount % 20) == 0) {
                double damageChance = (activeValue - 0.9F) * 2;

                if (entity.tickCount % 20 == 0 && random.nextFloat() < damageChance) {
                    entity.hurt(PSDamageTypes.create(entity.level(), PSDamageTypes.ALCOHOL_POSIONING), (int)((activeValue - 0.9f) * 50.0f + 4.0f));
                }
            }

            double motionEffect = Math.min(activeValue, 0.8);

            rotateEntityPitch(entity, Mth.sin(entity.tickCount / 600F * (float)Math.PI) / 2F * motionEffect * (random.nextFloat() + 0.5F));
            rotateEntityYaw(entity, Mth.cos(entity.tickCount / 500F * (float)Math.PI) / 1.3F * motionEffect * (random.nextFloat() + 0.5F));

            rotateEntityPitch(entity, Mth.sin(entity.tickCount / 180F * (float)Math.PI) / 3F * motionEffect * (random.nextFloat() + 0.5F));
            rotateEntityYaw(entity, Mth.cos(entity.tickCount / 150F * (float)Math.PI) / 2F * motionEffect * (random.nextFloat() + 0.5F));
        }
    }

    @Override
    public void onWakeUp(DrugProperties drugProperties) {
        double value = getActiveValue();

        if (value > 0) {
            super.onWakeUp(drugProperties);

            Player player = drugProperties.asEntity();
            RandomSource random = player.level().random;

            if (random.nextFloat() > (1 - value)) {
                player.animateHurt(random.nextFloat() * ((float)Math.PI * 2));
                player.playSound(SoundEvents.PLAYER_HURT, 1, 1);
                drugProperties.addToDrug(DrugType.SLEEP_DEPRIVATION, 0.25F);
                PSCriteria.HANGOVER.trigger(player);
            }
        } else {
            super.onWakeUp(drugProperties);
        }
    }
}
