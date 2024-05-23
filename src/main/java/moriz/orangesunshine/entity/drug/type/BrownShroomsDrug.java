/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.type;

import moriz.orangesunshine.entity.drug.DrugType;

/**
 * Created by lukas on 01.11.14.
 */
public class BrownShroomsDrug extends SimpleDrug {
    public BrownShroomsDrug(double decSpeed, double decSpeedPlus) {
        super(DrugType.BROWN_SHROOMS, decSpeed, decSpeedPlus);
    }

    @Override
    public float colorHallucinationStrength() {
        return (float) getActiveValue() * .1F;
    }

    @Override
    public float movementHallucinationStrength() {
        return (float) getActiveValue() * 0.42F;
    }

    @Override
    public float contextualHallucinationStrength() {
        return (float) getActiveValue() * 0.2F;
    }

    @Override
    public float viewWobblyness() {
        return (float) getActiveValue() * 0.1F;
    }

    @Override
    public float hungerSuppression() {
        return (float)getActiveValue() * 0.1F;
    }

    @Override
    public float bloomHallucinationStrength() {
        return (float) getActiveValue() * 0.35F;
    }

    @Override
    public float superSaturationHallucinationStrength() {
        return (float)getActiveValue() * 0.7F;
    }
}
