/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render.blocks;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import java.util.function.Function;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;

/**
 * Updated by Sollace on 6 Jan 2023
 * Migrated to 1.21.11 Mojmap with RenderState
 */
public class BarrelModel extends Model {
    private final ModelPart tree;

    private final ModelPart barrel;
    private final ModelPart legs;
    private final ModelPart tap;
    private final ModelPart tapHandle;

    public BarrelModel(ModelPart tree) {
        super(tree, (Function<Identifier, RenderType>) RenderTypes::entityCutout);
        this.tree = tree;
        this.barrel = tree.getChild("barrel");
        this.legs = tree.getChild("rack");
        this.tap = barrel.getChild("tap");
        this.tapHandle = tap.getChild("handle");
    }

    public static LayerDefinition getTexturedMeshDefinition() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();
        PartDefinition barrel = root.addOrReplaceChild("barrel", CubeListBuilder.create(), PartPose.offset(0, 9, 0));

        barrel.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 30).addBox(-4, 5, -8, 1, 1, 16, CubeDeformation.NONE)
                .texOffs(0, 28).addBox(3, 5, -8, 1, 1, 16, CubeDeformation.NONE)
                .texOffs(0, 26).addBox(-4, 12, -8, 1, 1, 16, CubeDeformation.NONE)
                .texOffs(0, 23).addBox(3, 12, -8, 1, 1, 16, CubeDeformation.NONE)
                .texOffs(45, 38).addBox(4, 5, -8, 2, 8, 16, CubeDeformation.NONE)
                .texOffs(82, 38).addBox(-6, 5, -8, 2, 8, 16, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(-4, 5, -7, 8, 8, 14, CubeDeformation.NONE)
                .texOffs(45, 19).addBox(-4, 3, -8, 8, 2, 16, CubeDeformation.NONE)
                .texOffs(45, 0).addBox(-4, 13, -8, 8, 2, 16, CubeDeformation.NONE), PartPose.offset(0, -9, 0));

        PartDefinition tap = barrel.addOrReplaceChild("tap", CubeListBuilder.create(), PartPose.offset(0, -2.5F, -7));
        tap.addOrReplaceChild("handle", CubeListBuilder.create()
                .texOffs(12, 50).addBox(-1.5F, 0, -0.5F, 3, 0, 1, CubeDeformation.NONE), PartPose.offset(0, 0.71F, -2));
        tap.addOrReplaceChild("base", CubeListBuilder.create()
                .texOffs(7, 50).addBox(-0.5F, 5.2F, -9.5F, 1, 2, 1, CubeDeformation.NONE)
                .texOffs(0, 50).addBox(-0.5F, 6, -8.5F, 1, 1, 2, CubeDeformation.NONE), PartPose.offset(0, -6.5F, 7));

        PartDefinition legs = root.addOrReplaceChild("rack", CubeListBuilder.create(), PartPose.offsetAndRotation(0, 3F, 0, 0, 0, Mth.PI));
        legs.addOrReplaceChild("back_legs", CubeListBuilder.create().texOffs(94, 19).addBox(-5, -2, -1, 10, 4, 2, CubeDeformation.NONE), PartPose.offsetAndRotation(0, 1.5969F, 5.9183F, 0.1487F, 0, 0));
        legs.addOrReplaceChild("crossbeam", CubeListBuilder.create().texOffs(94, 0).addBox(-1, 1, -5, 2, 1, 10, CubeDeformation.NONE), PartPose.ZERO);
        legs.addOrReplaceChild("front_legs", CubeListBuilder.create().texOffs(94, 12).addBox(-5, -2, -1, 10, 4, 2, CubeDeformation.NONE), PartPose.offsetAndRotation(0, 1.7332F, -5.7591F, -0.1487F, 0, 0));
        return LayerDefinition.create(modelData, 128, 64);
    }

    public void setRotationAngles(BarrelRenderState state) {
        tapHandle.yRot = state.tapRotation;
        barrel.xRot = state.xRot;
        barrel.y = state.y;
        tap.zRot = 0;
        tap.visible = state.tapVisible;
        legs.visible = state.rackVisible;
        tree.y = state.treeY;
    }

    public void render(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        tree.render(matrices, vertices, light, overlay, color);
    }
}
