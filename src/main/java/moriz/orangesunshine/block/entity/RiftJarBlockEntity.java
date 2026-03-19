/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block.entity;

import java.util.*;

import moriz.orangesunshine.client.render.bezier.Bezier;
import moriz.orangesunshine.entity.RealityRiftEntity;
import moriz.orangesunshine.entity.PSEntities;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RiftJarBlockEntity extends SyncedBlockEntity {
    public float currentRiftFraction;
    public int ticksAliveVisual;

    public boolean isOpening;
    public float fractionOpen;

    public boolean jarBroken = false;
    public boolean suckingRifts = true;
    public float fractionHandleUp;

    private final Map<UUID, JarRiftConnection> riftConnections = new HashMap<>();

    public RiftJarBlockEntity(BlockPos pos, BlockState state) {
        super(PSBlockEntities.RIFT_JAR, pos, state);
    }

    public Collection<JarRiftConnection> getConnections() {
        return riftConnections.values();
    }

    public void tickAnimation() {
        fractionOpen = MathUtils.nearValue(fractionOpen, isOpening ? 1 : 0, 0, 0.02F);
        fractionHandleUp = MathUtils.nearValue(fractionHandleUp, isSuckingRifts() ? 0 : 1, 0, 0.04F);
        ticksAliveVisual++;
    }

    public void tick(ServerLevel world) {
        tickAnimation();

//        if (!world.isClient)
//        {
//            boolean before = suckingRifts;
//            suckingRifts = !world.isDaytime() && world.canBlockSeeTheSky(xCoord, yCoord, zCoord);
//
//            if (before != suckingRifts)
//            {
//                markDirty();
//                world.markBlockForUpdate(xCoord, yCoord, zCoord);
//            }
//        }

        if (isSuckingRifts()) {
            if (fractionOpen > 0) {
                List<RealityRiftEntity> rifts = getAffectedRifts();

                if (rifts.size() > 0) {
                    float minus = (1F / rifts.size()) * 0.001f * fractionOpen;
                    rifts.forEach(rift -> {
                        currentRiftFraction += rift.takeFromRift(minus);

                        JarRiftConnection connection = createAndGetRiftConnection(rift);
                        connection.fractionUp = Math.min(1, connection.fractionUp + 0.02f * fractionOpen);
                    });
                }
            }
        } else {
            if (fractionOpen > 0) {
                float minus = Math.min(0.0004f * fractionOpen * currentRiftFraction + 0.0004f, currentRiftFraction);

                BlockPos pos = getBlockPos();
                Vec3 center = Vec3.atCenterOf(pos);
                world.getEntitiesOfClass(LivingEntity.class, new AABB(
                        pos.getX() - 5, pos.getY() - 5, pos.getZ() - 2,
                        pos.getX() + 6, pos.getY() + 6, pos.getZ() + 6
                    ), entity -> entity.isAlive() && (!(entity instanceof Player player) || (!player.isCreative() && !player.isSpectator()))
                ).stream().flatMap(DrugProperties::stream).forEach(drugProperties -> {
                    double effect = (5 - drugProperties.asEntity().position().distanceTo(center)) * 0.2F * minus;
                    drugProperties.addToDrug(DrugType.ZERO, effect * 5);
                    drugProperties.addToDrug(DrugType.POWER, effect * 35);
                });

                currentRiftFraction -= minus;
            }
        }

        riftConnections.values().removeIf(connection -> (connection.fractionUp -= 0.01F) <= 0);

        if (currentRiftFraction > 1) {
            jarBroken = true;

            releaseRift();
            world.destroyBlock(getBlockPos(), false);
            Vec3 explosionPosition = Vec3.atCenterOf(getBlockPos());
            world.explode(null, explosionPosition.x, explosionPosition.y, explosionPosition.z, 1, false, Level.ExplosionInteraction.BLOCK);
        }
    }

    public JarRiftConnection createAndGetRiftConnection(RealityRiftEntity rift) {
        return riftConnections.computeIfAbsent(rift.getUUID(), id -> new JarRiftConnection(rift));
    }

    public boolean toggleRiftJarOpen() {
        if (level instanceof ServerLevel world) {
            isOpening = !isOpening;
            setChanged();
            world.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
        return isOpening;
    }

    public void toggleSuckingRifts() {
        if (level instanceof ServerLevel world) {
            suckingRifts = !suckingRifts;
            setChanged();
            world.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    public boolean isSuckingRifts() {
        return suckingRifts;
    }

    public void releaseRift() {
        if (currentRiftFraction > 0) {
            List<RealityRiftEntity> rifts = getAffectedRifts();

            if (rifts.size() > 0) {
                rifts.get(0).addToRift(currentRiftFraction);
            } else if (level instanceof ServerLevel world) {
                RealityRiftEntity rift = PSEntities.REALITY_RIFT.create(world, EntitySpawnReason.TRIGGERED);
                if (rift != null) {
                    rift.setPos(Vec3.atCenterOf(getBlockPos()).add(5, 3, 0.5));
                    rift.setRiftSize(currentRiftFraction);
                    world.addFreshEntity(rift);
                }
            }

            currentRiftFraction = 0.0f;
        }
    }

    public List<RealityRiftEntity> getAffectedRifts() {
        if (level == null) {
            return List.of();
        }
        BlockPos pos = getBlockPos();
        return level.getEntitiesOfClass(RealityRiftEntity.class, new AABB(
                pos.getX() - 2.0f, pos.getY() + 0.0f, pos.getZ() - 2.0f,
                pos.getX() + 3.0f, pos.getY() + 10, pos.getZ() + 3
            ), RealityRiftEntity::isAlive
        );
    }

    @Override
    protected void writeNbt(CompoundTag compound) {
        compound.putFloat("currentRiftFraction", currentRiftFraction);
        compound.putBoolean("isOpening", isOpening);
        compound.putFloat("fractionOpen", fractionOpen);
        compound.putBoolean("jarBroken", jarBroken);
        compound.putBoolean("suckingRifts", suckingRifts);
        compound.putFloat("fractionHandleUp", fractionHandleUp);
    }

    @Override
    protected void readNbt(CompoundTag compound) {
        currentRiftFraction = compound.getFloatOr("currentRiftFraction", 0);
        isOpening = compound.getBooleanOr("isOpening", false);
        fractionOpen = compound.getFloatOr("fractionOpen", 0);
        jarBroken = compound.getBooleanOr("jarBroken", false);
        suckingRifts = compound.getBooleanOr("suckingRifts", true);
        fractionHandleUp = compound.getFloatOr("fractionHandleUp", 0);
    }

    public static class JarRiftConnection {
        public final UUID riftID;
        public final Vec3 position;

        public Bezier bezier;
        public float fractionUp;

        public JarRiftConnection(RealityRiftEntity rift) {
            riftID = rift.getUUID();
            position = rift.getEyePosition();
        }
    }
}
