/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class LatticeBlock extends CrossCollisionBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<LatticeBlock> CODEC = simpleCodec(LatticeBlock::new);
    private static final Set<Direction> X_DIRECTIONS = Set.of(Direction.EAST, Direction.WEST);
    private static final Set<Direction> Z_DIRECTIONS = Set.of(Direction.NORTH, Direction.SOUTH);

    private static final Map<Direction.Axis, Set<Direction>> DEFAULT_CONNECTIONS = Map.of(
            Direction.Axis.X, X_DIRECTIONS,
            Direction.Axis.Z, Z_DIRECTIONS
    );

    public LatticeBlock(BlockBehaviour.Properties settings) {
        super(1.6F, 1.6F, 16, 16, 16, settings);
        registerDefaultState(defaultBlockState()
                .setValue(EAST, true)
                .setValue(WEST, true)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public MapCodec<? extends LatticeBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, WATERLOGGED);
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        if (direction.getAxis().isHorizontal()) {
            Set<Direction> connections = getPossibleConnections(world, pos);

            if (connections.isEmpty()) {
                connections = (state.getValue(NORTH) || state.getValue(SOUTH)) ? Z_DIRECTIONS : X_DIRECTIONS;
            }

            return applyConnections(state, connections);
        }
        return super.updateShape(state, world, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState placedAgainst = world.getBlockState(pos.relative(ctx.getClickedFace().getOpposite()));

        Set<Direction> connections;

        if (ctx.getClickedFace().getAxis() == Direction.Axis.Y && placedAgainst.getBlock() instanceof LatticeBlock) {
            connections = getConnections(placedAgainst).collect(Collectors.toSet());
        } else {
            connections = getPossibleConnections(world, pos);
        }

        if (connections.isEmpty()) {
            connections = DEFAULT_CONNECTIONS.getOrDefault(ctx.getHorizontalDirection().getClockWise().getAxis(), Set.of());
        }

        BlockState placedState = super.getStateForPlacement(ctx);
        if (placedState == null) {
            return null;
        }

        return applyConnections(placedState, connections)
                .setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
    }

    private Set<Direction> getPossibleConnections(BlockGetter world, BlockPos pos) {
        return PROPERTY_BY_DIRECTION.keySet().stream()
                .filter(connection -> canConnect(world, pos, connection))
                .collect(Collectors.toSet());
    }

    private boolean canConnect(BlockGetter world, BlockPos pos, Direction connectionDirection) {
        Direction neighbourDirection = connectionDirection.getOpposite();
        BlockPos neighbourPos = pos.relative(connectionDirection);
        BlockState neighbourState = world.getBlockState(neighbourPos);

        if (neighbourState.isAir()) {
            return false;
        }

        if (isLattice(neighbourState)) {
            return true;
        }

        BlockPos oppositePos = pos.relative(neighbourDirection);
        BlockState oppositeState = world.getBlockState(oppositePos);

        boolean neighbour = canConnectTo(world, neighbourPos, neighbourState, neighbourDirection);
        boolean opposite = canConnectTo(world, oppositePos, oppositeState, connectionDirection);

        return neighbour && opposite;
    }

    protected boolean canConnectTo(BlockGetter world, BlockPos pos, BlockState state, Direction direction) {
        return isLattice(state)
                || (!Block.isExceptionForConnection(state) && state.isFaceSturdy(world, pos, direction))
                || (state.getBlock() instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, direction));
    }

    private boolean isLattice(BlockState state) {
        return state.getBlock() instanceof LatticeBlock;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        if (state.getValue(WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(state);
    }

    public static BlockState copyStateProperties(BlockState newState, BlockState state) {
        for (var connector : PROPERTY_BY_DIRECTION.entrySet()) {
            newState = newState.setValue(connector.getValue(), state.getValue(connector.getValue()));
        }
        return newState.setValue(WATERLOGGED, state.getValue(WATERLOGGED));
    }

    public static BlockState applyConnections(BlockState state, Set<Direction> connections) {
        for (var connector : PROPERTY_BY_DIRECTION.entrySet()) {
            state = state.setValue(connector.getValue(), connections.contains(connector.getKey()));
        }
        return state;
    }

    public static Stream<Direction> getConnections(BlockState state) {
        return PROPERTY_BY_DIRECTION.entrySet().stream()
                .filter(connector -> state.getValue(connector.getValue()))
                .map(Map.Entry::getKey);
    }

    public static Stream<Direction> getFreeConnections(BlockState state) {
        return PROPERTY_BY_DIRECTION.entrySet().stream()
                .filter(connector -> !state.getValue(connector.getValue()))
                .map(Map.Entry::getKey);
    }
}
