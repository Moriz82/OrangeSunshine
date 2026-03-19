/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class CoffeaPlantBlock extends TobaccoPlantBlock {
    public static final MapCodec<CoffeaPlantBlock> CODEC = simpleCodec(CoffeaPlantBlock::new);

    public CoffeaPlantBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public MapCodec<? extends CoffeaPlantBlock> codec() {
        return CODEC;
    }

    @Override
    public int getMaxAge(BlockState state) {
        return state.getValue(TOP) ? 3 : super.getMaxAge(state);
    }
}
