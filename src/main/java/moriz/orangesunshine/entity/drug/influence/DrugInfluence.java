/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.influence;

import java.util.Locale;
import java.util.Optional;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.util.NbtSerialisable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

public class DrugInfluence implements NbtSerialisable {

    public static Optional<DrugInfluence> loadFromNbt(CompoundTag compound) {
        return InfluenceType.of(compound.getStringOr("type", "")).map(type -> type.create(compound));
    }

    protected DrugType drugType;

    protected int delay;

    protected double influenceSpeed;
    protected double influenceSpeedPlus;

    protected double maxInfluence;

    private final InfluenceType type;

    public DrugInfluence(DrugType drugType, int delay, double influenceSpeed, double influenceSpeedPlus, double maxInfluence) {
        this(InfluenceType.DEFAULT, drugType, delay, influenceSpeed, influenceSpeedPlus, maxInfluence);
    }

    public DrugInfluence(InfluenceType type, DrugType drugType, int delay, double influenceSpeed, double influenceSpeedPlus, double maxInfluence) {
        this(type);
        this.drugType = drugType;

        this.delay = delay;

        this.influenceSpeed = influenceSpeed;
        this.influenceSpeedPlus = influenceSpeedPlus;

        this.maxInfluence = maxInfluence;
    }

    protected DrugInfluence(InfluenceType type) {
        this.type = type;
    }

    public final DrugType getDrugType() {
        return drugType;
    }

    public boolean isOf(DrugType type) {
        return getDrugType() == type;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public double getInfluenceSpeed() {
        return influenceSpeed;
    }

    public void setInfluenceSpeed(double influenceSpeed) {
        this.influenceSpeed = influenceSpeed;
    }

    public double getInfluenceSpeedPlus() {
        return influenceSpeedPlus;
    }

    public void setInfluenceSpeedPlus(double influenceSpeedPlus) {
        this.influenceSpeedPlus = influenceSpeedPlus;
    }

    public double getMaxInfluence() {
        return maxInfluence;
    }

    public void setMaxInfluence(double maxInfluence) {
        this.maxInfluence = maxInfluence;
    }

    public boolean update(DrugProperties drugProperties) {
        if (delay > 0) {
            delay--;
        }

        if (delay == 0 && maxInfluence > 0) {
            double addition = Math.min(maxInfluence, influenceSpeedPlus + maxInfluence * influenceSpeed);

            addToDrug(drugProperties, addition);
            maxInfluence -= addition;
        }

        return isDone();
    }

    public void addToDrug(DrugProperties drugProperties, double value) {
        drugProperties.addToDrug(drugType, value);
    }

    public boolean isDone() {
        return maxInfluence <= 0.0;
    }

    @Override
    public DrugInfluence clone() {
        return type.create(toNbt());
    }

    @Override
    public void fromNbt(CompoundTag compound) {
        if (compound.contains("drugName")) {
            drugType = DrugType.REGISTRY.getValue(OrangeSunshine.id(compound.getStringOr("drugName", "").toLowerCase(Locale.ROOT)));
        } else {
            Identifier id = Identifier.tryParse(compound.getStringOr("drugType", ""));
            drugType = id == null ? null : DrugType.REGISTRY.getValue(id);
        }
        delay = compound.getIntOr("delay", 0);
        influenceSpeed = compound.getDoubleOr("influenceSpeed", 0);
        influenceSpeedPlus = compound.getDoubleOr("influenceSpeedPlus", 0);
        maxInfluence = compound.getDoubleOr("maxInfluence", 0);
    }

    @Override
    public void toNbt(CompoundTag compound) {
        compound.putString("type", type.identifier());
        compound.putString("drugType", drugType.id().toString());
        compound.putInt("delay", delay);
        compound.putDouble("influenceSpeed", influenceSpeed);
        compound.putDouble("influenceSpeedPlus", influenceSpeedPlus);
        compound.putDouble("maxInfluence", maxInfluence);
    }

    public interface DelayType {
        int IMMEDIATE = 0;
        int INGESTED = 15;
        int INHALED = 20;
        int CONTACT = 30;
        int METABOLISED = 60;
    }
}
