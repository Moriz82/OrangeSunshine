package moriz.orangesunshine.block;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mojang.serialization.MapCodec;
import moriz.orangesunshine.block.entity.BottleRackBlockEntity;
import moriz.orangesunshine.block.entity.PSBlockEntities;
import moriz.orangesunshine.client.render.blocks.VoxelShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Created by lukas on 16.11.14.
 */
public class BottleRackBlock extends net.minecraft.world.level.block.BaseEntityBlock implements EntityBlock {
    public static final MapCodec<BottleRackBlock> CODEC = simpleCodec(BottleRackBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final Map<Direction, VoxelShape> SHAPES = Arrays.stream(Direction.values())
            .filter(d -> d.getAxis() != Direction.Axis.Y)
            .collect(Collectors.toMap(
                    Function.identity(),
                    VoxelShapeUtil.rotator(Shapes.or(
                            Block.box(0, 0, 3, 1, 16, 11),
                            Block.box(5, 0, 3, 6, 16, 11),
                            Block.box(10, 0, 3, 11, 16, 11),
                            Block.box(15, 0, 3, 16, 16, 11),

                            Block.box(1, 0, 2.75F, 15, 1, 12.35F),
                            Block.box(1, 5, 2.75F, 15, 6, 12.35F),
                            Block.box(1, 10, 2.75F, 15, 11, 12.35F),
                            Block.box(1, 15, 2.75F, 15, 16, 12.35F)
                    )))
            );

    public BottleRackBlock(BlockBehaviour.Properties settings) {
        super(settings.noOcclusion());
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public MapCodec<? extends BottleRackBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES.getOrDefault(state.getValue(FACING), Shapes.block());
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
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return world.getBlockEntity(pos, PSBlockEntities.BOTTLE_RACK).map(be -> {
            ItemStack extracted = be.extractItem(hit, state.getValue(FACING));
            if (!extracted.isEmpty()) {
                if (!player.addItem(extracted)) {
                    player.drop(extracted, false);
                }
                return world.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }
            return InteractionResult.FAIL;
        }).orElse(InteractionResult.FAIL);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return world.getBlockEntity(pos, PSBlockEntities.BOTTLE_RACK)
                .map(be -> be.insertItem(stack, hit, state.getValue(FACING)))
                .orElse(InteractionResult.FAIL);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        world.getBlockEntity(pos, PSBlockEntities.BOTTLE_RACK).ifPresent(be -> {
            Containers.dropContents(world, pos, be);
            world.updateNeighbourForOutputSignal(pos, this);
        });
        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BottleRackBlockEntity(pos, state);
    }
}
