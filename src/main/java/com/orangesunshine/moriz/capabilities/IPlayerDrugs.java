package com.orangesunshine.moriz.capabilities;

import com.orangesunshine.moriz.drugs.Drug;
import com.orangesunshine.moriz.drugs.DrugEffects;
import com.orangesunshine.moriz.drugs.DrugInstance;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.registries.tags.ITag;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@AutoRegisterCapability
public interface IPlayerDrugs {

    public static interface IStorage<T>
    {
        /**
         * Serialize the capability instance to a NBTTag.
         * This allows for a central implementation of saving the data.
         *
         * It is important to note that it is up to the API defining
         * the capability what requirements the 'instance' value must have.
         *
         * Due to the possibility of manipulating internal data, some
         * implementations MAY require that the 'instance' be an instance
         * of the 'default' implementation.
         *
         * Review the API docs for more info.
         *
         * @param instance An instance of that capabilities interface.
         * @param side The side of the object the instance is associated with.
         * @return a NBT holding the data. Null if no data needs to be stored.
         */
        @Nullable
        CompoundTag writeNBT( T instance, Direction side);

        /**
         * Read the capability instance from a NBT tag.
         *
         * This allows for a central implementation of saving the data.
         *
         * It is important to note that it is up to the API defining
         * the capability what requirements the 'instance' value must have.
         *
         * Due to the possibility of manipulating internal data, some
         * implementations MAY require that the 'instance' be an instance
         * of the 'default' implementation.
         *
         * Review the API docs for more info.         *
         *
         * @param instance An instance of that capabilities interface.
         * @param side The side of the object the instance is associated with.
         * @param nbt A NBT holding the data. Must not be null, as doesn't make sense to call this function with nothing to read...
         */
        void readNBT(T instance, Direction side, Tag nbt);
    }

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