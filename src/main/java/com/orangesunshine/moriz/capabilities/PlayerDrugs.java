package com.orangesunshine.moriz.capabilities;

import com.orangesunshine.moriz.OrangeSunshine;
import com.orangesunshine.moriz.drugs.Drug;
import com.orangesunshine.moriz.drugs.DrugEffects;
import com.orangesunshine.moriz.drugs.DrugInstance;
import com.orangesunshine.moriz.network.ActiveDrugCapSync;
import com.orangesunshine.moriz.network.DrugCapSync;
import com.orangesunshine.moriz.network.PacketHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.tags.ITag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PlayerDrugs {
    static class Implementation implements IPlayerDrugs, INBTSerializable<Tag> {
        private final Map<Drug, Float> active = new HashMap<>();
        private final List<DrugInstance> sources = new ArrayList<>();
        private final Map<Drug, Integer> abuseTimers = new HashMap<>();
        private final DrugEffects drugEffects = new DrugEffects();
        private int smokeTick = 0;

        private Storage storage = new Storage();

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

        @Override
        public Tag serializeNBT() {
            return storage.writeNBT(this, null);
        }

        @Override
        public void deserializeNBT(Tag nbt) {
            storage.readNBT(this, null, nbt);
        }
    }

    static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final Implementation defaultImplementation = new Implementation();
        private final LazyOptional<IPlayerDrugs> optional = LazyOptional.of(() -> defaultImplementation);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap instanceof IPlayerDrugs) {
                return optional.cast();
            }
            return LazyOptional.of(null);
        }

        @Override
        public CompoundTag serializeNBT() {
            return (CompoundTag) defaultImplementation.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            defaultImplementation.deserializeNBT(nbt);
        }

        public void invalidate() {
            optional.invalidate();
        }
    }

    static class Storage implements IPlayerDrugs.IStorage<IPlayerDrugs> {
        @Nullable
        public CompoundTag writeNBT( IPlayerDrugs instance, Direction side) {
            CompoundTag nbt = new CompoundTag();

            ListTag drugSources = new ListTag();
            for (DrugInstance drugInstance : instance.getDrugSources()) {
                CompoundTag drugProperties = new CompoundTag();
                drugProperties.putString("id", drugInstance.toName());
                drugProperties.putInt("delay", drugInstance.getDelayTime());
                drugProperties.putFloat("potency", drugInstance.getPotency());
                drugProperties.putInt("duration", drugInstance.getDuration());
                drugProperties.putInt("timeActive", drugInstance.getTimeActive());
                drugSources.add(drugProperties);
            }
            nbt.put("sources", drugSources);

            CompoundTag drugAbuse = new CompoundTag();
            instance.getDrugAbuseMap().forEach((drug, tick) -> {
                if (tick > 0)
                    drugAbuse.putInt(Drug.toName(drug), tick);
            });
            nbt.put("abuse", drugAbuse);

            return nbt;
        }

        public void readNBT(IPlayerDrugs instance, Direction side, Tag nbtIn) {
            if (nbtIn != null) {
                CompoundTag nbt = (CompoundTag) nbtIn;

                ListTag drugSources = nbt.getList("sources", 10);
                for (Tag drugSource : drugSources) {
                    CompoundTag drugProperties = (CompoundTag) drugSource;
                    Drug drug = Drug.byName(drugProperties.getString("id"));
                    if (drug == null) {
                        OrangeSunshine.LOGGER.warn("Tried to read non-existent registry {} from sources, ignoring", drugProperties.getString("id"));
                        continue;
                    }
                    DrugInstance drugInstance = new DrugInstance(drug, drugProperties.getInt("delay"), drugProperties.getFloat("potency"), drugProperties.getInt("duration"), drugProperties.getInt("timeActive"));
                    instance.addDrugSource(drugInstance);
                }

                CompoundTag drugAbuse = nbt.getCompound("abuse");
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
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, PlayerDrugs::attachCapabilitiesEntity);
        MinecraftForge.EVENT_BUS.addListener(PlayerDrugs::onPlayerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(PlayerDrugs::onPlayerChangedDimension);
        MinecraftForge.EVENT_BUS.addListener(PlayerDrugs::onServerTick);
    }



    static void attachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            PlayerDrugs.Provider provider = new PlayerDrugs.Provider();
            event.addCapability(new ResourceLocation(OrangeSunshine.MODID, "drugs"), provider);
            event.addListener(provider::invalidate);
        }
    }

    static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        sync((ServerPlayer) event.getEntity());
        syncActives((ServerPlayer) event.getEntity());
    }

    static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        sync((ServerPlayer) event.getEntity());
        syncActives((ServerPlayer) event.getEntity());
    }

    private static int totalTicks = 1;
    static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (totalTicks++ % (5*20) == 0) {
                MinecraftServer server = LogicalSidedProvider.CLIENTWORLD.get(LogicalSide.SERVER).get().getServer();
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    sync(player);
                }
            }
        }
    }

    public static void sync(ServerPlayer player) {
        List<DrugInstance> drugInstances = Drug.getDrugSources(player);
        PacketHandler.INSTANCE.send(new DrugCapSync(drugInstances), PacketDistributor.PLAYER.with(player));
    }

    private static void syncActives(ServerPlayer player) {
        Map<Drug, Float> actives = Drug.getActiveDrugs(player);
        PacketHandler.INSTANCE.send(new ActiveDrugCapSync(actives), PacketDistributor.PLAYER.with(player));
    }
}