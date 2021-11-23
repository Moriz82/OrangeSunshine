package com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs;

public class LSDEffect extends Drug {
    public LSDEffect(DrugProperties properties) {
        super(properties);
    }

    @Override
    public void renderTick(DrugEffects drugEffects, float effect) {
        drugEffects.BIG_WAVES.addValue(effect*0.1F);
        drugEffects.SMALL_WAVES.addValue(effect*0.8F);
        drugEffects.WIGGLE_WAVES.addValue(effect*0.312F);
        drugEffects.WORLD_DEFORMATION.addValue(effect*1.5F);
        drugEffects.SATURATION.addValue(effect*2.345F);
        drugEffects.HUE_AMPLITUDE.addValue(effect*0.8F);
        drugEffects.CAMERA_TREMBLE.addValue(effect*0.3F);
        if (effect > 0.4) {
            drugEffects.KALEIDOSCOPE_INTENSITY.addValue((effect - 0.4F)*5.5F);
            drugEffects.RECURSION.addValue((effect - 0.4F)*3.5F);
        }
    }
}
