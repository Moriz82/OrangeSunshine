package moriz.orangesunshine.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Kratom crop block - 4 growth stages (AGE_3), extends DrugCropBlock.
 */
public class KratomPlantBlock extends DrugCropBlock {
    public static final MapCodec<KratomPlantBlock> CODEC = simpleCodec(KratomPlantBlock::new);

    public KratomPlantBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public MapCodec<? extends KratomPlantBlock> codec() {
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
