package moriz.orangesunshine.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class DrugCropBlock extends CannabisPlantBlock {
    public static final IntegerProperty AGE_3 = BlockStateProperties.AGE_3;

    public DrugCropBlock(Properties settings) {
        super(settings);
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE_3;
    }

    @Override
    public int getMaxAge(BlockState state) {
        return 3;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE_3, GROWING, NATURAL);
    }
}
