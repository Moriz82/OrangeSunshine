/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.type;

import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.util.MathUtils;
import net.minecraft.nbt.CompoundTag;

public class HarmoniumDrug extends SimpleDrug {
    public float[] currentColor = { 1, 1, 1 };

    public HarmoniumDrug(double decSpeed, double decSpeedPlus) {
        super(DrugType.HARMONIUM, decSpeed, decSpeedPlus);
    }

    @Override
    public void applyContrastColorization(float[] rgba) {
        MathUtils.mixColorsDynamic(currentColor, rgba, (float) getActiveValue(), false);
    }

    @Override
    public void applyColorBloom(float[] rgba) {
        MathUtils.mixColorsDynamic(currentColor, rgba, (float) getActiveValue() * 3, false);
    }

    @Override
    public void toNbt(CompoundTag tagCompound) {
        super.toNbt(tagCompound);
        tagCompound.putFloat("currentColor[0]", currentColor[0]);
        tagCompound.putFloat("currentColor[1]", currentColor[1]);
        tagCompound.putFloat("currentColor[2]", currentColor[2]);
    }

    @Override
    public void fromNbt(CompoundTag tagCompound) {
        super.fromNbt(tagCompound);
        currentColor[0] = tagCompound.getFloatOr("currentColor[0]", 1);
        currentColor[1] = tagCompound.getFloatOr("currentColor[1]", 1);
        currentColor[2] = tagCompound.getFloatOr("currentColor[2]", 1);
    }
}
