/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render.blocks;

import moriz.orangesunshine.block.BarrelBlock;
import moriz.orangesunshine.block.entity.BarrelBlockEntity;
import moriz.orangesunshine.client.render.RenderUtil;
import moriz.orangesunshine.fluid.*;
import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.fluid.container.Resovoir;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererFactory;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.registry.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class BarrelBlockEntityRenderer implements BlockEntityRenderer<BarrelBlockEntity> {
    private final BarrelModel model;

    public BarrelBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        model = new BarrelModel(BarrelModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(BarrelBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertices, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5F, 0, 0.5F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - entity.getCachedState().get(BarrelBlock.FACING).asRotation()));

        model.setRotationAngles(entity);
        model.render(matrices, vertices.getBuffer(model.getLayer(getBarrelTexture(entity))), light, overlay, 1, 1, 1, 1);

        Resovoir tank = entity.getTank(Direction.UP);

        SimpleFluid fluid = tank.getFluidType();
        if (!fluid.isEmpty()) {
            Identifier symbol = fluid.getSymbol(tank.getStack());

            if (MinecraftClient.getInstance().getResourceManager().getResource(symbol).isPresent()) {
                matrices.translate(0, 0.5, 0);
                if (entity.getCachedState().get(BarrelBlock.FACING).getAxis() == Axis.Y) {
                    Matrix4f mat = new Matrix4f();
                    RotationAxis.POSITIVE_X.rotationDegrees(90).get(mat);

                    matrices.multiplyPositionMatrix(mat);
                    matrices.translate(0, -0.1, 0);
                }
                float barrelZ = -0.4376F + 0.06F;
                float iconSize = 0.5F;
                VertexConsumer buffer = vertices.getBuffer(model.getLayer(symbol));


                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));

                for (int i = 0; i < 2; i++) {
                    RenderUtil.vertex(buffer, matrices, -iconSize, -iconSize, barrelZ, 1, 1, overlay, light);
                    RenderUtil.vertex(buffer, matrices, -iconSize,  iconSize, barrelZ, 1, 0, overlay, light);
                    RenderUtil.vertex(buffer, matrices,  iconSize,  iconSize, barrelZ, 0, 0, overlay, light);
                    RenderUtil.vertex(buffer, matrices,  iconSize, -iconSize, barrelZ, 0, 1, overlay, light);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                }
            }
        }

        matrices.pop();
    }

    public static Identifier getBarrelTexture(BarrelBlockEntity barrel) {
        BlockState state = barrel.getCachedState();
        Identifier id = Registries.BLOCK.getId(state.getBlock());
        return new Identifier(id.getNamespace(), "textures/entity/barrel/" + id.getPath() + ".png");
    }
}
