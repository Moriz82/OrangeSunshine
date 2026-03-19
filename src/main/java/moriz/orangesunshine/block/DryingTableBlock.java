/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import moriz.orangesunshine.screen.DryingTableScreenHandler;
import org.jetbrains.annotations.Nullable;
import com.mojang.serialization.MapCodec;
import moriz.orangesunshine.block.entity.DryingTableBlockEntity;
import moriz.orangesunshine.block.entity.PSBlockEntities;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DryingTableBlock extends BaseEntityBlock implements EntityBlock {
    public static final MapCodec<DryingTableBlock> CODEC = simpleCodec(DryingTableBlock::new);
    private static final VoxelShape SHAPE = net.minecraft.world.level.block.Block.box(0, 0, 0, 16, 12, 16);

    public DryingTableBlock(BlockBehaviour.Properties settings) {
        super(settings.noOcclusion());
    }

    @Override
    public MapCodec<? extends DryingTableBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return openMenu(world, pos, player);
    }

    @Override
    protected InteractionResult useItemOn(net.minecraft.world.item.ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return openMenu(world, pos, player);
    }

    private InteractionResult openMenu(Level world, BlockPos pos, Player player) {
        return world.getBlockEntity(pos, PSBlockEntities.DRYING_TABLE).<InteractionResult>map(be -> {
            if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(new ExtendedScreenHandlerFactory<BlockPos>() {
                    @Override
                    public Component getDisplayName() {
                        return DryingTableBlock.this.getName();
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player openingPlayer) {
                        return new DryingTableScreenHandler(syncId, inv, be);
                    }

                    @Override
                    public BlockPos getScreenOpeningData(ServerPlayer player) {
                        return pos;
                    }
                });
                return InteractionResult.SUCCESS_SERVER;
            }
            return InteractionResult.SUCCESS;
        }).orElse(InteractionResult.FAIL);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide() ? null : createTickerHelper(type, PSBlockEntities.DRYING_TABLE, (w, p, s, entity) -> entity.tick((ServerLevel)w));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DryingTableBlockEntity(pos, state);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return world.getBlockEntity(pos, PSBlockEntities.DRYING_TABLE)
                .map(be -> (int)(be.getHeatRatio() * 15))
                .orElse(0);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        return world.getBlockEntity(pos, PSBlockEntities.DRYING_TABLE)
                .map(be -> (int)(be.getDryingProgress() * 15))
                .orElse(0);
    }
}
