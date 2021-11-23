package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import net.minecraft.entity.EntityLivingBase;

/**
 * Created by lukas on 17.02.14.
 */
public interface IDrugRenderer
{
    public void update(com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties drugProperties, EntityLivingBase entity);

    public void distortScreen(float par1, EntityLivingBase entity, int rendererUpdateCount, com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties drugProperties);

    public void renderOverlaysAfterShaders(float par1, EntityLivingBase entity, int updateCounter, int width, int height, com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties drugProperties);

    public void renderOverlaysBeforeShaders(float par1, EntityLivingBase entity, int updateCounter, int width, int height, com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties drugProperties);

    public void renderAllHallucinations(float par1, com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties drugProperties);

    public float getCurrentHeatDistortion();

    public float getCurrentWaterDistortion();

    public float getCurrentWaterScreenDistortion();
}
