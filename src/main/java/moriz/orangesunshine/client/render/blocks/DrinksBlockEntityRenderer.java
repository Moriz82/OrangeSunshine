/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render.blocks;

import com.mojang.blaze3d.systems.RenderSystem;

import moriz.orangesunshine.block.PlacedDrinksBlock;
import moriz.orangesunshine.client.render.PlacedDrinksModelProvider;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererFactory;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.PlayerEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DrinksBlockEntityRenderer implements BlockEntityRenderer<PlacedDrinksBlock.Data> {
    private static final VoxelShape FILLED_SLOT_RAY_TRACE_SHAPE = Block.createCuboidShape(-2, 0, -2, 2, 4, 2);
    private static final VoxelShape EMPTY_SLOT_RAY_TRACE_SHAPE = Block.createCuboidShape(-2, 0, -2, 2, 0.01, 2);

    public DrinksBlockEntityRenderer(BlockEntityRendererFactory.Context context) { }

    @Override
    public void render(PlacedDrinksBlock.Data entity, float tickDelta, PoseStack matrices, MultiBufferSource vertices, int light, int overlay) {
        entity.forEachDrink((y, drink) -> {
            PlacedDrinksModelProvider.Entry geometry = PlacedDrinksModelProvider.INSTANCE.get(drink.stack().getItem()).orElse(PlacedDrinksModelProvider.Entry.DEFAULT);
            matrices.push();
            matrices.translate(drink.x(), y, drink.z());
            matrices.translate(0.5F, 0, 0.5F);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(drink.rotation()));
            matrices.translate(-0.5F, 0, -0.5F);
            PlacedDrinksModelProvider.INSTANCE.renderDrink(drink.stack(), matrices, vertices, light, overlay);
            matrices.pop();

            return geometry.height();
        });

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player != null && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hit = (BlockHitResult)client.crosshairTarget;
            if (hit.getBlockPos().equals(entity.getPos())) {
                PlacedDrinksBlock.Data.getHitPos(hit).ifPresent(pos -> {
                    WorldRenderer.drawShapeOutline(matrices, vertices.getBuffer(RenderLayer.getLines()), entity.hasDrink(pos) ? FILLED_SLOT_RAY_TRACE_SHAPE : EMPTY_SLOT_RAY_TRACE_SHAPE, pos.getX() / 16F, 0, pos.getZ() / 16F, 0, 0, 0, 0.4F, false);
                    RenderSystem.setShaderColor(0, 0, 0, 0.4F);
                    MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().draw(RenderLayer.getLines());
                    RenderSystem.setShaderColor(1, 1, 1, 1);
                });
            }
        }
    }
}
