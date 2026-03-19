/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import org.joml.Vector3f;
import moriz.orangesunshine.ParticleHelper;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.util.MathUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;

/**
 * Created by lukas on 03.03.14.
 */
public class RealityRiftEntity extends Entity {
    private static final EntityDataAccessor<Float> SIZE =
            SynchedEntityData.defineId(RealityRiftEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> INSTABILITY =
            SynchedEntityData.defineId(RealityRiftEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> CLOSING =
            SynchedEntityData.defineId(RealityRiftEntity.class, EntityDataSerializers.BOOLEAN);

    public float visualRiftSize;

    public static void spawn(Entity entity) {
        RealityRiftEntity rift = PSEntities.REALITY_RIFT.create(entity.level(), EntitySpawnReason.TRIGGERED);
        if (rift == null) {
            return;
        }

        Level level = entity.level();
        rift.setPos(
                entity.getX() + (level.random.nextDouble() - 0.5) * 100,
                entity.getY() + (level.random.nextDouble() - 0.5) * 100,
                entity.getZ() + (level.random.nextDouble() - 0.5) * 100
        );
        level.addFreshEntity(rift);
    }

    RealityRiftEntity(EntityType<RealityRiftEntity> type, Level level) {
        super(type, level);
        setRiftSize((float)level.random.triangle(0.5F, 0.5F));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SIZE, 0F);
        builder.define(CLOSING, false);
        builder.define(INSTABILITY, 0F);
    }

    public float getRiftSize() {
        return getEntityData().get(SIZE);
    }

    public void setRiftSize(float size) {
        getEntityData().set(SIZE, Math.max(0, size));
    }

    public void addToRift(float size) {
        setRiftSize(getRiftSize() + size);
    }

    public float takeFromRift(float size) {
        if (isCritical()) {
            return 0.2f;
        }

        float riftSize = getRiftSize();
        float newVal = Math.max(riftSize - size, 0.0f);
        setRiftSize(newVal);
        return riftSize - newVal;
    }

    public float getInstability() {
        return getEntityData().get(INSTABILITY);
    }

    public void setInstability(float instability) {
        getEntityData().set(INSTABILITY, Math.max(0, instability));
    }

    public boolean isRiftClosing() {
        return getEntityData().get(CLOSING);
    }

    public void setRiftClosing(boolean closing) {
        getEntityData().set(CLOSING, closing);
    }

    public boolean isCritical() {
        return (getInstability() > 0 || getRiftSize() > 3) && !isRiftClosing();
    }

    @Override
    public Component getDisplayName() {
        return super.getDisplayName().copy().withStyle(ChatFormatting.OBFUSCATED);
    }

    @Override
    public void tick() {
        super.tick();
        setDeltaMovement(Vec3.ZERO);

        boolean critical = isCritical();

        if (level().isClientSide()) {
            Vec3 pos = position();
            Supplier<Vec3> particlePositionSupplier = () -> {
                float distance = random.nextFloat() * random.nextFloat();
                return ParticleHelper.apply(pos, x -> x + (random.nextFloat() * 8 - 4) * distance)
                        .add(0, getBbHeight() / 2F, 0);
            };
            ParticleHelper.spawnParticles(level(), ParticleTypes.LARGE_SMOKE, particlePositionSupplier,
                    Suppliers.ofInstance(Vec3.ZERO), random.nextInt(3));
            ParticleHelper.spawnParticles(level(),
                    new DustParticleOptions(0xFF8080, 1),
                    particlePositionSupplier,
                    Suppliers.ofInstance(new Vec3(-10, -10, -10)),
                    random.nextInt(2));
            ParticleHelper.spawnParticles(level(), ParticleTypes.ENCHANT,
                    Suppliers.ofInstance(pos.add(0, 1 + (getBbHeight() / 2F), 0)), () -> {
                        float distance = random.nextFloat() * random.nextFloat();
                        return ParticleHelper.apply(Vec3.ZERO, x -> x + (random.nextFloat() * 8 - 4) * distance)
                                .add(0, getBbHeight() / 2F, 0);
                    }, 1);
        }

        float searchDistance = 5.0f + getInstability() * 50.0f;
        for (LivingEntity livingEntity : level().getEntitiesOfClass(
                LivingEntity.class,
                getBoundingBox().inflate(searchDistance),
                EntitySelector.NO_CREATIVE_OR_SPECTATOR
        )) {
            double dist = livingEntity.distanceTo(this);
            double effect = (searchDistance - dist) * 0.0005 * getRiftSize();

            if (effect > 0.0) {
                DrugProperties.of(livingEntity).ifPresentOrElse(drugProperties -> {
                    drugProperties.addToDrug(DrugType.ZERO, effect * 20.0f);
                    drugProperties.addToDrug(DrugType.POWER, effect * 200.0f);
                }, () -> {
                    if (critical) {
                        livingEntity.hurt(damageSources().magic(), (float)effect * 20.0f);
                    }
                });
            }
        }

        if (critical) {
            float prevInstability = getInstability();
            float newInstability = Math.min(prevInstability + 0.001f, 1.0f);
            setInstability(newInstability);

            float previousDestroyRange = prevInstability * 50.0f;
            float newDestroyRange = newInstability * 50.0f;

            if (previousDestroyRange < newDestroyRange) {
                int destroyRange = Mth.ceil(newDestroyRange);
                BlockPos center = BlockPos.containing(position());
                BlockPos.withinManhattan(center, destroyRange, destroyRange, destroyRange).forEach(pos -> {
                    if (pos.closerThan(center, newDestroyRange)
                            && !pos.closerThan(center, previousDestroyRange)
                            && !level().isEmptyBlock(pos)) {
                        level().setBlockAndUpdate(pos, PSBlocks.GLITCH.defaultBlockState());
                    }
                });
            }
        }

        if (isRiftClosing()) {
            setRiftSize(getRiftSize() - 1F / 20F);
        } else if (!critical) {
            setRiftSize(getRiftSize() - 1F / 20F / 20F / 60F);
        }

        visualRiftSize = MathUtils.nearValue(visualRiftSize, getRiftSize(), 0.05f, 0.005f);

        if (!level().isClientSide()) {
            if (getInstability() >= 0.9f) {
                setRiftClosing(true);
            }

            if (visualRiftSize <= 0.0f && getRiftSize() <= 0.0f) {
                discard();
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        setRiftSize(input.getFloatOr("riftSize", 0));
        setRiftClosing(input.getBooleanOr("isRiftClosing", false));
        setInstability(input.getFloatOr("instability", 0));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putFloat("riftSize", getRiftSize());
        output.putBoolean("isRiftClosing", isRiftClosing());
        output.putFloat("instability", getInstability());
    }

    @Override
    public boolean hurtServer(ServerLevel level, net.minecraft.world.damagesource.DamageSource damageSource, float amount) {
        return false;
    }
}
