/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render.blocks;

import moriz.orangesunshine.block.entity.MashTubBlockEntity;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

/**
 * Migrated to 1.21.11 Mojmap with RenderState
 */
public class MashTubBlockEntityRenderer implements BlockEntityRenderer<MashTubBlockEntity, MashTubRenderState> {

    public MashTubBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public MashTubRenderState createRenderState() {
        return new MashTubRenderState();
    }

    @Override
    public void extractRenderState(MashTubBlockEntity entity, MashTubRenderState state, float tickDelta, Vec3 offset, CrumblingOverlay crumbling) {
        var tank = entity.getTank(Direction.UP);
        state.fluidColor = tank.getFluidType().getColor(tank.getStack());
        int cap = tank.getCapacity();
        state.fluidLevel = cap <= 0 ? 0 : (float) tank.getLevel() / cap;
        state.solidContents = entity.solidContents.copy();
        state.suppliedIngredients.clear();
        state.suppliedIngredients.putAll(entity.getSuppliedIngredients());
    }

    @Override
    public void submit(MashTubRenderState state, PoseStack matrices, SubmitNodeCollector collector, CameraRenderState cameraState) {
        // Rendering logic for Mash Tub (fluids and solids)
        // This will involve submitting custom geometry or models based on state.fluidLevel and state.solidContents
        
        if (state.fluidLevel > 0) {
            // Submit fluid geometry
        }
        
        if (!state.solidContents.isEmpty()) {
            // Submit solid contents model/item
        }
        
        if (!state.suppliedIngredients.isEmpty()) {
            // Submit ingredients models/items
        }
    }
}
