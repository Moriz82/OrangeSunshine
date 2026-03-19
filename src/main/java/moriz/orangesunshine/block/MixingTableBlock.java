package moriz.orangesunshine.block;

import com.mojang.serialization.MapCodec;
import moriz.orangesunshine.block.entity.MixingTableBlockEntity;
import moriz.orangesunshine.block.entity.PSBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MixingTableBlock extends BaseEntityBlock {
    public static final MapCodec<MixingTableBlock> CODEC = simpleCodec(MixingTableBlock::new);
    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 4, 12);

    public MixingTableBlock(Properties settings) {
        super(settings);
    }

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MixingTableBlockEntity(pos, state);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean moved) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MixingTableBlockEntity mixingTable) {
            Containers.dropContents(level, pos, mixingTable);
            Containers.updateNeighboursAfterDestroy(state, level, pos);
        }
        super.affectNeighborsAfterRemoval(state, level, pos, moved);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MixingTableBlockEntity mixingTable) {
            player.openMenu(mixingTable);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, PSBlockEntities.MIXING_TABLE_BLOCK_ENTITY, (tickLevel, pos, tickState, entity) -> entity.tick(tickLevel, pos, tickState));
    }
}
