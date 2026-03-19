/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import java.util.List;

import moriz.orangesunshine.block.entity.FlaskBlockEntity;
import moriz.orangesunshine.fluid.container.FluidContainer;
import moriz.orangesunshine.fluid.container.Resovoir;
import moriz.orangesunshine.screen.FluidContraptionScreenHandler;
import moriz.orangesunshine.screen.PSScreenHandlers;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * @author Sollace
 * @since 3 Jan 2023
 */
public abstract class BlockWithFluid<T extends FlaskBlockEntity> extends BaseEntityBlock implements EntityBlock {
    public static final Identifier CONTENTS_DYNAMIC_DROP_ID = Identifier.withDefaultNamespace("contents");

    protected BlockWithFluid(BlockBehaviour.Properties settings) {
        super(settings);
    }

    protected abstract BlockEntityType<T> getBlockEntityType();

    protected abstract MenuType<FluidContraptionScreenHandler<T>> getScreenHandlerType();

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide() && FluidContainer.of(stack, null) != null) {
            level.getBlockEntity(pos, getBlockEntityType()).ifPresent(be -> be.getTank(Direction.UP).deposit(stack));
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof DirectionalFluidResovoir container && blockEntity.getType() == getBlockEntityType()) {
            builder = builder.withDynamicDrop(CONTENTS_DYNAMIC_DROP_ID, lootConsumer -> {
                List<ItemStack> dynamicStacks = container.getDroppedStacks(FluidContainer.of(asItem()));
                if (dynamicStacks.isEmpty()) {
                    lootConsumer.accept(asItem().getDefaultInstance());
                } else {
                    dynamicStacks.forEach(lootConsumer);
                }
            });
        }
        return super.getDrops(state, builder);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return interact(state, level, pos, player, InteractionHand.MAIN_HAND, hit);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return interact(state, level, pos, player, hand, hit);
    }

    private InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return level.getBlockEntity(pos, getBlockEntityType()).map(be -> {
            InteractionResult result = onInteract(be.getBlockState(), level, be.getBlockPos(), player, hand, be);
            if (result != InteractionResult.PASS) {
                return result;
            }

            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(new ExtendedScreenHandlerFactory<PSScreenHandlers.BlockSideData>() {
                    @Override
                    public Component getDisplayName() {
                        return BlockWithFluid.this.getName();
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player openingPlayer) {
                        return new FluidContraptionScreenHandler<>(getScreenHandlerType(), syncId, inventory, be, hit.getDirection());
                    }

                    @Override
                    public PSScreenHandlers.BlockSideData getScreenOpeningData(ServerPlayer player) {
                        return new PSScreenHandlers.BlockSideData(be.getBlockPos(), hit.getDirection());
                    }
                });
                return InteractionResult.SUCCESS_SERVER;
            }

            return InteractionResult.SUCCESS;
        }).orElse(InteractionResult.FAIL);
    }

    protected InteractionResult onInteract(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, T blockEntity) {
        return InteractionResult.PASS;
    }

    @Override
    @Nullable
    public <Q extends BlockEntity> BlockEntityTicker<Q> getTicker(Level level, BlockState state, BlockEntityType<Q> type) {
        return level.isClientSide() ? null : createTickerHelper(type, getBlockEntityType(), (world, pos, blockState, entity) -> entity.tick((ServerLevel)world));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return getBlockEntityType().create(pos, state);
    }

    public interface DirectionalFluidResovoir {
        Resovoir getTank(Direction direction);

        List<ItemStack> getDroppedStacks(FluidContainer container);

        void tick(ServerLevel level);
    }
}
