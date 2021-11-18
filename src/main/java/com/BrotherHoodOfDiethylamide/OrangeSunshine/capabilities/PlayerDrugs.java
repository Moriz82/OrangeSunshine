package com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugEffects;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugInstance;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.network.ActiveDrugCapSync;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.network.DrugCapSync;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.network.PacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PlayerDrugs {
    static class Implementation implements IPlayerDrugs {
        private final Map<Drug, Float> active = new HashMap<>();
        private final List<DrugInstance> sources = new ArrayList<>();
        private final Map<Drug, Integer> abuseTimers = new HashMap<>();
        private final DrugEffects drugEffects = new DrugEffects();
        private int smokeTick = 0;

        @Override
        public void addDrugSource(DrugInstance drug) {
            sources.add(drug);
        }

        @Override
        public void setSources(List<DrugInstance> drugInstances) {
            sources.clear();
            sources.addAll(drugInstances);
        }

        @Override
        public void removeDrugSource(DrugInstance drug) {
            sources.remove(drug);
        }

        @Override
        public void clearDrugSources() {
            sources.clear();
        }

        @Override
        public List<DrugInstance> getDrugSources() {
            return sources;
        }

        @Override
        public void putActive(Drug drug, float effect) {
            if (effect > 0) {
                active.put(drug, effect);
            } else {
                active.remove(drug);
            }
        }

        @Nullable
        @Override
        public Float getActive(Drug drug) {
            return active.get(drug);
        }

        @Override
        public void clearActives() {
            active.clear();
        }

        @Override
        public void setActives(Map<Drug, Float> activeDrugs) {
            active.clear();
            active.putAll(activeDrugs);
        }

        @Override
        public Map<Drug, Float> getActiveDrugs() {
            return active;
        }

        @Override
        public void addDrugAbuse(Drug drug, int ticks) {
            abuseTimers.put(drug, abuseTimers.getOrDefault(drug, 0) + ticks);
        }

        @Override
        public int getDrugAbuse(Drug drug) {
            return abuseTimers.getOrDefault(drug, 0);
        }

        @Override
        public void tickDrugAbuse() {
            abuseTimers.replaceAll((drug, tick) -> tick - 1);
            abuseTimers.values().removeIf(tick -> tick <= 0);
        }

        @Override
        public void setDrugAbuseMap(Map<Drug, Integer> drugAbuseMap) {
            abuseTimers.clear();
            abuseTimers.putAll(drugAbuseMap);
        }

        @Override
        public Map<Drug, Integer> getDrugAbuseMap() {
            return abuseTimers;
        }

        @Override
        public DrugEffects getDrugEffects() {
            return drugEffects;
        }

        @Override
        public void setSmokeTicks(int ticks) {
            smokeTick = ticks;
        }

        @Override
        public int getSmokeTicks() {
            return smokeTick;
        }
    }

    static class Provider implements ICapabilitySerializable<CompoundNBT> {
        private final Implementation defaultImplementation = new Implementation();
        private final LazyOptional<IPlayerDrugs> optional = LazyOptional.of(() -> defaultImplementation);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return optional.cast();
        }

        @Override
        public CompoundNBT serializeNBT() {
            return (CompoundNBT) PlayerProperties.PLAYER_DRUGS.writeNBT(defaultImplementation, null);
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            PlayerProperties.PLAYER_DRUGS.readNBT(defaultImplementation, null, nbt);
        }

        public void invalidate() {
            optional.invalidate();
        }
    }

    static class Storage implements Capability.IStorage<IPlayerDrugs>{
        @Nullable
        @Override
        public INBT writeNBT(Capability<IPlayerDrugs> capability, IPlayerDrugs instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();

            ListNBT drugSources = new ListNBT();
            for (DrugInstance drugInstance : instance.getDrugSources()) {
                CompoundNBT drugProperties = new CompoundNBT();
                drugProperties.putString("id", drugInstance.toName());
                drugProperties.putInt("delay", drugInstance.getDelayTime());
                drugProperties.putFloat("potency", drugInstance.getPotency());
                drugProperties.putInt("duration", drugInstance.getDuration());
                drugProperties.putInt("timeActive", drugInstance.getTimeActive());
                drugSources.add(drugProperties);
            }
            nbt.put("sources", drugSources);

            CompoundNBT drugAbuse = new CompoundNBT();
            instance.getDrugAbuseMap().forEach((drug, tick) -> {
                if (tick > 0)
                    drugAbuse.putInt(Drug.toName(drug), tick);
            });
            nbt.put("abuse", drugAbuse);

            return nbt;
        }

        @Override
        public void readNBT(Capability<IPlayerDrugs> capability, IPlayerDrugs instance, Direction side, INBT nbtIn) {
            if (nbtIn != null) {
                CompoundNBT nbt = (CompoundNBT) nbtIn;

                ListNBT drugSources = nbt.getList("sources", 10);
                for (INBT drugSource : drugSources) {
                    CompoundNBT drugProperties = (CompoundNBT) drugSource;
                    Drug drug = Drug.byName(drugProperties.getString("id"));
                    if (drug == null) {
                        OrangeSunshine.LOGGER.warn("Tried to read non-existent registry {} from sources, ignoring", drugProperties.getString("id"));
                        continue;
                    }
                    DrugInstance drugInstance = new DrugInstance(drug, drugProperties.getInt("delay"), drugProperties.getFloat("potency"), drugProperties.getInt("duration"), drugProperties.getInt("timeActive"));
                    instance.addDrugSource(drugInstance);
                }

                CompoundNBT drugAbuse = nbt.getCompound("abuse");
                for (String drugKey : drugAbuse.getAllKeys()) {
                    Drug drug = Drug.byName(drugKey);
                    if (drug == null) {
                        OrangeSunshine.LOGGER.warn("Tried to read non-existent registry {} from abuse map, ignoring", drugKey);
                        continue;
                    }
                    instance.addDrugAbuse(drug, drugAbuse.getInt(drugKey));
                }
            }
        }
    }

    static void register() {
        CapabilityManager.INSTANCE.register(IPlayerDrugs.class, new PlayerDrugs.Storage(), PlayerDrugs.Implementation::new);

        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, PlayerDrugs::attachCapabilitiesEntity);
        MinecraftForge.EVENT_BUS.addListener(PlayerDrugs::onPlayerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(PlayerDrugs::onPlayerChangedDimension);
        MinecraftForge.EVENT_BUS.addListener(PlayerDrugs::onServerTick);
    }

    static void attachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            PlayerDrugs.Provider provider = new PlayerDrugs.Provider();
            event.addCapability(new ResourceLocation(OrangeSunshine.MOD_ID, "drugs"), provider);
            event.addListener(provider::invalidate);
        }
    }

    static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        sync((ServerPlayerEntity) event.getPlayer());
        syncActives((ServerPlayerEntity) event.getPlayer());
    }

    static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        sync((ServerPlayerEntity) event.getPlayer());
        syncActives((ServerPlayerEntity) event.getPlayer());
    }

    private static int totalTicks = 1;
    static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (totalTicks++ % (5*20) == 0) {
                MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
                for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
                    sync(player);
                }
            }
        }
    }

    public static void sync(ServerPlayerEntity player) {
        List<DrugInstance> drugInstances = Drug.getDrugSources(player);
        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new DrugCapSync(drugInstances));
    }

    private static void syncActives(ServerPlayerEntity player) {
        Map<Drug, Float> actives = Drug.getActiveDrugs(player);
        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new ActiveDrugCapSync(actives));
    }
}
