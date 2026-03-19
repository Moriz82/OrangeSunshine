/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import com.mojang.serialization.MapCodec;
import moriz.orangesunshine.PSTags;
import moriz.orangesunshine.block.entity.DistilleryBlockEntity;
import moriz.orangesunshine.block.entity.PSBlockEntities;
import moriz.orangesunshine.screen.FluidContraptionScreenHandler;
import moriz.orangesunshine.screen.PSScreenHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Created by lukas on 25.10.14.
 */
public class DistilleryBlock extends BlockWithFluid<DistilleryBlockEntity> {
    public static final MapCodec<DistilleryBlock> CODEC = simpleCodec(DistilleryBlock::new);
    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(5, 0, 5, 11, 6, 11),
        Block.box(4, 0, 6, 12, 5, 10),
        Block.box(6, 0, 4, 10, 5, 12),
        Block.box(6, 6, 6, 10, 14, 10),
        Block.box(5, 9, 6, 11, 13, 10),
        Block.box(6, 9, 5, 10, 13, 11)
    );
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public DistilleryBlock(BlockBehaviour.Properties settings) {
        super(settings.noOcclusion());
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP));
    }

    @Override
    public MapCodec<? extends DistilleryBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
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
        return defaultBlockState().setValue(FACING, findConnection(ctx.getLevel(), ctx.getClickedPos()).orElse(Direction.UP));
    }

    public static boolean canConnectTo(BlockState state, Direction direction) {
        return state.is(PSTags.BARRELS)
            || state.is(PSBlocks.MASH_TUB)
            || state.is(PSBlocks.FLASK)
            || (state.is(PSBlocks.DISTILLERY) && state.getValue(FACING) != direction.getOpposite());
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess scheduledTicks, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(FACING).getAxis() == Direction.Axis.Y || state.getValue(FACING) == direction) {
            return state.setValue(FACING, findConnection(world, pos).orElse(Direction.UP));
        }

        return super.updateShape(state, world, scheduledTicks, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected BlockEntityType<DistilleryBlockEntity> getBlockEntityType() {
        return PSBlockEntities.DISTILLERY;
    }

    @Override
    protected MenuType<FluidContraptionScreenHandler<DistilleryBlockEntity>> getScreenHandlerType() {
        return PSScreenHandlers.DISTILLERY;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    private static java.util.Optional<Direction> findConnection(LevelReader world, BlockPos pos) {
        for (Direction direction : new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
            if (canConnectTo(world.getBlockState(pos.relative(direction)), direction)) {
                return java.util.Optional.of(direction);
            }
        }
        return java.util.Optional.empty();
    }
}
