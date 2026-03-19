/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.type;

import java.util.Optional;

import moriz.orangesunshine.PSDamageTypes;
import moriz.orangesunshine.entity.drug.*;
import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.util.MathUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class SimpleDrug implements Drug {
    protected double effect;
    protected double effectActive;
    protected boolean locked = false;

    private final double decreaseSpeed;
    private final double decreaseSpeedPlus;
    private final boolean invisible;

    private final DrugType type;

    private int ticksActive;

    public SimpleDrug(DrugType type, double decSpeed, double decSpeedPlus) {
        this(type, decSpeed, decSpeedPlus, false);
    }

    public SimpleDrug(DrugType type, double decSpeed, double decSpeedPlus, boolean invisible) {
        this.type = type;
        decreaseSpeed = decSpeed;
        decreaseSpeedPlus = decSpeedPlus;

        this.invisible = invisible;
    }

    @Override
    public final DrugType getType() {
        return type;
    }

    public void setActiveValue(double value) {
        effectActive = value;
    }

    @Override
    public double getActiveValue() {
        return effectActive;
    }

    public double getDesiredValue() {
        return effect;
    }

    public int getTicksActive() {
        return ticksActive;
    }

    @Override
    public void setDesiredValue(double value) {
        effect = value;
    }

    @Override
    public void addToDesiredValue(double value) {
        if (!locked) {
            effect += value;
        }
    }

    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public boolean isVisible() {
        return !invisible;
    }

    @Override
    public void update(DrugProperties drugProperties) {
        if (getActiveValue() > 0) {
            ticksActive++;

            if (heartbeatSpeed() > 3) {
                drugProperties.asEntity().hurt(drugProperties.damageOf(PSDamageTypes.HEART_ATTACK), Integer.MAX_VALUE);
                reset(drugProperties);
            }
        } else {
            ticksActive = 0;
        }

        if (!locked) {
            effect *= decreaseSpeed;
            effect -= decreaseSpeedPlus;
        }

        effect = Mth.clamp(effect, 0, 1);
        setActiveValue(MathUtils.nearValue(effectActive, effect, 0.05, 0.005));
    }

    @Override
    public void onWakeUp(DrugProperties drugProperties) {
        reset(drugProperties);
    }

    @Override
    public void reset(DrugProperties drugProperties) {
        if (!locked) {
            effect = 0;
        }
    }

    @Override
    public void fromNbt(CompoundTag compound) {
        setDesiredValue(compound.getDoubleOr("effect", 0));
        setActiveValue(compound.getDoubleOr("effectActive", 0));
        setLocked(compound.getBooleanOr("locked", false));
        ticksActive = compound.getIntOr("ticksActive", 0);
    }

    @Override
    public void toNbt(CompoundTag compound) {
        compound.putDouble("effect", getDesiredValue());
        compound.putDouble("effectActive", getActiveValue());
        compound.putBoolean("locked", isLocked());
        compound.putInt("ticksActive", ticksActive);
    }

    @Override
    public Optional<Component> trySleep(BlockPos pos) {
        return Optional.empty();
    }

    @Override
    public void applyContrastColorization(float[] rgba) {

    }

    @Override
    public void applyColorBloom(float[] rgba) {

    }

    protected static void rotateEntityPitch(Entity entity, double amount) {
        entity.setXRot((float)Mth.clamp(entity.getXRot() + amount, -90, 90));
    }

    protected static void rotateEntityYaw(Entity entity, double amount) {
        entity.setYRot(entity.getYRot() + (float)amount);
    }
}
