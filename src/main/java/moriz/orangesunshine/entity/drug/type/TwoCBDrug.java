/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.type;

import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.util.MathUtils;

/**
 * 2C-B - synthetic psychedelic phenethylamine.
 * Milder than LSD, more empathogenic with vivid color enhancement
 * and subtle visual patterns. Slight stimulant effect.
 */
public class TwoCBDrug extends SimpleDrug {
    public TwoCBDrug(DrugType type, double decSpeed, double decSpeedPlus) {
        super(type, decSpeed, decSpeedPlus);
    }

    @Override
    public float colorHallucinationStrength() {
        return (float) getActiveValue() * 0.4F;
    }

    @Override
    public float movementHallucinationStrength() {
        return (float) getActiveValue() * 0.3F;
    }

    @Override
    public float superSaturationHallucinationStrength() {
        return (float) getActiveValue() * 0.5F;
    }

    @Override
    public float bloomHallucinationStrength() {
        return MathUtils.inverseLerp((float) getActiveValue(), 0, 0.5F) * 0.3F;
    }

    @Override
    public float viewWobblyness() {
        return (float) getActiveValue() * 0.1F;
    }

    @Override
    public float speedModifier() {
        return 1 + (float) getActiveValue() * 0.05F;
    }

    @Override
    public float hungerSuppression() {
        return (float) getActiveValue() * 0.15F;
    }
}
