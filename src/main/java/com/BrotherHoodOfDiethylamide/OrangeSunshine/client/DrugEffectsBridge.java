package com.BrotherHoodOfDiethylamide.OrangeSunshine.client;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugEffects;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

/**
 * Bridges the new ADSR DrugEffects system to the portedpsych renderer.
 * On each client render tick, reads DrugEffects values and feeds them
 * into portedpsych's DrugProperties drug interface methods.
 */
@SideOnly(Side.CLIENT)
public class DrugEffectsBridge implements com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.Drug {
    private DrugEffects cachedEffects;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return;

        // Lazy-init DrugProperties for the client-side player on first tick
        if (DrugProperties.getDrugProperties(player) == null) {
            DrugProperties.initInEntity(player);
        }

        cachedEffects = Drug.getDrugEffects(player);

        // Run renderTick for each active drug
        Map<Drug, Float> actives = Drug.getActiveDrugs(player);
        cachedEffects.reset(true);
        for (Map.Entry<Drug, Float> entry : actives.entrySet()) {
            entry.getKey().renderTick(cachedEffects, entry.getValue());
        }

        // Register ourselves as a portedpsych drug if needed
        DrugProperties drugProperties = DrugProperties.getDrugProperties(player);
        if (drugProperties != null) {
            if (!drugProperties.drugs.containsKey("bridge")) {
                drugProperties.addDrug("bridge", this);
            }
        }
    }

    private DrugEffects getEffects() {
        if (cachedEffects == null) cachedEffects = new DrugEffects();
        return cachedEffects;
    }

    // portedpsych Drug interface implementation — maps new DrugEffects to old renderer

    @Override
    public void update(EntityLivingBase entity, DrugProperties drugProperties) {
        // No-op: updates happen in onClientTick
    }

    @Override
    public void reset(EntityLivingBase entity, DrugProperties drugProperties) {
        cachedEffects = new DrugEffects();
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {}

    @Override
    public void readFromNBT(NBTTagCompound compound) {}

    @Override
    public double getActiveValue() {
        DrugEffects e = getEffects();
        // Return max of all visual effect channels as a general "active" indicator
        float max = 0;
        max = Math.max(max, e.BIG_WAVES.getValue());
        max = Math.max(max, e.SMALL_WAVES.getValue());
        max = Math.max(max, e.WORLD_DEFORMATION.getValue());
        max = Math.max(max, e.SATURATION.getValue());
        max = Math.max(max, e.CAMERA_TREMBLE.getValue());
        max = Math.max(max, e.KALEIDOSCOPE_INTENSITY.getValue());
        max = Math.max(max, e.RECURSION.getValue());
        return max;
    }

    @Override
    public void addToDesiredValue(double effect) {}

    @Override
    public void setDesiredValue(double effect) {}

    @Override
    public boolean isVisible() {
        return getActiveValue() > 0;
    }

    @Override
    public void setLocked(boolean drugLocked) {}

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public float heartbeatVolume() {
        return 0;
    }

    @Override
    public float heartbeatSpeed() {
        return 0;
    }

    @Override
    public float breathVolume() {
        return 0;
    }

    @Override
    public float breathSpeed() {
        return 0;
    }

    @Override
    public float randomJumpChance() {
        return 0;
    }

    @Override
    public float randomPunchChance() {
        return 0;
    }

    @Override
    public float digSpeedModifier() {
        return 1.0f; // Handled by DrugHandler attribute modifiers
    }

    @Override
    public float speedModifier() {
        return 1.0f; // Handled by DrugHandler attribute modifiers
    }

    @Override
    public float soundVolumeModifier() {
        return 1.0f;
    }

    @Override
    public EntityPlayer.SleepResult getSleepStatus() {
        return null;
    }

    @Override
    public void applyContrastColorization(float[] rgba) {
        float hue = getEffects().HUE_AMPLITUDE.getValue();
        if (hue > 0) {
            rgba[0] *= 1.0f + hue * 0.5f;
            rgba[1] *= 1.0f - hue * 0.2f;
            rgba[2] *= 1.0f + hue * 0.3f;
            rgba[3] += hue * 0.3f;
        }
    }

    @Override
    public void applyColorBloom(float[] rgba) {
        float bloom = getEffects().BLOOM_RADIUS.getValue();
        if (bloom > 0) {
            rgba[3] += bloom * 0.1f;
        }
    }

    @Override
    public float desaturationHallucinationStrength() {
        float sat = getEffects().SATURATION.getValue();
        return sat < 0 ? -sat : 0;
    }

    @Override
    public float superSaturationHallucinationStrength() {
        float sat = getEffects().SATURATION.getValue();
        return sat > 0 ? Math.min(sat * 0.3f, 1.0f) : 0;
    }

    @Override
    public float contextualHallucinationStrength() {
        return Math.min(getEffects().WORLD_DEFORMATION.getValue() * 0.15f, 0.5f);
    }

    @Override
    public float colorHallucinationStrength() {
        return Math.min(getEffects().HUE_AMPLITUDE.getValue() * 0.5f, 1.0f);
    }

    @Override
    public float movementHallucinationStrength() {
        DrugEffects e = getEffects();
        return Math.min(
                e.BIG_WAVES.getValue() + e.SMALL_WAVES.getValue() +
                e.WIGGLE_WAVES.getValue() + e.WORLD_DEFORMATION.getValue(), 2.0f) * 0.5f;
    }

    @Override
    public float handTrembleStrength() {
        return getEffects().HAND_TREMBLE.getValue();
    }

    @Override
    public float viewTrembleStrength() {
        return getEffects().CAMERA_TREMBLE.getValue();
    }

    @Override
    public float headMotionInertness() {
        return getEffects().CAMERA_INERTIA.getValue();
    }

    @Override
    public float bloomHallucinationStrength() {
        return getEffects().BLOOM_RADIUS.getValue() * 0.1f;
    }

    @Override
    public float viewWobblyness() {
        return getEffects().BUMPY.getValue() * 0.01f;
    }

    @Override
    public float doubleVision() {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawOverlays(float partialTicks, EntityLivingBase entity, int updateCounter, int width, int height, DrugProperties drugProperties) {
        // No custom overlays — portedpsych handles it
    }

    @Override
    public float motionBlur() {
        return getEffects().WATER_DISTORT.getValue();
    }
}
