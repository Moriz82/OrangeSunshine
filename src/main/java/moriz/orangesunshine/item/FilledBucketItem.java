/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import java.util.function.Consumer;

import moriz.orangesunshine.fluid.FluidVolumes;
import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.fluid.container.FluidContainer;
import org.jetbrains.annotations.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Created by Sollace on Feb 6 2023
 */
public class FilledBucketItem extends Item implements FluidContainer {

    public FilledBucketItem(Item.Properties settings) {
        super(settings.craftRemainder(Items.BUCKET));
        DispenserBlock.registerBehavior(this, new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource pointer, ItemStack stack) {
                BlockPos blockPos = pointer.pos().relative(pointer.state().getValue(DispenserBlock.FACING));
                ServerLevel level = pointer.level();
                if (FilledBucketItem.this.placeFluid(getFluid(stack).getFluidState(stack), null, level, blockPos, null)) {
                    return consumeWithRemainder(pointer, stack, new ItemStack(FilledBucketItem.this.asEmpty()));
                }
                return super.execute(pointer, stack);
            }
        });
    }

    @Override
    public int getMaxCapacity() {
        return FluidVolumes.BUCKET;
    }

    @Override
    public Item asEmpty() {
        return Items.BUCKET;
    }

    @Override
    public Component getName(ItemStack stack) {
        SimpleFluid fluid = getFluid(stack);

        if (!fluid.isEmpty()) {
            return Component.empty()
                    .append(fluid.getName(stack))
                    .append(Component.literal(" "))
                    .append(Items.BUCKET.getName(stack));
        }

        return Items.BUCKET.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
        if (flag.isAdvanced()) {
            consumer.accept(Component.literal(getLevel(stack) + "/" + getMaxCapacity(stack)));
        }
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);

        FluidState fluid = getFluid(stack).getFluidState(stack);

        BlockHitResult hit = getPlayerPOVHitResult(level, user, fluid.isEmpty()
                ? ClipContext.Fluid.SOURCE_ONLY
                : ClipContext.Fluid.NONE);

        if (hit.getType() == HitResult.Type.MISS) {
            return InteractionResult.PASS;
        }

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = hit.getBlockPos();
            Direction direction = hit.getDirection();
            BlockPos blockPos2 = blockPos.relative(direction);
            if (!level.mayInteract(user, blockPos) || !user.mayUseItemAt(blockPos2, direction, stack)) {
                return InteractionResult.FAIL;
            }

            if (fluid.isEmpty()) {
                BlockState state = level.getBlockState(blockPos);
                Block block = state.getBlock();
                if (block instanceof BucketPickup pickup) {
                    ItemStack filledStack = pickup.pickupBlock(user, level, blockPos, state);
                    if (!filledStack.isEmpty()) {
                        user.awardStat(Stats.ITEM_USED.get(this));
                        pickup.getPickupSound().ifPresent(sound -> user.playSound(sound, 1.0f, 1.0f));
                        level.gameEvent(user, GameEvent.FLUID_PICKUP, blockPos);
                        ItemStack result = ItemUtils.createFilledResult(stack, user, filledStack);
                        if (!level.isClientSide() && user instanceof ServerPlayer serverPlayer) {
                            CriteriaTriggers.FILLED_BUCKET.trigger(serverPlayer, filledStack);
                        }
                        return InteractionResult.SUCCESS.heldItemTransformedTo(result);
                    }
                }
                return InteractionResult.FAIL;
            }
            BlockState blockState = level.getBlockState(blockPos);
            BlockPos blockPos3 = blockState.getBlock() instanceof LiquidBlockContainer && fluid.is(Fluids.WATER) ? blockPos : blockPos2;
            if (placeFluid(fluid, user, level, blockPos3, hit)) {
                if (user instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, blockPos3, stack);
                }
                user.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResult.SUCCESS.heldItemTransformedTo(ItemUtils.createFilledResult(stack, user, new ItemStack(asEmpty())));
            }

            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    public boolean placeFluid(FluidState fluid, @Nullable LivingEntity user, Level level, BlockPos pos, @Nullable BlockHitResult hit) {
        if (!(fluid.getType() instanceof FlowingFluid)) {
            return false;
        }

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        boolean canPlace = state.canBeReplaced(fluid.getType());

        if (!(state.isAir() || canPlace || block instanceof LiquidBlockContainer container && container.canPlaceLiquid(user, level, pos, state, fluid.getType()))) {
            return hit != null && placeFluid(fluid, user, level, hit.getBlockPos().relative(hit.getDirection()), null);
        }

        if (level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos) && !fluid.is(FluidTags.LAVA)) {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            level.playSound(user, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
            for (int l = 0; l < 8; ++l) {
                level.addParticle(ParticleTypes.LARGE_SMOKE, i + level.random.nextFloat(), j + level.random.nextFloat(), k + level.random.nextFloat(), 0.0, 0.0, 0.0);
            }
            return true;
        }

        if (block instanceof LiquidBlockContainer container && fluid.is(Fluids.WATER)) {
            container.placeLiquid(level, pos, state, fluid);
            playEmptyingSound(fluid.getType(), user, level, pos);
            return true;
        }

        if (!level.isClientSide() && canPlace && !state.liquid()) {
            level.destroyBlock(pos, true);
        }

        if (level.setBlock(pos, fluid.createLegacyBlock(), Block.UPDATE_ALL_IMMEDIATE) || state.getFluidState().isSource()) {
            playEmptyingSound(fluid.getType(), user, level, pos);
            return true;
        }
        return false;
    }

    protected void playEmptyingSound(Fluid fluid, @Nullable LivingEntity user, LevelAccessor level, BlockPos pos) {
        SoundEvent soundEvent = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        level.playSound(user, pos, soundEvent, SoundSource.BLOCKS, 1, 1);
        level.gameEvent(user, GameEvent.FLUID_PLACE, pos);
    }
}
