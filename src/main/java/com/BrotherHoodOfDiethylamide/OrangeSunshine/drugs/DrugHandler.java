package com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.IPlayerDrugs;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.PlayerProperties;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.events.hooks.IncreaseAirSupplyEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = OrangeSunshine.MOD_ID)
public class DrugHandler {
    private static final UUID uuid = UUID.fromString("512eebf1-6b63-4e4e-be79-42d90813a70a");

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        DrugEffects drugEffects = Drug.getDrugEffects(player);
        if (event.phase == TickEvent.Phase.START) {
            Drug.tick(player);

            modifyAttribute(player, Attributes.MOVEMENT_SPEED, "Drug movement speed", Math.max(drugEffects.MOVEMENT_SPEED.getValue(), -0.5F), AttributeModifier.Operation.MULTIPLY_TOTAL);
            modifyAttribute(player, Attributes.ATTACK_SPEED, "Drug attack speed", Math.max(drugEffects.DIG_SPEED.getValue(), -0.5F), AttributeModifier.Operation.MULTIPLY_TOTAL);

            if ((int) drugEffects.DROWN_RATE.getValue() > 0) {
                player.setAirSupply(player.getAirSupply() - (int) drugEffects.DROWN_RATE.getValue());
                if (player.getAirSupply() <= -20) {
                    player.setAirSupply(0);
                    player.hurt(DamageSource.DROWN, 2F);
                }
            }
            if (drugEffects.HUNGER_RATE.getValue() > 0) {
                player.causeFoodExhaustion(0.005F*drugEffects.HUNGER_RATE.getValue());
            }
            if (drugEffects.REGENERATION_RATE.getValue() > 0) {
                int k = (int) (50/(2*drugEffects.REGENERATION_RATE.getValue()));
                if (k > 0 && player.tickCount % k == 0 && player.getHealth() < player.getMaxHealth()) {
                    player.heal(1F);
                }
            }
        } else {
            drugEffects.reset(false);
        }
    }

    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event) {
        if (event.world.isClientSide) return;

        ServerWorld serverWorld = (ServerWorld) event.world;
        for (ServerPlayerEntity player : serverWorld.players()) {
            IPlayerDrugs playerDrugs = PlayerProperties.getPlayerDrugs(player);
            if (playerDrugs.getSmokeTicks() > 0) {
                playerDrugs.setSmokeTicks(playerDrugs.getSmokeTicks() - 1);
                Vector3d lookVector = player.getLookAngle();
                serverWorld.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + player.getEyeHeight() - 0.15F, player.getZ(), 0, lookVector.x, lookVector.y, lookVector.z, 0.1);
            }
        }
    }

    @SubscribeEvent
    public static void breatheEvent(IncreaseAirSupplyEvent event) {
        if (event.livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.livingEntity;
            DrugEffects drugEffects = Drug.getDrugEffects(player);
            if (drugEffects.DROWN_RATE.getValue() > 0) event.setCanceled(true);
        }
    }

    private static void modifyAttribute(LivingEntity entity, Attribute attribute, String name, double value, AttributeModifier.Operation op) {
        ModifiableAttributeInstance attributeInstance = entity.getAttribute(attribute);
        if (attributeInstance == null) return;
        attributeInstance.removeModifier(uuid);
        attributeInstance.addTransientModifier(new AttributeModifier(uuid, name, value, op));
    }
}
