package com.orangesunshine.moriz.drugs;


import com.orangesunshine.moriz.OrangeSunshine;
import com.orangesunshine.moriz.capabilities.IPlayerDrugs;
import com.orangesunshine.moriz.capabilities.PlayerDrugs;
import com.orangesunshine.moriz.capabilities.PlayerProperties;
import com.orangesunshine.moriz.drugs.DrugEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.*;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Drug {
    private final Envelope envelope;
    private final int abuseAdder;

    public Drug(DrugProperties properties) {
        super();
        this.envelope = Objects.requireNonNull(properties.envelope);
        this.abuseAdder = properties.abuseAdder;
    }

    @Nullable
    public static Drug byName(String resource) {
        Drug drug = null;
        for (RegistryObject<Drug> e : OrangeSunshine.DRUGS.getEntries()) {
            if (e.getId().equals(new ResourceLocation(resource))) {
                drug = e.get();
                break;
            }
        }
        return drug;
    }

    public static String toName(Drug drug) {
        String name = "";
        for (RegistryObject<Drug> e : OrangeSunshine.DRUGS.getEntries()) {
            if (e.get().equals(drug)) {
                name = e.getId().getPath();
                break;
            }
        }
        return name;
    }

    public static void addDrug(Player player, DrugInstance drugInstance) {
        drugInstance.getDrug().startUse(player);
        PlayerProperties.getPlayerDrugs(player).addDrugSource(drugInstance);
    }

    public static void clearDrugs(Player player) {
        PlayerProperties.getPlayerDrugs(player).clearDrugSources();
    }

    public static List<DrugInstance> getDrugSources(Player player) {
        return PlayerProperties.getPlayerDrugs(player).getDrugSources();
    }

    public static Map<Drug, Float> getActiveDrugs(Player player) {
        return PlayerProperties.getPlayerDrugs(player).getActiveDrugs();
    }

    public static int getAbuse(Player player, Drug drug) {
        return PlayerProperties.getPlayerDrugs(player).getDrugAbuse(drug);
    }

    public static void addAbuse(Player player, Drug drug, int ticks) {
        PlayerProperties.getPlayerDrugs(player).addDrugAbuse(drug, ticks);
    }

    public static DrugEffects getDrugEffects(Player Player) {
        return PlayerProperties.getPlayerDrugs(Player).getDrugEffects();
    }

    @OnlyIn(Dist.CLIENT)
    public static DrugEffects getDrugEffects() {
        assert Minecraft.getInstance().player != null;
        return PlayerProperties.getPlayerDrugs(Minecraft.getInstance().player).getDrugEffects();
    }

    public static void tick(Player player) {
        IPlayerDrugs playerDrugs = PlayerProperties.getPlayerDrugs(player);
        Map<Drug, Float> map = playerDrugs.getActiveDrugs();
        List<DrugInstance> toRemove = new ArrayList<>();

        playerDrugs.tickDrugAbuse();
        map.clear();

        for (DrugInstance drugInstance : playerDrugs.getDrugSources()) {
            Drug drug = drugInstance.getDrug();

            if (drugInstance.isActive()) {
                if (!map.containsKey(drug)) {
                    map.put(drug, 0F);
                }

                float effect = drugInstance.getEffect(drug.getEnvelope());
                if (effect < 0) {
                    toRemove.add(drugInstance);
                    continue;
                }

                map.put(drug, map.get(drug) + effect);
            }
        }

        for (DrugInstance drugInstance : toRemove) {
            playerDrugs.removeDrugSource(drugInstance);
        }

        for (Map.Entry<Drug, Float> entry : map.entrySet()) {
            float clamped = Mth.clamp(entry.getValue(), 0F, 1F);
            if (clamped < 1E-6F) continue;
            entry.setValue(clamped);
            Drug drug = entry.getKey();
            drug.effectTick(player, getDrugEffects(player), clamped);
            if (drug.getAbuseAdder() > 0) addAbuse(player, drug, drug.getAbuseAdder());
        }

        if (!player.level().isClientSide) // abuse map isn't synced, so to fix spasms, this is here
            playerDrugs.getDrugAbuseMap().forEach((drug, abuse) -> drug.abuseTick(player, getDrugEffects(player), abuse));
    }

    public void startUse(Player player) {
    }

    @OnlyIn(Dist.CLIENT)
    public void renderTick(DrugEffects drugEffects, float effect) {
    }

    public void effectTick(Player player, DrugEffects drugEffects, float effect) {
    }

    public void abuseTick(Player player, DrugEffects drugEffects, int abuse) {
    }

    public int getAbuse(Player Player) {
        return Drug.getAbuse(Player, this);
    }

    public int getAbuseAdder() {
        return abuseAdder;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    public static class DrugProperties {
        private int abuseAdder = 0;
        private Envelope envelope;

        public DrugProperties adsr(float attack, float decay, float sustain, float release) {
            envelope = new Envelope(attack, decay, sustain, release);
            return this;
        }

        public DrugProperties abuse(int ticks) {
            abuseAdder = ticks;
            return this;
        }
    }

    public static class Envelope {
        private final float attack;
        private final float decay;
        private final float sustain;
        private final float release;

        public Envelope(float attack, float decay, float sustain, float release) {
            this.attack = attack;
            this.decay = decay;
            this.sustain = sustain;
            this.release = release;
        }

        public float getLevel(int timeActive, int duration) {
            if (timeActive < duration) {
                return getRisingLevel(timeActive);
            } else {
                return getDecayingLevel(timeActive - duration);
            }
        }

        private float getRisingLevel(float timeRising) {
            if (timeRising < attack) {
                return lerp(0F, 1F, (float) Mth.smoothstep(timeRising/attack));
            } else if (timeRising < attack + decay) {
                return lerp(1F, sustain, (float) Mth.smoothstep((timeRising - attack)/decay));
            } else {
                return sustain;
            }
        }

        private float getDecayingLevel(float timeDecaying) {
            return lerp(sustain, 0F, (float) Mth.smoothstep(timeDecaying/release));
        }

        private float lerp(float a, float b, float t) {
            return (1F - t)*a + t*b;
        }
    }
}