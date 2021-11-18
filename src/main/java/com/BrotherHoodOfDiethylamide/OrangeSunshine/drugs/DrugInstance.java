package com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs;

public class DrugInstance {
    private final Drug drug;
    private final int duration;
    private final float potency;
    private int delayTime;
    private int timeActive;

    public DrugInstance(Drug drug, int delayTime, float potency, int duration) {
        this.drug = drug;
        this.delayTime = delayTime;
        this.potency = potency;
        this.duration = duration;
        this.timeActive = 0;
    }

    public DrugInstance(Drug drug, int delayTime, float potency, int duration, int timeActive) {
        this.drug = drug;
        this.delayTime = delayTime;
        this.potency = potency;
        this.duration = duration;
        this.timeActive = timeActive;
    }

    public String toName() {
        return Drug.toName(drug);
    }

    public Drug getDrug() {
        return drug;
    }

    public boolean isActive() {
        if (delayTime > 0) {
            delayTime--;
            return false;
        }
        return true;
    }

    public float getEffect(Drug.Envelope envelope) {
        return potency*envelope.getLevel(timeActive++, duration);
    }

    public float getPotency() {
        return potency;
    }

    public int getTimeActive() {
        return timeActive;
    }

    public int getDuration() {
        return duration;
    }

    public int getDelayTime() {
        return delayTime;
    }
}