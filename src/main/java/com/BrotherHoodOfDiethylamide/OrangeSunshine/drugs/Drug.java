package com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.IPlayerDrugs;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.PlayerDrugs;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.PlayerProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Drug {
    private final Envelope envelope;
    private final int abuseAdder;

    public Drug(DrugProperties properties) {
        this.envelope = properties.envelope;
        this.abuseAdder = properties.abuseAdder;
    }

    @Nullable
    public static Drug byName(String name) {
        return DrugRegistry.DRUGS.get(name);
    }

    public static String toName(Drug drug) {
        for (Map.Entry<String, Drug> entry : DrugRegistry.DRUGS.entrySet()) {
            if (entry.getValue() == drug) {
                return entry.getKey();
            }
        }
        return "unknown";
    }

    public static void addDrug(EntityPlayer player, DrugInstance drugInstance) {
        drugInstance.getDrug().startUse(player);
        PlayerProperties.getPlayerDrugs(player).addDrugSource(drugInstance);
        if (player instanceof EntityPlayerMP) {
            PlayerDrugs.sync((EntityPlayerMP) player);
        }
    }

    public static void clearDrugs(EntityPlayer player) {
        PlayerProperties.getPlayerDrugs(player).clearDrugSources();
    }

    public static List<DrugInstance> getDrugSources(EntityPlayer player) {
        return PlayerProperties.getPlayerDrugs(player).getDrugSources();
    }

    public static Map<Drug, Float> getActiveDrugs(EntityPlayer player) {
        return PlayerProperties.getPlayerDrugs(player).getActiveDrugs();
    }

    public static int getAbuse(EntityPlayer player, Drug drug) {
        return PlayerProperties.getPlayerDrugs(player).getDrugAbuse(drug);
    }

    public static void addAbuse(EntityPlayer player, Drug drug, int ticks) {
        PlayerProperties.getPlayerDrugs(player).addDrugAbuse(drug, ticks);
    }

    public static DrugEffects getDrugEffects(EntityPlayer player) {
        return PlayerProperties.getPlayerDrugs(player).getDrugEffects();
    }

    @SideOnly(Side.CLIENT)
    public static DrugEffects getDrugEffects() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return new DrugEffects();
        return PlayerProperties.getPlayerDrugs(player).getDrugEffects();
    }

    public static void tick(EntityPlayer player) {
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
                if (effect < 1e-5f && drugInstance.getTimeActive() > drugInstance.getDuration()) {
                    toRemove.add(drugInstance);
                    continue;
                }

                map.put(drug, map.get(drug) + effect);
            }
        }

        for (DrugInstance drugInstance : toRemove) {
            playerDrugs.removeDrugSource(drugInstance);
        }
        if (!toRemove.isEmpty() && !player.world.isRemote && player instanceof EntityPlayerMP) {
            PlayerDrugs.sync((EntityPlayerMP) player);
        }

        for (Map.Entry<Drug, Float> entry : map.entrySet()) {
            float clamped = MathHelper.clamp(entry.getValue(), 0F, 1F);
            if (clamped < 1E-6F) continue;
            entry.setValue(clamped);
            Drug drug = entry.getKey();
            drug.effectTick(player, getDrugEffects(player), clamped);
            if (drug.getAbuseAdder() > 0) addAbuse(player, drug, drug.getAbuseAdder());
        }

        if (!player.world.isRemote)
            playerDrugs.getDrugAbuseMap().forEach((drug, abuse) -> drug.abuseTick(player, getDrugEffects(player), abuse));
    }

    public void startUse(EntityPlayer player) {
    }

    @SideOnly(Side.CLIENT)
    public void renderTick(DrugEffects drugEffects, float effect) {
    }

    public void effectTick(EntityPlayer player, DrugEffects drugEffects, float effect) {
    }

    public void abuseTick(EntityPlayer player, DrugEffects drugEffects, int abuse) {
    }

    public int getAbuse(EntityPlayer player) {
        return Drug.getAbuse(player, this);
    }

    public int getAbuseAdder() {
        return abuseAdder;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    public static class DrugProperties {
        int abuseAdder = 0;
        Envelope envelope;

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
                return lerp(0F, 1F, (float) smoothstep(timeRising / attack));
            } else if (timeRising < attack + decay) {
                return lerp(1F, sustain, (float) smoothstep((timeRising - attack) / decay));
            } else {
                return sustain;
            }
        }

        private float getDecayingLevel(float timeDecaying) {
            return lerp(sustain, 0F, (float) smoothstep(timeDecaying / release));
        }

        private float lerp(float a, float b, float t) {
            return (1F - t) * a + t * b;
        }

        private static double smoothstep(double x) {
            x = MathHelper.clamp(x, 0.0, 1.0);
            return x * x * (3.0 - 2.0 * x);
        }
    }
}
