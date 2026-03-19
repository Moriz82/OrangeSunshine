package moriz.orangesunshine.block;

import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class VineStemBlock extends FlowerBlock {
    public static final MapCodec<VineStemBlock> CODEC = RecordCodecBuilder.<VineStemBlock>mapCodec(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().<Supplier<Block>>xmap(block -> () -> block, Supplier::get).fieldOf("lattice").forGetter(b -> b.lattice),
            propertiesCodec()
    ).apply(instance, VineStemBlock::new));
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;

    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;
    public static final int MAX_AGE = 4;

    private final Supplier<Block> lattice;

    public VineStemBlock(Supplier<Block> lattice, BlockBehaviour.Properties settings) {
        super(MobEffects.MINING_FATIGUE, 5, settings);
        this.lattice = lattice;
        registerDefaultState(defaultBlockState().setValue(AGE, 0).setValue(NORTH, false).setValue(SOUTH, false).setValue(EAST, false).setValue(WEST, false));
    }

    @Override
    public MapCodec<? extends VineStemBlock> codec() {
        return CODEC;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {

        if (state.getValue(AGE) < MAX_AGE) {
            world.setBlock(pos, state.cycle(AGE), Block.UPDATE_ALL);
        }

        for (BlockPos mPos : BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 0, 1))) {
            BlockState s = world.getBlockState(mPos);
            if (s.is(PSBlocks.LATTICE)) {
                world.setBlock(mPos, LatticeBlock.copyStateProperties(lattice.get().defaultBlockState(), s), Block.UPDATE_ALL);
                return;
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = super.getStateForPlacement(ctx);
        if (state == null) {
            return null;
        }
        return state.setValue(NORTH, canConnect(world.getBlockState(pos.north()), Direction.SOUTH))
                .setValue(EAST, canConnect(world.getBlockState(pos.east()), Direction.WEST))
                .setValue(SOUTH, canConnect(world.getBlockState(pos.south()), Direction.NORTH))
                .setValue(WEST, canConnect(world.getBlockState(pos.west()), Direction.EAST));
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
        if (direction.getAxis().isHorizontal()) {
            return state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), canConnect(neighborState, direction.getOpposite()));
        }
        return super.updateShape(state, world, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    public boolean canConnect(BlockState state, Direction dir) {
        return state.is(PSBlocks.MORNING_GLORY_LATTICE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, EAST, WEST, SOUTH, AGE);
    }
}
