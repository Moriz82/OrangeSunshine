/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.influence;

import moriz.orangesunshine.entity.drug.*;
import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.entity.drug.type.HarmoniumDrug;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

/**
 * Created by lukas on 10.03.14.
 */
public class HarmoniumDrugInfluence extends DrugInfluence {

    private float[] color;

    public HarmoniumDrugInfluence(int delay, double influenceSpeed, double influenceSpeedPlus, double maxInfluence, float[] color) {
        super(InfluenceType.HARMONIUM, DrugType.HARMONIUM, delay, influenceSpeed, influenceSpeedPlus, maxInfluence);
        this.color = color;
    }

    public HarmoniumDrugInfluence(InfluenceType type) {
        super(type);
        color = new float[3];
    }

    @Override
    public void addToDrug(DrugProperties drugProperties, double value) {
        super.addToDrug(drugProperties, value);

        Drug drug = drugProperties.getDrug(getDrugType());

        if (drug instanceof HarmoniumDrug) {
            HarmoniumDrug harmonium = (HarmoniumDrug) drug;

            double inf = value + (1 - value) * (1 - harmonium.getActiveValue());
            harmonium.currentColor[0] = (float) Mth.lerp(inf, harmonium.currentColor[0], color[0]);
            harmonium.currentColor[1] = (float) Mth.lerp(inf, harmonium.currentColor[1], color[1]);
            harmonium.currentColor[2] = (float) Mth.lerp(inf, harmonium.currentColor[2], color[2]);
        }
    }

    @Override
    public void fromNbt(CompoundTag compound) {
        super.fromNbt(compound);
        color[0] = compound.getFloatOr("color[0]", 0);
        color[1] = compound.getFloatOr("color[1]", 0);
        color[2] = compound.getFloatOr("color[2]", 0);
    }

    @Override
    public void toNbt(CompoundTag compound) {
        super.toNbt(compound);
        compound.putFloat("color[0]", color[0]);
        compound.putFloat("color[1]", color[1]);
        compound.putFloat("color[2]", color[2]);
    }
}
