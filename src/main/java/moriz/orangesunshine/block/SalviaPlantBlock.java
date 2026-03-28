package moriz.orangesunshine.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Salvia Divinorum crop block - 4 growth stages (AGE_3), extends DrugCropBlock.
 */
public class SalviaPlantBlock extends DrugCropBlock {
    public static final MapCodec<SalviaPlantBlock> CODEC = simpleCodec(SalviaPlantBlock::new);

    public SalviaPlantBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public MapCodec<? extends SalviaPlantBlock> codec() {
        return CODEC;
    }

    @Override
    protected float getRandomGrowthChance() {
        return 0.1F;
    }

    @Override
    public int getMaxHeight() {
        return 1;
    }
}
