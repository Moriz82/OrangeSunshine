/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug;

import moriz.orangesunshine.*;
import moriz.orangesunshine.advancement.PSCriteria;
import moriz.orangesunshine.entity.*;
import moriz.orangesunshine.entity.drug.hallucination.HallucinationManager;
import moriz.orangesunshine.entity.drug.influence.DrugInfluence;
import moriz.orangesunshine.entity.drug.sound.DrugMusicManager;
import moriz.orangesunshine.item.PSItems;
import moriz.orangesunshine.mixin.MixinLivingEntity;
import moriz.orangesunshine.network.Channel;
import moriz.orangesunshine.network.MsgDrugProperties;
import moriz.orangesunshine.util.NbtSerialisable;
import moriz.orangesunshine.PSDamageTypes;
import moriz.orangesunshine.PSSounds;
import moriz.orangesunshine.ParticleHelper;
import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.entity.RealityRiftEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class DrugProperties implements NbtSerialisable {
    public static final Identifier DRUG_EFFECT_ID = OrangeSunshine.id("drug_effect");

    private final Map<DrugType, Drug> drugs = DrugType.REGISTRY.stream().collect(Collectors.toMap(Function.identity(), DrugType::create));
    private final List<DrugInfluence> influences = new ArrayList<>();

    private boolean dirty;

    private final HallucinationManager hallucinations = new HallucinationManager(this);
    private final DrugMusicManager soundManager = new DrugMusicManager(this);

    private int timeBreathingSmoke;
    @Nullable
    private Vector3f breathSmokeColor;

    private final Player entity;

    private final Stomach stomach;

    public DrugProperties(Player entity) {
        this.entity = entity;
        this.stomach = new Stomach(this);
    }

    public static DrugProperties of(Player player) {
        return ((DrugPropertiesContainer)player).getDrugProperties();
    }

    public static Stream<DrugProperties> stream(Entity entity) {
        if (entity instanceof DrugPropertiesContainer c) {
            return Stream.of(c.getDrugProperties());
        }
        return Stream.empty();
    }

    public static Optional<DrugProperties> of(Entity entity) {
        if (entity instanceof DrugPropertiesContainer c) {
            return Optional.of(c.getDrugProperties());
        }
        return Optional.empty();
    }

    public Player asEntity() {
        return entity;
    }

    public DamageSource damageOf(ResourceKey<DamageType> type) {
        return PSDamageTypes.create(entity.level(), type);
    }

    public Stomach getStomach() {
        return stomach;
    }

    public void markDirty() {
        dirty = true;
    }

    public HallucinationManager getHallucinations() {
        return hallucinations;
    }

    public DrugMusicManager getMusicManager() {
        return soundManager;
    }

    public Drug getDrug(DrugType type) {
        return drugs.computeIfAbsent(type, DrugType::create);
    }

    public float getDrugValue(DrugType type) {
        if (!drugs.containsKey(type)) {
            return 0F;
        }
        return (float) getDrug(type).getActiveValue();
    }

    public boolean isDrugActive(DrugType type) {
        return drugs.containsKey(type) && getDrugValue(type) > Mth.EPSILON;
    }

    public boolean isTripping() {
        float f = getModifier(Drug.MOVEMENT_HALLUCINATION_STRENGTH)
                + getModifier(Drug.CONTEXTUAL_HALLUCINATION_STRENGTH)
                + getModifier(Drug.COLOR_HALLUCINATION_STRENGTH);
        return f > 0.7F;
    }

    public void addToDrug(DrugType type, double effect) {
        getDrug(type).addToDesiredValue(effect);
        PSCriteria.DRUG_EFFECTS_CHANGED.trigger(this);
        markDirty();
        OrangeSunshine.LOGGER.info("[OrangeSunshine] addToDrug {} +{} → desiredNow={} side={}",
                type.id().getPath(),
                String.format("%.4f", effect),
                String.format("%.4f", getDrugValue(type)),
                entity.level().isClientSide() ? "CLIENT" : "SERVER");
    }

    public void setDrugValue(DrugType type, double effect) {
        getDrug(type).setDesiredValue(effect);
        PSCriteria.DRUG_EFFECTS_CHANGED.trigger(this);
        markDirty();
    }

    public void addToDrug(DrugInfluence influence) {
        influences.add(influence);
        markDirty();
    }

    public void addAll(Iterable<DrugInfluence> influences) {
        influences.forEach(influence -> this.influences.add(influence.clone()));
        markDirty();
    }

    public Collection<Drug> getAllDrugs() {
        return drugs.values();
    }

    public Set<DrugType> getAllDrugNames() {
        return drugs.keySet();
    }

    public void startBreathingSmoke(int time, Vector3f color) {
        this.breathSmokeColor = color;
        this.timeBreathingSmoke = time + 10; //10 is the time spent breathing in
        markDirty();

        entity.level().playSound(entity, entity, PSSounds.ENTITY_PLAYER_BREATH, SoundSource.PLAYERS, 0.02F, 1.5F);
    }

    public boolean isBreathingSmoke() {
        return timeBreathingSmoke > 0;
    }

    public int getAge() {
        return entity.tickCount;
    }

    public void onTick() {
        if (entity.tickCount % 5 == 0) { //4 times / sec is enough
            influences.removeIf(influence -> {
                if (influence.update(this)) {
                    markDirty();
                    return true;
                }
                return false;
            });
        }

        if (entity.getUseItem().is(PSItems.BONG) && entity.getRandom().nextInt(3) == 0) {
            entity.playSound(PSSounds.BONG_HIT, 1, 1);
        }

        drugs.values().forEach(drug -> drug.update(this));

        stomach.onTick();
        soundManager.update();

        RandomSource random = entity.getRandom();

        if (entity.level().isClientSide()) {
            hallucinations.update();

            if (entity.onGround() && random.nextFloat() < getModifier(Drug.JUMP_CHANCE)) {
                ((MixinLivingEntity)entity).invokeJump();
            }

            if (!entity.swinging && random.nextFloat() < getModifier(Drug.PUNCH_CHANCE)) {
                entity.swing(InteractionHand.MAIN_HAND);
            }
        } else {
            if (random.nextFloat() < getModifier(Drug.DROWSYNESS)) {
                entity.causeFoodExhaustion(0.05F);
            }
        }

        if (isBreathingSmoke()) {
            timeBreathingSmoke--;

            if (timeBreathingSmoke > 10 && entity.level().isClientSide()) {
                Vec3 look = entity.getViewVector(1);

                if (random.nextInt(2) == 0) {
                    float s = random.nextFloat() * 0.05f + 0.2f;
                    ParticleHelper.spawnColoredParticle(entity, breathSmokeColor, look, s, 6.0f);
                    ParticleHelper.spawnColoredParticle(entity, breathSmokeColor, look, s, 6.0f);
                    ParticleHelper.spawnColoredParticle(entity, breathSmokeColor, look, s, 6.0f);
                }

                if (random.nextInt(5) == 0) {
                    float s = random.nextFloat() * 0.05f + 0.1f;
                    ParticleHelper.spawnColoredParticle(entity, breathSmokeColor, look, s, 10.5f);
                    ParticleHelper.spawnColoredParticle(entity, breathSmokeColor, look, s, 10.5f);
                    ParticleHelper.spawnColoredParticle(entity, breathSmokeColor, look, s, 10.5f);
                }
            }
        }

        changeDrugModifierMultiply(entity, Attributes.MOVEMENT_SPEED, getModifier(Drug.SPEED));
        changeDrugModifierMultiply(entity, Attributes.ATTACK_SPEED, getModifier(Drug.SPEED));

        // Verbose periodic log: every 100 ticks (5 s) log non-zero drugs + pending influence count
        if (entity.tickCount % 100 == 0) {
            long nonZero = drugs.values().stream().filter(d -> d.getActiveValue() > 0.001).count();
            if (nonZero > 0 || !influences.isEmpty()) {
                OrangeSunshine.LOGGER.info("[OrangeSunshine] tick={} side={} activeDrugs={} pendingInfluences={}",
                        entity.tickCount,
                        entity.level().isClientSide() ? "CLIENT" : "SERVER",
                        nonZero, influences.size());
                drugs.forEach((type, drug) -> {
                    if (drug.getActiveValue() > 0.001) {
                        OrangeSunshine.LOGGER.info("[OrangeSunshine]   {} act={}", type.id().getPath(),
                                String.format("%.4f", drug.getActiveValue()));
                    }
                });
                influences.forEach(inf -> OrangeSunshine.LOGGER.info("[OrangeSunshine]   influence {} delay={} remaining={}",
                        inf.getDrugType() != null ? inf.getDrugType().id().getPath() : "null",
                        inf.getDelay(),
                        String.format("%.4f", inf.getMaxInfluence())));
            }
        }

        if (dirty) {
            dirty = false;
            sendCapabilities();
        }

        if (!entity.level().isClientSide() && OrangeSunshine.getConfig().balancing.randomTicksUntilRiftSpawn > 0) {
            if (random.nextInt(OrangeSunshine.getConfig().balancing.randomTicksUntilRiftSpawn) == 0) {
                RealityRiftEntity.spawn(entity);
            }
        }
    }

    public void sendCapabilities() {
        if (!entity.level().isClientSide() && entity instanceof ServerPlayer serverPlayer) {
            // log non-zero drug values being synced
            drugs.forEach((type, drug) -> {
                if (drug.getActiveValue() > 0.001 || ((drug instanceof moriz.orangesunshine.entity.drug.type.SimpleDrug sd) && sd.getDesiredValue() > 0.001)) {
                    OrangeSunshine.LOGGER.info("[OrangeSunshine] S2C sync {} act={} des={}",
                            type.id().getPath(),
                            String.format("%.3f", drug.getActiveValue()),
                            (drug instanceof moriz.orangesunshine.entity.drug.type.SimpleDrug sd2)
                                    ? String.format("%.3f", sd2.getDesiredValue()) : "?");
                }
            });
            Channel.UPDATE_DRUG_PROPERTIES.sendToSurroundingPlayers(new MsgDrugProperties(this), entity);
            Channel.UPDATE_DRUG_PROPERTIES.sendToPlayer(new MsgDrugProperties(this), serverPlayer);
        }
    }

    @Override
    public void fromNbt(CompoundTag tagCompound) {
        CompoundTag drugData = tagCompound.getCompoundOrEmpty("Drugs");
        drugs.clear();
        drugData.keySet().forEach(key -> {
            Identifier id = Identifier.tryParse(key);
            if (id != null) {
                DrugType.REGISTRY.getOptional(id).ifPresent(type -> getDrug(type).fromNbt(drugData.getCompoundOrEmpty(key)));
            }
        });
        influences.clear();
        for (Tag tag : tagCompound.getListOrEmpty("drugInfluences")) {
            if (tag instanceof CompoundTag influenceTag) {
                DrugInfluence.loadFromNbt(influenceTag).ifPresent(this::addToDrug);
            }
        }
        stomach.fromNbt(tagCompound.getCompoundOrEmpty("stomach"));
        dirty = false;
    }

    @Override
    public void toNbt(CompoundTag compound) {
        CompoundTag drugsComp = new CompoundTag();
        drugs.forEach((key, drug) -> {
            drugsComp.put(key.id().toString(), drug.toNbt());
        });
        compound.put("Drugs", drugsComp);

        ListTag influenceTagList = new ListTag();
        for (DrugInfluence influence : influences) {
            influenceTagList.add(influence.toNbt());
        }
        compound.put("drugInfluences", influenceTagList);
        compound.put("stomach", stomach.toNbt());
    }

    public void copyFrom(DrugProperties old, boolean alive) {
        if (alive) {
            influences.clear();
            influences.addAll(old.influences);
            drugs.clear();
            drugs.putAll(old.drugs);
            timeBreathingSmoke = old.timeBreathingSmoke;
            breathSmokeColor = old.breathSmokeColor;
            dirty = true;
        }
    }

    public boolean onAwoken() {
        drugs.values().forEach(drug -> drug.onWakeUp(this));
        influences.clear();
        dirty = true;

        // TODO: (Sollace) Implement longer sleeping/comas
        return true;
    }

    public Optional<Component> trySleep(BlockPos pos) {
        return getAllDrugs().stream().flatMap(drug -> drug.trySleep(pos).stream()).findFirst();
    }

    public float getModifier(Drug.AggregateModifier modifier) {
        return modifier.get(this);
    }

    private void changeDrugModifierMultiply(LivingEntity entity, Holder<Attribute> attribute, double value) {
        // 2: ret *= 1.0 + value
        changeDrugModifier(entity, attribute, value - 1.0, Operation.ADD_MULTIPLIED_TOTAL);
    }

    private void changeDrugModifier(LivingEntity entity, Holder<Attribute> attribute, double value, Operation operation) {
        AttributeInstance speedInstance = entity.getAttribute(attribute);
        if (speedInstance == null) {
            return;
        }
        speedInstance.removeModifier(DRUG_EFFECT_ID);
        speedInstance.addOrUpdateTransientModifier(new AttributeModifier(DRUG_EFFECT_ID, value, operation));
    }
}
