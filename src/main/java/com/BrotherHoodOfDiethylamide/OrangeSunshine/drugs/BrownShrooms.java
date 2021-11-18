package com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs;

public class BrownShrooms extends Drug {
    public BrownShrooms(DrugProperties properties) {
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
        if (effect > 0.7) {
            drugEffects.RECURSION.addValue((effect - 0.7F)*15.0F);
        }
    }
}
