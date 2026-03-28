/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.type;

import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.util.MathUtils;

/**
 * Salvia Divinorum - intense, short-duration dissociative psychedelic.
 * Very fast decay (0.001d) so effects are extreme but brief (30-60 second peak).
 */
public class SalviaDrug extends SimpleDrug {

    public SalviaDrug(double decSpeed, double decSpeedPlus) {
        super(DrugType.SALVIA, decSpeed, decSpeedPlus);
    }

    @Override
    public float colorHallucinationStrength() {
        return (float) getActiveValue() * 1.5F;
    }

    @Override
    public float movementHallucinationStrength() {
        return (float) getActiveValue() * 2.0F;
    }

    @Override
    public float viewTrembleStrength() {
        return MathUtils.inverseLerp((float) getActiveValue(), 0.3F, 1F) * 1.0F;
    }

    @Override
    public float viewWobblyness() {
        return (float) getActiveValue() * 0.4F;
    }

    @Override
    public float headMotionInertness() {
        return (float) getActiveValue() * 15F;
    }

    @Override
    public float superSaturationHallucinationStrength() {
        return (float) getActiveValue() * 1.0F;
    }

    @Override
    public float contextualHallucinationStrength() {
        return (float) getActiveValue() * 1.5F;
    }

    @Override
    public float speedModifier() {
        return 1 - (float) getActiveValue() * 0.5F;
    }

    @Override
    public float bloomHallucinationStrength() {
        return (float) getActiveValue() * 0.7F;
    }

    @Override
    public float doubleVision() {
        return MathUtils.inverseLerp((float) getActiveValue(), 0.4F, 1F) * 0.6F;
    }
}
