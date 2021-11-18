package com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs;

import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class DrugEffects {
    private final List<DrugEffect> EFFECTS = new ArrayList<>();

    public final FloatDrugEffect CAMERA_TREMBLE = registerClient();
    public final FloatDrugEffect CAMERA_INERTIA = registerClient();
    public final FloatDrugEffect HAND_TREMBLE = registerClient();
    public final FloatDrugEffect BIG_WAVES = registerClient();
    public final FloatDrugEffect SMALL_WAVES = registerClient();
    public final FloatDrugEffect WIGGLE_WAVES = registerClient();
    public final FloatDrugEffect WORLD_DEFORMATION = registerClient();
    public final FloatDrugEffect KALEIDOSCOPE_INTENSITY = registerClient();
    public final FloatDrugEffect SATURATION = registerClient();
    public final FloatDrugEffect HUE_AMPLITUDE = registerClient();
    public final FloatDrugEffect BUMPY = registerClient();
    public final FloatDrugEffect BRIGHTNESS = registerClient();
    public final FloatDrugEffect BLOOM_RADIUS = registerClient();
    public final FloatDrugEffect BLOOM_THRESHOLD = registerClient();
    public final FloatDrugEffect RECURSION = registerClient();
    public final FloatDrugEffect WATER_DISTORT = registerClient();

    public final FloatDrugEffect MOVEMENT_SPEED = register();
    public final FloatDrugEffect DIG_SPEED = register();
    public final FloatDrugEffect DROWN_RATE = register();
    public final FloatDrugEffect REGENERATION_RATE = register();
    public final FloatDrugEffect HUNGER_RATE = register();


    private FloatDrugEffect register(boolean isClientOnly) {
        FloatDrugEffect floatDrugEffect = new FloatDrugEffect(isClientOnly);
        EFFECTS.add(floatDrugEffect);
        return floatDrugEffect;
    }

    private FloatDrugEffect register() {
        return register(false);
    }

    private FloatDrugEffect registerClient() {
        return register(true);
    }

    public void reset(boolean clientOnly) {
        for (DrugEffect effect : EFFECTS) {
            if (effect.isClientOnly() == clientOnly) effect.resetValue();
        }
    }

    private interface DrugEffect {
        boolean isClientOnly();
        void resetValue();
    }

    public static class FloatDrugEffect implements DrugEffect {
        private final boolean isClientOnly;
        private float value = 0;

        public FloatDrugEffect(boolean isClientOnly) {
            this.isClientOnly = isClientOnly;
        }

        public void addValue(float valueIn) {
            value += valueIn;
        }

        public float getValue() {
            return value;
        }

        public float getClamped() {
            return MathHelper.clamp(value, 0, 1);
        }

        @Override
        public boolean isClientOnly() {
            return isClientOnly;
        }

        @Override
        public void resetValue() {
            value = 0;
        }
    }
}
