/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class CocaPlantBlock extends CannabisPlantBlock {
    public static final MapCodec<CocaPlantBlock> CODEC = simpleCodec(CocaPlantBlock::new);
    private static final IntegerProperty AGE_12 = IntegerProperty.create("age", 0, 12);

    public CocaPlantBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public MapCodec<? extends CocaPlantBlock> codec() {
        return CODEC;
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE_12;
    }

    @Override
    public int getMaxAge(BlockState state) {
        return 12;
    }

    @Override
    protected float getRandomGrowthChance() {
        return 0.1F;
    }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
        return floor.is(Blocks.FARMLAND) || floor.is(this) || floor.is(BlockTags.DIRT) || floor.is(Blocks.GRASS_BLOCK);
    }
}
