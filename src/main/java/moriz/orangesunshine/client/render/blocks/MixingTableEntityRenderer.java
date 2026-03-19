/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render.blocks;

import moriz.orangesunshine.block.entity.FlaskBlockEntity;
import moriz.orangesunshine.block.entity.MixingTableBlockEntity;
import moriz.orangesunshine.client.render.FluidBoxRenderer;
import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.fluid.container.Resovoir;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererFactory;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
public class MixingTableEntityRenderer<T extends MixingTableBlockEntity> implements BlockEntityRenderer<T> {

    public MixingTableEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertices, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5F, 0, 0.5F);

        float scale = 1/8F - 0.001F;
        matrices.scale(scale, scale, scale);

        matrices.pop();
    }
}
