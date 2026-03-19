/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import com.mojang.serialization.MapCodec;
import moriz.orangesunshine.block.entity.FlaskBlockEntity;
import moriz.orangesunshine.block.entity.PSBlockEntities;
import moriz.orangesunshine.screen.FluidContraptionScreenHandler;
import moriz.orangesunshine.screen.PSScreenHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Created by lukas on 25.10.14.
 */
public class FlaskBlock extends BlockWithFluid<FlaskBlockEntity> {
    public static final MapCodec<FlaskBlock> CODEC = simpleCodec(FlaskBlock::new);
    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(5, 0, 5, 11, 5, 11),
        Block.box(4, 0, 6, 12, 4, 10),
        Block.box(6, 0, 4, 10, 4, 12),
        Block.box(6, 5, 6, 10, 11, 10),
        Block.box(5, 7, 6, 11, 10, 10),
        Block.box(6, 7, 5, 10, 10, 11)
    );

    public FlaskBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public MapCodec<? extends FlaskBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected BlockEntityType<FlaskBlockEntity> getBlockEntityType() {
        return PSBlockEntities.FLASK;
    }

    @Override
    protected MenuType<FluidContraptionScreenHandler<FlaskBlockEntity>> getScreenHandlerType() {
        return PSScreenHandlers.FLASK;
    }
}
