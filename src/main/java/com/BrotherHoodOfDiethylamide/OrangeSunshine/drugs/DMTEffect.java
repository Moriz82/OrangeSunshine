package com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs;

public class DMTEffect extends Drug {
    public DMTEffect(DrugProperties properties) {
        super(properties);
    }

    @Override
    public void renderTick(DrugEffects drugEffects, float effect) {
        drugEffects.BIG_WAVES.addValue(effect*5.0F);
        drugEffects.SMALL_WAVES.addValue(effect*2.4F);
        drugEffects.WIGGLE_WAVES.addValue(effect*5.4F);
        drugEffects.WORLD_DEFORMATION.addValue(effect*5.7F);
        drugEffects.SATURATION.addValue(effect*8.0F);
        drugEffects.HUE_AMPLITUDE.addValue(effect*9.4F);
        drugEffects.CAMERA_TREMBLE.addValue(effect*1.5F);
        drugEffects.KALEIDOSCOPE_INTENSITY.addValue(effect*5.0F);
        drugEffects.RECURSION.addValue(effect*30.0F);
    }
}