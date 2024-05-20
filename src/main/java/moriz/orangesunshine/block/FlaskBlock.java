/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import com.mojang.serialization.MapCodec;

import moriz.orangesunshine.block.entity.*;
import moriz.orangesunshine.block.entity.FlaskBlockEntity;
import moriz.orangesunshine.block.entity.PSBlockEntities;
import moriz.orangesunshine.screen.FluidContraptionScreenHandler;
import moriz.orangesunshine.screen.PSScreenHandlers;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

/**
 * Created by lukas on 25.10.14.
 */
public class FlaskBlock extends BlockWithFluid<FlaskBlockEntity> {
    public static final MapCodec<FlaskBlock> CODEC = createCodec(FlaskBlock::new);
    private static final VoxelShape SHAPE = VoxelShapes.union(
        createCuboidShape(5, 0, 5, 11, 5, 11),
        createCuboidShape(4, 0, 6, 12, 4, 10),
        createCuboidShape(6, 0, 4, 10, 4, 12),
        createCuboidShape(6, 5, 6, 10, 11, 10),
        createCuboidShape(5, 7, 6, 11, 10, 10),
        createCuboidShape(6, 7, 5, 10, 10, 11)
    );

    public FlaskBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends FlaskBlock> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected BlockEntityType<FlaskBlockEntity> getBlockEntityType() {
        return PSBlockEntities.FLASK;
    }

    @Override
    protected ScreenHandlerType<FluidContraptionScreenHandler<FlaskBlockEntity>> getScreenHandlerType() {
        return PSScreenHandlers.FLASK;
    }
}
