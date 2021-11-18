package com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs;

public class RedShrooms extends Drug {
    public RedShrooms(DrugProperties properties) {
        super(properties);
    }

    @Override
    public void renderTick(DrugEffects drugEffects, float effect) {
        drugEffects.BIG_WAVES.addValue(effect*0.3F);
        drugEffects.SMALL_WAVES.addValue(effect*0.4F);
        drugEffects.WIGGLE_WAVES.addValue(effect*0.4F);
        drugEffects.WORLD_DEFORMATION.addValue(effect*0.7F);
        drugEffects.SATURATION.addValue(effect*2.0F);
        drugEffects.HUE_AMPLITUDE.addValue(effect*0.4F);
        drugEffects.CAMERA_TREMBLE.addValue(effect*0.5F);
        if (effect > 0.4) {
            drugEffects.KALEIDOSCOPE_INTENSITY.addValue((effect - 0.4F)*7.5F);
        }
    }
}
