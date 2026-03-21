package com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugEffects;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugInstance;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface IPlayerDrugs {
    void addDrugSource(DrugInstance drug);

    void removeDrugSource(DrugInstance drug);

    void setSources(List<DrugInstance> drugInstances);

    void clearDrugSources();

    List<DrugInstance> getDrugSources();

    void putActive(Drug drug, float effect);

    @Nullable
    Float getActive(Drug drug);

    void clearActives();

    void setActives(Map<Drug, Float> activeDrugs);

    Map<Drug, Float> getActiveDrugs();

    void addDrugAbuse(Drug drug, int ticks);

    int getDrugAbuse(Drug drug);

    void tickDrugAbuse();

    void setDrugAbuseMap(Map<Drug, Integer> drugAbuseMap);

    Map<Drug, Integer> getDrugAbuseMap();

    DrugEffects getDrugEffects();

    void setSmokeTicks(int ticks);

    int getSmokeTicks();
}
