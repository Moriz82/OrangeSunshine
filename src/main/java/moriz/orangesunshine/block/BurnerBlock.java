/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BurnerBlock extends Block {
    public static final MapCodec<BurnerBlock> CODEC = simpleCodec(BurnerBlock::new);
    private static final VoxelShape SHAPE = ShapeUtil.createCenteredShape(5, 2, 5);

    public BurnerBlock(BlockBehaviour.Properties settings) {
        super(settings.noOcclusion());
    }

    @Override
    public MapCodec<? extends BurnerBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
