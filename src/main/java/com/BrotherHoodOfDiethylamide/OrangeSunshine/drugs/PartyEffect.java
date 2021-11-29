package com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class PartyEffect extends Drug {
    public PartyEffect(DrugProperties properties) {
        super(properties);
    }

    @Override
    public void startUse(PlayerEntity player) {
    }

    @Override
    public void renderTick(DrugEffects drugEffects, float effect) {
        drugEffects.CAMERA_TREMBLE.addValue(effect*0.5F);
        drugEffects.WIGGLE_WAVES.addValue(effect*0.4F);
        drugEffects.BRIGHTNESS.addValue(effect*0.2F);
        drugEffects.SATURATION.addValue(effect*5.0F);
        drugEffects.HUE_AMPLITUDE.addValue(effect*2.8F);
        drugEffects.WORLD_DEFORMATION.addValue(effect*0.8F);
        if (effect > 0.5F) {
            float f = effect - 0.5F;
            drugEffects.WATER_DISTORT.addValue(Math.min(f*f, 0.08F));
            drugEffects.SATURATION.addValue(f);
        }
    }

    @Override
    public void effectTick(PlayerEntity player, DrugEffects drugEffects, float effect) {
        player.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 35, 0, true, true));
        if (effect == 1F) drugEffects.DROWN_RATE.addValue(3F);
    }
}

