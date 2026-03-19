/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import java.util.List;

import moriz.orangesunshine.PSSounds;
import moriz.orangesunshine.item.PSItems;
import moriz.orangesunshine.item.RiftJarItem;
import org.jetbrains.annotations.Nullable;
import com.mojang.serialization.MapCodec;
import moriz.orangesunshine.block.entity.PSBlockEntities;
import moriz.orangesunshine.block.entity.RiftJarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

class RiftJarBlock extends BaseEntityBlock implements EntityBlock {
    public static final MapCodec<RiftJarBlock> CODEC = simpleCodec(RiftJarBlock::new);
    private static final VoxelShape SHAPE = Shapes.or(
            net.minecraft.world.level.block.Block.box(4, 0, 4, 12, 5, 12),
            net.minecraft.world.level.block.Block.box(4.5, 5, 4.5, 11.5, 7, 11.5),
            net.minecraft.world.level.block.Block.box(4, 7, 4, 12, 12, 12),
            net.minecraft.world.level.block.Block.box(5, 12, 5, 11, 14, 11)
    );
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    public RiftJarBlock(BlockBehaviour.Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public MapCodec<? extends RiftJarBlock> codec() {
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
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return interact(world, pos, player);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return interact(world, pos, player);
    }

    private InteractionResult interact(Level world, BlockPos pos, Player player) {
        return world.getBlockEntity(pos, PSBlockEntities.RIFT_JAR).<InteractionResult>map(be -> {
            if (!world.isClientSide()) {
                if (player.isShiftKeyDown()) {
                    be.toggleSuckingRifts();
                    world.playSound(null, pos, PSSounds.BLOCK_RIFT_JAR_TOGGLE, SoundSource.BLOCKS, 1, 1);
                } else {
                    world.playSound(null, pos, be.toggleRiftJarOpen() ? PSSounds.BLOCK_RIFT_JAR_OPEN : PSSounds.BLOCK_RIFT_JAR_CLOSE, SoundSource.BLOCKS, 1, 1);
                }
                return InteractionResult.SUCCESS_SERVER;
            }
            return InteractionResult.SUCCESS;
        }).orElse(InteractionResult.FAIL);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof RiftJarBlockEntity be && !be.jarBroken) {
            return List.of(RiftJarItem.createFilledRiftJar(be.currentRiftFraction, PSItems.RIFT_JAR));
        }
        return List.of();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RiftJarBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <Q extends BlockEntity> BlockEntityTicker<Q> getTicker(Level world, BlockState state, BlockEntityType<Q> type) {
        return world.isClientSide()
                ? createTickerHelper(type, PSBlockEntities.RIFT_JAR, (w, p, s, entity) -> entity.tickAnimation())
                : createTickerHelper(type, PSBlockEntities.RIFT_JAR, (w, p, s, entity) -> entity.tick((ServerLevel)w));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }
}
