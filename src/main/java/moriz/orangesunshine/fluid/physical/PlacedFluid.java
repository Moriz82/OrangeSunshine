package moriz.orangesunshine.fluid.physical;

import java.util.stream.Stream;

import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.item.PSItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.server.level.ServerLevel;

public abstract class PlacedFluid extends WaterFluid {
    private static final Direction[] ALL_DIRECTIONS = Direction.values();

    protected abstract PhysicalFluid getPysicalFluid();

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
        super.createFluidStateDefinition(builder);
        getType().getStateManager().appendProperties(builder);
    }

    public SimpleFluid getType() {
        return getPysicalFluid().getType();
    }

    @Override
    public Fluid getFlowing() {
        return getPysicalFluid().getFlowingFluid();
    }

    @Override
    public Fluid getSource() {
        return getPysicalFluid().getStandingFluid();
    }

    @Override
    public Item getBucket() {
        return getType().isEmpty() ? Items.BUCKET : PSItems.FILLED_BUCKET;
    }

    @Override
    public BlockState createLegacyBlock(FluidState state) {
        return getType().getStateManager().copyStateValues(state, getPysicalFluid().getBlock().defaultBlockState()
                .trySetValue(LiquidBlock.LEVEL, getLegacyLevel(state))
        );
    }

    @Override
    public int getDropOff(LevelReader world) {
        return super.getDropOff(world);
    }

    @Override
    public int getSlopeFindDistance(LevelReader world) {
        return super.getSlopeFindDistance(world);
    }

    @Override
    public int getTickDelay(LevelReader world) {
        return super.getTickDelay(world);
    }

    @Override
    public int getAmount(FluidState state) {
        return state.getValue(LEVEL);
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == getSource() || fluid == getFlowing();
    }

    @Override
    public void animateTick(Level world, BlockPos pos, FluidState state, RandomSource random) {
        super.animateTick(world, pos, state, random);
        getType().randomDisplayTick(world, pos, state, random);
    }

    @Override
    protected void randomTick(ServerLevel world, BlockPos pos, FluidState state, RandomSource random) {
        super.randomTick(world, pos, state, random);
        getType().onRandomTick(world, pos, state, random);
    }

    @Override
    protected FluidState getNewLiquid(ServerLevel world, BlockPos pos, BlockState state) {
        return getType().getStateManager().computeAverage(Stream.of(ALL_DIRECTIONS)
                .map(direction -> world.getBlockState(pos.relative(direction)).getFluidState())
                .filter(neighbourState -> neighbourState.getType().isSame(this)),
                super.getNewLiquid(world, pos, state)
        );
    }

    static PlacedFluid still(PhysicalFluid physical) {
        return new PlacedFluid() {
            @Override
            public boolean isSource(FluidState state) {
                return true;
            }

            @Override
            public int getAmount(FluidState state) {
                return 8;
            }

            @Override
            protected boolean canConvertToSource(ServerLevel world) {
                return true;
            }

            @Override
            protected PhysicalFluid getPysicalFluid() {
                return physical;
            }
        };
    }

    static PlacedFluid flowing(PhysicalFluid physical) {
        return new PlacedFluid() {
            @Override
            protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
                super.createFluidStateDefinition(builder);
                builder.add(LEVEL);
            }

            @Override
            public boolean isSource(FluidState state) {
                return false;
            }

            @Override
            public int getAmount(FluidState state) {
                return state.getValue(LEVEL);
            }

            @Override
            protected boolean canConvertToSource(ServerLevel world) {
                return true;
            }

            @Override
            protected PhysicalFluid getPysicalFluid() {
                return physical;
            }
        };
    }
}
