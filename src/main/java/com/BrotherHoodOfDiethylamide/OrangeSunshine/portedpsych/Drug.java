package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface Drug
{
    void update(EntityLivingBase entity, com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties drugProperties);

    void reset(EntityLivingBase entity, com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties drugProperties);

    void writeToNBT(NBTTagCompound compound);

    void readFromNBT(NBTTagCompound compound);

    double getActiveValue();

    void addToDesiredValue(double effect);

    void setDesiredValue(double effect);

    boolean isVisible();

    void setLocked(boolean drugLocked);

    boolean isLocked();

    float heartbeatVolume();

    float heartbeatSpeed();

    float breathVolume();

    float breathSpeed();

    float randomJumpChance();

    float randomPunchChance();

    float digSpeedModifier();

    float speedModifier();

    float soundVolumeModifier();

    EntityPlayer.SleepResult getSleepStatus();

    void applyContrastColorization(float[] rgba);

    void applyColorBloom(float[] rgba);

    float desaturationHallucinationStrength();

    float superSaturationHallucinationStrength();

    float contextualHallucinationStrength();

    float colorHallucinationStrength();

    float movementHallucinationStrength();

    float handTrembleStrength();

    float viewTrembleStrength();

    float headMotionInertness();

    float bloomHallucinationStrength();

    float viewWobblyness();

    float doubleVision();

    @SideOnly(Side.CLIENT)
    void drawOverlays(float partialTicks, EntityLivingBase entity, int updateCounter, int width, int height, com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties drugProperties);

    float motionBlur();
}
