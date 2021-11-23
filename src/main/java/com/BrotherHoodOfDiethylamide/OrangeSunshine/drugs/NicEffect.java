package com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs;

public class NicEffect  extends Drug {
    public NicEffect(DrugProperties properties) {
        super(properties);
    }

    @Override
    public void renderTick(DrugEffects drugEffects, float effect) {
        drugEffects.SMALL_WAVES.addValue(effect*0.1F);
        drugEffects.WORLD_DEFORMATION.addValue(effect*0.1F);
        drugEffects.SATURATION.addValue(effect*0.1F);
        drugEffects.HUE_AMPLITUDE.addValue(effect*0.1F);
        drugEffects.CAMERA_TREMBLE.addValue(effect*0.1F);
        drugEffects.BRIGHTNESS.addValue(effect*0.5F);
    }
}
