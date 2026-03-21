package com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugEffects;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugInstance;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.network.ActiveDrugCapSyncMessage;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.network.DrugCapSyncMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
        private final Implementation instance = new Implementation();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == PlayerProperties.PLAYER_DRUGS;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PlayerProperties.PLAYER_DRUGS) {
                return PlayerProperties.PLAYER_DRUGS.cast(instance);
            }
            return null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return (NBTTagCompound) Storage.writeNBTStatic(instance);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            Storage.readNBTStatic(instance, nbt);
        }
    }

    public static class Storage implements Capability.IStorage<IPlayerDrugs> {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IPlayerDrugs> capability, IPlayerDrugs instance, EnumFacing side) {
            return writeNBTStatic(instance);
        }

        @Override
        public void readNBT(Capability<IPlayerDrugs> capability, IPlayerDrugs instance, EnumFacing side, NBTBase nbtBase) {
            readNBTStatic(instance, (NBTTagCompound) nbtBase);
        }

        static NBTBase writeNBTStatic(IPlayerDrugs instance) {
            NBTTagCompound nbt = new NBTTagCompound();

            NBTTagList drugSources = new NBTTagList();
            for (DrugInstance drugInstance : instance.getDrugSources()) {
                NBTTagCompound drugProperties = new NBTTagCompound();
                drugProperties.setString("id", drugInstance.toName());
                drugProperties.setInteger("delay", drugInstance.getDelayTime());
                drugProperties.setFloat("potency", drugInstance.getPotency());
                drugProperties.setInteger("duration", drugInstance.getDuration());
                drugProperties.setInteger("timeActive", drugInstance.getTimeActive());
                drugSources.appendTag(drugProperties);
            }
            nbt.setTag("sources", drugSources);

            NBTTagCompound drugAbuse = new NBTTagCompound();
            instance.getDrugAbuseMap().forEach((drug, tick) -> {
                if (tick > 0)
                    drugAbuse.setInteger(Drug.toName(drug), tick);
            });
            nbt.setTag("abuse", drugAbuse);

            return nbt;
        }

        static void readNBTStatic(IPlayerDrugs instance, NBTTagCompound nbt) {
            if (nbt == null) return;

            NBTTagList drugSources = nbt.getTagList("sources", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < drugSources.tagCount(); i++) {
                NBTTagCompound drugProperties = drugSources.getCompoundTagAt(i);
                Drug drug = Drug.byName(drugProperties.getString("id"));
                if (drug == null) {
                    OrangeSunshine.logger.warn("Tried to read non-existent drug {} from sources, ignoring", drugProperties.getString("id"));
                    continue;
                }
                DrugInstance drugInstance = new DrugInstance(drug,
                        drugProperties.getInteger("delay"),
                        drugProperties.getFloat("potency"),
                        drugProperties.getInteger("duration"),
                        drugProperties.getInteger("timeActive"));
                instance.addDrugSource(drugInstance);
            }

            NBTTagCompound drugAbuse = nbt.getCompoundTag("abuse");
            for (String drugKey : drugAbuse.getKeySet()) {
                Drug drug = Drug.byName(drugKey);
                if (drug == null) {
                    OrangeSunshine.logger.warn("Tried to read non-existent drug {} from abuse map, ignoring", drugKey);
                    continue;
                }
                instance.addDrugAbuse(drug, drugAbuse.getInteger(drugKey));
            }
        }
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IPlayerDrugs.class, new Storage(), Implementation::new);

        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public static class EventHandler {
        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof EntityPlayer) {
                event.addCapability(new ResourceLocation(OrangeSunshine.MODID, "drugs"), new Provider());
            }
        }

        @SubscribeEvent
        public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.player instanceof EntityPlayerMP) {
                sync((EntityPlayerMP) event.player);
                syncActives((EntityPlayerMP) event.player);
            }
        }

        @SubscribeEvent
        public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (event.player instanceof EntityPlayerMP) {
                sync((EntityPlayerMP) event.player);
                syncActives((EntityPlayerMP) event.player);
            }
        }

        private int totalTicks = 1;

        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                if (totalTicks++ % (5 * 20) == 0) {
                    MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                    if (server != null) {
                        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
                            sync(player);
                        }
                    }
                }
            }
        }
    }

    public static void sync(EntityPlayerMP player) {
        List<DrugInstance> drugInstances = Drug.getDrugSources(player);
        OrangeSunshine.network.sendTo(new DrugCapSyncMessage(drugInstances), player);
    }

    public static void syncActives(EntityPlayerMP player) {
        Map<Drug, Float> actives = Drug.getActiveDrugs(player);
        OrangeSunshine.network.sendTo(new ActiveDrugCapSyncMessage(actives), player);
    }
}
