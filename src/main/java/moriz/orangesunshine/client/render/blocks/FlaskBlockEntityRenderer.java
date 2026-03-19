/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render.blocks;

import moriz.orangesunshine.block.entity.FlaskBlockEntity;
import moriz.orangesunshine.client.render.FluidBoxRenderer;
import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.fluid.container.Resovoir;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererFactory;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

/**
 * Created by lukas on 25.10.14.
 * Updated by Sollace on 5 Jan 2023
 *
 * Renders fluid inside the flask
 */
public class FlaskBlockEntityRenderer<T extends FlaskBlockEntity> implements BlockEntityRenderer<T> {

    public FlaskBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertices, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5F, 0, 0.5F);

        float scale = 1/8F - 0.001F;
        matrices.scale(scale, scale, scale);


        Resovoir tank = entity.getTank(Direction.UP);
        SimpleFluid fluid = tank.getFluidType();

        if (!fluid.isEmpty()) {
            float fluidHeight = Mth.clamp((float) tank.getLevel() / (float) tank.getCapacity(), 0, 1);

            FluidBoxRenderer fluidRenderer = FluidBoxRenderer.getInstance()
                    .texture(vertices, tank)
                    .light(light).overlay(overlay)
                    .position(matrices);
            fluidRenderer.draw(-1, 0, -2, 2, fluidHeight, 1, Direction.NORTH, Direction.UP);
            fluidRenderer.draw(-1, 0,  1, 2, fluidHeight, 1, Direction.SOUTH, Direction.UP);
            fluidRenderer.draw(-2, 0, -1, 1, fluidHeight, 2, Direction.WEST, Direction.UP);
            fluidRenderer.draw( 1, 0, -1, 1, fluidHeight, 2, Direction.EAST, Direction.UP);
            fluidRenderer.draw(-1, 0, -1, 2, fluidHeight, 2, Direction.UP);
        }

        matrices.pop();
    }
}
