/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render.blocks;

import moriz.orangesunshine.block.BarrelBlock;
import moriz.orangesunshine.block.entity.BarrelBlockEntity;
import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.fluid.container.Resovoir;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.RenderType;

public class BarrelBlockEntityRenderer implements BlockEntityRenderer<BarrelBlockEntity, BarrelRenderState> {
    private final BarrelModel model;

    public BarrelBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        model = new BarrelModel(BarrelModel.getTexturedModelData().bakeRoot());
    }

    @Override
    public BarrelRenderState createRenderState() {
        return new BarrelRenderState();
    }

    @Override
    public void extractRenderState(BarrelBlockEntity entity, BarrelRenderState state, float tickDelta, Vec3 offset, CrumblingOverlay crumbling) {
        state.facing = entity.getBlockState().getValue(BarrelBlock.FACING);
        state.tapRotation = entity.tapRotation;
        BlockState blockState = entity.getBlockState();
        state.xRot = state.facing.getAxis() == Direction.Axis.Y ? Mth.HALF_PI : 0;
        state.y = 9 - 2 * state.xRot;
        state.tapVisible = state.xRot == 0 && blockState.getValue(BarrelBlock.TAPPED);
        state.rackVisible = state.xRot == 0;
        state.treeY = state.rackVisible ? 0 : 2;
        state.texture = getBarrelTexture(entity);

        Resovoir tank = entity.getTank(Direction.UP);
        SimpleFluid fluid = tank.getFluidType();
        if (!fluid.isEmpty()) {
            state.symbol = fluid.getSymbol(tank.getStack());
        } else {
            state.symbol = null;
        }
    }

    @Override
    public void submit(BarrelRenderState state, PoseStack matrices, SubmitNodeCollector collector, CameraRenderState cameraState) {
        matrices.pushPose();
        matrices.translate(0.5F, 0, 0.5F);
        matrices.mulPose(Axis.YP.rotationDegrees(180 - state.facing.toYRot()));

        model.setRotationAngles(state);
        
        collector.order(0).submitModel(model, state, matrices, model.renderType(state.texture), state.lightCoords, state.overlayCoords, 0xFFFFFFFF, state.breakProgress);

        matrices.popPose();
    }

    public static Identifier getBarrelTexture(BarrelBlockEntity barrel) {
        BlockState state = barrel.getBlockState();
        // BuiltInRegistries.BLOCK.getKey(state.getBlock())
        // For now just return a constant to test compilation
        return Identifier.fromNamespaceAndPath("orangesunshine", "textures/entity/barrel/oak_barrel.png");
    }
}
