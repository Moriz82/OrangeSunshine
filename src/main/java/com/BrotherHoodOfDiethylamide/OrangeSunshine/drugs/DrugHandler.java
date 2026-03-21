package com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.IPlayerDrugs;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.PlayerProperties;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = OrangeSunshine.MODID)
public class DrugHandler {
    private static final UUID DRUG_UUID = UUID.fromString("512eebf1-6b63-4e4e-be79-42d90813a70a");

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        DrugEffects drugEffects = Drug.getDrugEffects(player);
        if (event.phase == TickEvent.Phase.START) {
            Drug.tick(player);

            modifyAttribute(player, SharedMonsterAttributes.MOVEMENT_SPEED, "Drug movement speed",
                    Math.max(drugEffects.MOVEMENT_SPEED.getValue(), -0.5F), 2);

            if ((int) drugEffects.DROWN_RATE.getValue() > 0) {
                player.setAir(player.getAir() - (int) drugEffects.DROWN_RATE.getValue());
                if (player.getAir() <= -20) {
                    player.setAir(0);
                    player.attackEntityFrom(DamageSource.DROWN, 2F);
                }
            }
            if (drugEffects.HUNGER_RATE.getValue() > 0) {
                player.addExhaustion(0.005F * drugEffects.HUNGER_RATE.getValue());
            }
            if (drugEffects.REGENERATION_RATE.getValue() > 0) {
                int k = (int) (50 / (2 * drugEffects.REGENERATION_RATE.getValue()));
                if (k > 0 && player.ticksExisted % k == 0 && player.getHealth() < player.getMaxHealth()) {
                    player.heal(1F);
                }
            }
        } else {
            drugEffects.reset(false);
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        DrugEffects effects = Drug.getDrugEffects(event.getEntityPlayer());
        float digMod = effects.DIG_SPEED.getValue();
        if (digMod != 0) {
            event.setNewSpeed(event.getNewSpeed() * (1.0F + digMod));
        }
    }

    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event) {
        if (event.world.isRemote) return;

        WorldServer serverWorld = (WorldServer) event.world;
        for (EntityPlayerMP player : serverWorld.getPlayers(EntityPlayerMP.class, p -> true)) {
            IPlayerDrugs playerDrugs = PlayerProperties.getPlayerDrugs(player);
            if (playerDrugs.getSmokeTicks() > 0) {
                playerDrugs.setSmokeTicks(playerDrugs.getSmokeTicks() - 1);
                Vec3d lookVector = player.getLookVec();
                for (int i = 0; i < 10; i++) {
                    serverWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                            player.posX, player.posY + player.getEyeHeight() - 0.15, player.posZ,
                            0, lookVector.x, lookVector.y, lookVector.z, 0.1);
                }
            }
        }
    }

    private static void modifyAttribute(EntityLivingBase entity, IAttribute attribute, String name, double value, int operation) {
        IAttributeInstance attributeInstance = entity.getEntityAttribute(attribute);
        if (attributeInstance == null) return;
        AttributeModifier oldModifier = attributeInstance.getModifier(DRUG_UUID);
        if (oldModifier != null) {
            attributeInstance.removeModifier(oldModifier);
        }
        attributeInstance.applyModifier(new AttributeModifier(DRUG_UUID, name, value, operation));
    }
}
