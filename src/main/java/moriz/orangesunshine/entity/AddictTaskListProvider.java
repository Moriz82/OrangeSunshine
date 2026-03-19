package moriz.orangesunshine.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import moriz.orangesunshine.PSDamageTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.SetLookAndInteract;
import net.minecraft.world.entity.ai.behavior.ShowTradesToPlayer;
import net.minecraft.world.entity.ai.behavior.UpdateActivityFromSchedule;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class AddictTaskListProvider {
    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> createWorkTasks(
            Pair<Integer, BehaviorControl<LivingEntity>> busyFollowTasks, float speed) {
        return ImmutableList.<Pair<Integer, ? extends BehaviorControl<? super Villager>>>of(
                busyFollowTasks,
                Pair.of(7, new WorkTask()),
                Pair.of(10, new ShowTradesToPlayer(400, 1600)),
                Pair.of(10, SetLookAndInteract.create(EntityType.PLAYER, 8)),
                Pair.of(11, goToPlayer(MemoryModuleType.NEAREST_PLAYERS, speed, 9)),
                Pair.of(99, UpdateActivityFromSchedule.create())
        );
    }

    public static BehaviorControl<Villager> goToPlayer(
            MemoryModuleType<List<Player>> playerMemory, float walkSpeed, int maxDistance) {
        return new GoToPlayerTask(playerMemory, walkSpeed, maxDistance);
    }

    public static float getShakeAmount(LivingEntity entity) {
        float healthScale = 1.0F - entity.getHealth() / entity.getMaxHealth();
        float shakeAmount = (float)(Math.cos(entity.tickCount * 3.25) * Math.PI * 0.4F * (1.0F + healthScale * 8.0F));
        entity.hurtTime = Math.abs(shakeAmount) > 5.0F ? 1 : 0;
        return shakeAmount;
    }

    static class GoToPlayerTask extends Behavior<Villager> {
        private final MemoryModuleType<List<Player>> playerMemory;
        private final float walkSpeed;
        private final int maxDistance;
        private long nextWalkTime;

        GoToPlayerTask(MemoryModuleType<List<Player>> playerMemory, float walkSpeed, int maxDistance) {
            super(ImmutableMap.of(
                    MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                    playerMemory, MemoryStatus.VALUE_PRESENT
            ), 1);
            this.playerMemory = playerMemory;
            this.walkSpeed = walkSpeed;
            this.maxDistance = maxDistance;
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel world, Villager entity) {
            return getNearbyPlayer(entity).isPresent();
        }

        @Override
        protected void start(ServerLevel world, Villager entity, long time) {
            if (time <= nextWalkTime || getNearbyPlayer(entity).isEmpty()) {
                return;
            }

            Vec3 targetPos = LandRandomPos.getPos(entity, 8, 6);
            if (targetPos != null) {
                entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(targetPos, walkSpeed, 1));
            } else {
                entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
            }
            nextWalkTime = time + 180L;
        }

        private Optional<Player> getNearbyPlayer(Villager entity) {
            return entity.getBrain().getMemory(playerMemory)
                    .filter(players -> !players.isEmpty())
                    .map(players -> players.get(0))
                    .filter(player -> player != null && player.position().closerThan(entity.position(), maxDistance));
        }
    }

    static class WorkTask extends Behavior<Villager> {
        private static final long RUN_TIME = 300L;
        private long lastCheckedTime;

        WorkTask() {
            super(ImmutableMap.of(), 1);
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel world, Villager entity) {
            if (world.getGameTime() - lastCheckedTime < RUN_TIME || world.getRandom().nextInt(2) != 0) {
                return false;
            }

            lastCheckedTime = world.getGameTime();
            return true;
        }

        @Override
        protected void start(ServerLevel world, Villager entity, long time) {
            if (entity.shouldRestock(world)) {
                entity.playWorkSound();
                entity.restock();
                entity.hurt(PSDamageTypes.create(world, PSDamageTypes.OVERDOSE), 1.0F + world.getRandom().nextFloat() * 5.0F);
            }
        }
    }
}
