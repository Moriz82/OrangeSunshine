package moriz.orangesunshine.fluid.physical;

import moriz.orangesunshine.item.PSItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;

public abstract class PlacedFluidBlock extends LiquidBlock {
    protected abstract PhysicalFluid getPysicalFluid();

    static PlacedFluidBlock create(PhysicalFluid physical, ResourceKey<Block> id) {
        return new PlacedFluidBlock((FlowingFluid)physical.getFlowingFluid(), id) {
            @Override
            protected PhysicalFluid getPysicalFluid() {
                return physical;
            }
        };
    }

    PlacedFluidBlock(FlowingFluid fluid, ResourceKey<Block> id) {
        super(fluid, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).setId(id));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        getPysicalFluid().getType().getStateManager().appendProperties(builder);
    }

    @Override
    public ItemStack pickupBlock(LivingEntity player, LevelAccessor world, BlockPos pos, BlockState state) {
        if (state.getValue(LEVEL) == 0) {
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            return getPysicalFluid().getType().getStack(state.getFluidState(), PSItems.FILLED_BUCKET);
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return getPysicalFluid().getType().getStateManager().copyStateValues(state, super.getFluidState(state));
    }
}
