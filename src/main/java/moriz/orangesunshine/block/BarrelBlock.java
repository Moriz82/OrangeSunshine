/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import java.util.Map;

import com.mojang.serialization.MapCodec;
import moriz.orangesunshine.fluid.FluidVolumes;
import moriz.orangesunshine.fluid.container.FluidContainer;
import moriz.orangesunshine.fluid.container.Resovoir;
import org.jetbrains.annotations.Nullable;
import moriz.orangesunshine.block.entity.BarrelBlockEntity;
import moriz.orangesunshine.block.entity.PSBlockEntities;
import moriz.orangesunshine.fluid.*;
import moriz.orangesunshine.screen.FluidContraptionScreenHandler;
import moriz.orangesunshine.screen.PSScreenHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BarrelBlock extends BlockWithFluid<BarrelBlockEntity> {
    public static final MapCodec<BarrelBlock> CODEC = simpleCodec(BarrelBlock::new);
    public static final int MAX_TAP_AMOUNT = FluidVolumes.BUCKET;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING_HOPPER;
    public static final BooleanProperty TAPPED = BooleanProperty.create("tapped");

    private static final Map<Direction.Axis, VoxelShape> STANDING_SHAPES = Map.of(
        Direction.Axis.X, Shapes.or(
            net.minecraft.world.level.block.Block.box(0, 5, 2, 16, 13, 14),
            net.minecraft.world.level.block.Block.box(0, 3, 4, 16, 15, 12)
        ),
        Direction.Axis.Y, Shapes.or(
            net.minecraft.world.level.block.Block.box(2, 0, 4, 14, 16, 12),
            net.minecraft.world.level.block.Block.box(4, 0, 2, 12, 16, 14)
        ),
        Direction.Axis.Z, Shapes.or(
            net.minecraft.world.level.block.Block.box(2, 5, 0, 14, 13, 16),
            net.minecraft.world.level.block.Block.box(4, 3, 0, 12, 15, 16)
        )
    );

    public BarrelBlock(BlockBehaviour.Properties settings) {
        super(settings.noOcclusion());
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(TAPPED, true));
    }

    @Override
    public MapCodec<? extends BarrelBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, TAPPED);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return STANDING_SHAPES.get(state.getValue(FACING).getAxis());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction side = ctx.getNearestLookingDirection().getOpposite();
        return defaultBlockState().setValue(FACING, side.getAxis() == Direction.Axis.Y ? Direction.DOWN : side);
    }

    @Override
    protected InteractionResult onInteract(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BarrelBlockEntity blockEntity) {
        if (!state.getValue(TAPPED) || state.getValue(FACING).getAxis() == Direction.Axis.Y) {
            return InteractionResult.FAIL;
        }

        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof FluidContainer container) {

            if (container.getLevel(stack) < container.getMaxCapacity(stack)) {

                Resovoir tank = blockEntity.getTank(Direction.DOWN);
                if (tank.getLevel() > 0 && tank.getFluidType().isSuitableContainer(container)) {
                    if (!world.isClientSide()) {
                        if (stack.getCount() > 1) {
                            player.getInventory().placeItemBackInInventory(tank.drain(MAX_TAP_AMOUNT, stack.split(1)));
                        } else {
                            player.setItemInHand(hand, tank.drain(MAX_TAP_AMOUNT, stack.split(1)));
                        }

                        blockEntity.timeLeftTapOpen = 20;
                        blockEntity.markForUpdate();
                        return InteractionResult.SUCCESS_SERVER;
                    }

                    return InteractionResult.SUCCESS;
                }
            }

            return InteractionResult.FAIL;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected BlockEntityType<BarrelBlockEntity> getBlockEntityType() {
        return PSBlockEntities.BARREL;
    }

    @Override
    protected MenuType<FluidContraptionScreenHandler<BarrelBlockEntity>> getScreenHandlerType() {
        return PSScreenHandlers.BARREL;
    }

    @Override
    @Nullable
    public <Q extends BlockEntity> BlockEntityTicker<Q> getTicker(Level world, BlockState state, BlockEntityType<Q> type) {
        return world.isClientSide() ? createTickerHelper(type, getBlockEntityType(), (w, p, s, entity) -> entity.tickAnimations()) : super.getTicker(world, state, type);
    }
}
