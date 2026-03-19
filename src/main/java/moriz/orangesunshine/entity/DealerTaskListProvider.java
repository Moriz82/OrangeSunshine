package moriz.orangesunshine.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.block.entity.DryingTableBlockEntity;
import moriz.orangesunshine.recipe.DryingRecipe;
import moriz.orangesunshine.recipe.PSRecipes;
import moriz.orangesunshine.recipe.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetLookAndInteract;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromBlockMemory;
import net.minecraft.world.entity.ai.behavior.ShowTradesToPlayer;
import net.minecraft.world.entity.ai.behavior.StrollAroundPoi;
import net.minecraft.world.entity.ai.behavior.StrollToPoi;
import net.minecraft.world.entity.ai.behavior.StrollToPoiList;
import net.minecraft.world.entity.ai.behavior.UpdateActivityFromSchedule;
import net.minecraft.world.entity.ai.behavior.UseBonemeal;
import net.minecraft.world.entity.ai.behavior.WorkAtPoi;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import org.jetbrains.annotations.Nullable;

public class DealerTaskListProvider {
    private static final ResourceKey<VillagerProfession> DRUG_DEALER_PROFESSION =
            ResourceKey.create(Registries.VILLAGER_PROFESSION, OrangeSunshine.id("drug_dealer"));
    private static final TagKey<Block> DRYING_TABLES =
            TagKey.create(Registries.BLOCK, OrangeSunshine.id("drying_tables"));
    private static final TagKey<Item> DRUG_CROP_SEEDS =
            TagKey.create(Registries.ITEM, OrangeSunshine.id("drug_crop_seeds"));

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> createWorkTasks(
            Pair<Integer, BehaviorControl<LivingEntity>> busyFollowTasks, float speed) {
        return ImmutableList.<Pair<Integer, ? extends BehaviorControl<? super Villager>>>of(
                busyFollowTasks,
                Pair.of(5, new RunOne<Villager>(ImmutableList.<Pair<? extends BehaviorControl<? super Villager>, Integer>>of(
                        Pair.of(new DealerWorkTask(), 7),
                        Pair.of(StrollAroundPoi.create(MemoryModuleType.JOB_SITE, 0.4F, 4), 2),
                        Pair.of(StrollToPoi.create(MemoryModuleType.JOB_SITE, 0.4F, 1, 10), 5),
                        Pair.of(StrollToPoiList.create(MemoryModuleType.SECONDARY_JOB_SITE, speed, 1, 6, MemoryModuleType.JOB_SITE), 5),
                        Pair.of(new DealerVillagerTask(), 2),
                        Pair.of(new UseBonemeal(), 4)
                ))),
                Pair.of(10, new ShowTradesToPlayer(400, 1600)),
                Pair.of(10, SetLookAndInteract.create(EntityType.PLAYER, 4)),
                Pair.of(2, SetWalkTargetFromBlockMemory.create(MemoryModuleType.JOB_SITE, speed, 9, 100, 1200)),
                Pair.of(99, UpdateActivityFromSchedule.create())
        );
    }

    static class DealerWorkTask extends WorkAtPoi {
        private static final int OUTPUT_SLOT = 0;

        @Override
        protected void useWorkstation(ServerLevel world, Villager entity) {
            Optional<GlobalPos> jobSite = entity.getBrain().getMemory(MemoryModuleType.JOB_SITE);
            if (jobSite.isEmpty()) {
                return;
            }

            BlockPos jobSitePos = jobSite.get().pos();
            BlockState blockState = world.getBlockState(jobSitePos);
            if (!blockState.is(DRYING_TABLES) || !(world.getBlockEntity(jobSitePos) instanceof DryingTableBlockEntity blockEntity)) {
                return;
            }

            int[] inputSlots = blockEntity.getAvailableSlots(Direction.DOWN);
            ItemStack output = blockEntity.getStack(OUTPUT_SLOT);
            if (!output.isEmpty()) {
                blockEntity.clear();
                ItemStack remainder = entity.getInventory().addItem(output);
                if (!remainder.isEmpty()) {
                    entity.spawnAtLocation(world, remainder);
                }
            }

            findDryingRecipe(world, entity, inputSlots.length).ifPresent(recipe ->
                    moveRecipeIngredients(entity, blockEntity, inputSlots, recipe.value().getInput()));
        }

        private static Optional<RecipeHolder<DryingRecipe>> findDryingRecipe(ServerLevel world, Villager entity, int requiredSlots) {
            return world.recipeAccess().getRecipes().stream()
                    .filter(recipe -> recipe.value().getType() == PSRecipes.DRYING_TYPE)
                    .map(DealerWorkTask::castDryingRecipe)
                    .filter(recipe -> countMatchingIngredientSlots(entity.getInventory(), recipe.value().getInput()) >= requiredSlots)
                    .findFirst();
        }

        private static int countMatchingIngredientSlots(net.minecraft.world.SimpleContainer inventory, Ingredient ingredient) {
            int matchingSlots = 0;
            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                ItemStack stack = inventory.getItem(slot);
                if (!stack.isEmpty() && ingredient.test(stack)) {
                    matchingSlots++;
                }
            }
            return matchingSlots;
        }

        private static void moveRecipeIngredients(Villager entity, DryingTableBlockEntity blockEntity, int[] inputSlots, Ingredient ingredient) {
            List<ItemStack> consumedMaterials = new ArrayList<>(inputSlots.length);
            for (int slot = 0; slot < entity.getInventory().getContainerSize() && consumedMaterials.size() < inputSlots.length; slot++) {
                ItemStack stack = entity.getInventory().getItem(slot);
                if (stack.isEmpty() || !ingredient.test(stack)) {
                    continue;
                }

                consumedMaterials.add(stack);
                entity.getInventory().setItem(slot, ItemStack.EMPTY);
            }

            for (int i = 0; i < inputSlots.length && i < consumedMaterials.size(); i++) {
                blockEntity.setStack(inputSlots[i], consumedMaterials.get(i));
            }
        }

        @SuppressWarnings("unchecked")
        private static RecipeHolder<DryingRecipe> castDryingRecipe(RecipeHolder<?> recipe) {
            return (RecipeHolder<DryingRecipe>)recipe;
        }
    }

    static class DealerVillagerTask extends Behavior<Villager> {
        private static final int MAX_RUN_TIME = 200;
        private static final float WALK_SPEED = 0.5F;

        @Nullable
        private BlockPos currentTarget;
        private long nextResponseTime;
        private int ticksRan;
        private final List<BlockPos> targetPositions = new ArrayList<>();

        DealerVillagerTask() {
            super(ImmutableMap.of(
                    MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT,
                    MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                    MemoryModuleType.SECONDARY_JOB_SITE, MemoryStatus.VALUE_PRESENT
            ), MAX_RUN_TIME);
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel world, Villager entity) {
            if (!world.getGameRules().get(GameRules.MOB_GRIEFING)) {
                return false;
            }

            if (!entity.getVillagerData().profession().is(DRUG_DEALER_PROFESSION)) {
                return false;
            }

            BlockPos.MutableBlockPos mutable = entity.blockPosition().mutable();
            targetPositions.clear();

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        mutable.set(entity.getX() + x, entity.getY() + y, entity.getZ() + z);
                        if (isSuitableTarget(mutable, world)) {
                            targetPositions.add(mutable.immutable());
                        }
                    }
                }
            }

            currentTarget = chooseRandomTarget(world);
            return currentTarget != null;
        }

        @Override
        protected void start(ServerLevel world, Villager entity, long time) {
            if (time > nextResponseTime && currentTarget != null) {
                setTargetMemories(entity, currentTarget);
            }
        }

        @Override
        protected void tick(ServerLevel world, Villager entity, long time) {
            if (currentTarget != null && !currentTarget.closerToCenterThan(entity.position(), 1.0)) {
                return;
            }

            if (currentTarget != null && time > nextResponseTime) {
                BlockState state = world.getBlockState(currentTarget);

                if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
                    world.destroyBlock(currentTarget, true, entity);
                }

                if (state.isAir() && world.getBlockState(currentTarget.below()).getBlock() instanceof FarmBlock) {
                    RecipeUtils.consume(entity.getInventory(), stack -> stack.getItem() instanceof BlockItem && stack.is(DRUG_CROP_SEEDS)).ifPresent(stack -> {
                        BlockState plantedState = ((BlockItem)stack.getItem()).getBlock().defaultBlockState();
                        world.setBlockAndUpdate(currentTarget, plantedState);
                        world.gameEvent(GameEvent.BLOCK_PLACE, currentTarget, GameEvent.Context.of(entity, plantedState));
                        world.playSound(null,
                                currentTarget.getX(),
                                currentTarget.getY(),
                                currentTarget.getZ(),
                                SoundEvents.CROP_PLANTED,
                                SoundSource.BLOCKS,
                                1.0F,
                                1.0F);
                    });
                }

                if (state.getBlock() instanceof CropBlock crop && !crop.isMaxAge(state)) {
                    targetPositions.remove(currentTarget);
                    currentTarget = chooseRandomTarget(world);
                    if (currentTarget != null) {
                        nextResponseTime = time + 20L;
                        setTargetMemories(entity, currentTarget);
                    }
                }
            }

            ticksRan++;
        }

        @Override
        protected void stop(ServerLevel world, Villager entity, long time) {
            entity.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
            entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
            ticksRan = 0;
            nextResponseTime = time + 40L;
        }

        @Override
        protected boolean canStillUse(ServerLevel world, Villager entity, long time) {
            return ticksRan < MAX_RUN_TIME;
        }

        @Nullable
        private BlockPos chooseRandomTarget(ServerLevel world) {
            return targetPositions.isEmpty() ? null : targetPositions.get(world.getRandom().nextInt(targetPositions.size()));
        }

        private boolean isSuitableTarget(BlockPos pos, ServerLevel world) {
            BlockState blockState = world.getBlockState(pos);
            return blockState.getBlock() instanceof CropBlock crop && crop.isMaxAge(blockState)
                    || blockState.isAir() && world.getBlockState(pos.below()).getBlock() instanceof FarmBlock;
        }

        private static void setTargetMemories(Villager entity, BlockPos target) {
            entity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(target));
            entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(target), WALK_SPEED, 1));
        }
    }
}
