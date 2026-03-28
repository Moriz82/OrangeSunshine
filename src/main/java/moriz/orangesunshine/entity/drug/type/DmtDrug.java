/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.type;

import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.util.MathUtils;

public class DmtDrug extends SimpleDrug {
    public DmtDrug(double decSpeed, double decSpeedPlus) {
        super(DrugType.DMT, decSpeed, decSpeedPlus);
    }

    @Override
    public float colorHallucinationStrength() {
        return (float)getActiveValue() * 1.8F;
    }

    @Override
    public float movementHallucinationStrength() {
        return (float)getActiveValue() * 2.2F;
    }

    @Override
    public float contextualHallucinationStrength() {
        return (float)getActiveValue() * 1.2F;
    }

    @Override
    public float bloomHallucinationStrength() {
        return (float)getActiveValue() * 0.9F;
    }

    @Override
    public float superSaturationHallucinationStrength() {
        return (float)getActiveValue() * 1.2F;
    }

    @Override
    public float viewTrembleStrength() {
        return (float)getActiveValue() * 1.5F;
    }

    @Override
    public float viewWobblyness() {
        return (float)getActiveValue() * 0.25F;
    }

    @Override
    public float doubleVision() {
        return MathUtils.inverseLerp((float)getActiveValue(), 0.3F, 1F) * 0.9F;
    }
}
