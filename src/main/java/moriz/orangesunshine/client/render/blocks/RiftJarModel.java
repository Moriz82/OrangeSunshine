/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.client.render.blocks;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import java.util.function.Function;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;

/**
 * Updated by Sollace on 5 Jan 2023
 * Migrated to 1.21.11 Mojmap
 */
public class RiftJarModel extends Model {
    private final ModelPart tree;

    private final ModelPart cork;
    private final ModelPart knot;

    private final ModelPart interior;

    public RiftJarModel(ModelPart tree) {
        super(tree, (Function<Identifier, RenderType>) RenderTypes::entityTranslucent);
        this.tree = tree;
        this.cork = tree.getChild("cork");
        this.knot = tree.getChild("knot");
        this.interior = tree.getChild("interior");
    }

    public static LayerDefinition getTexturedMeshDefinition() {
        MeshDefinition data = new MeshDefinition();
        PartDefinition root = data.getRoot();
        root.addOrReplaceChild("glass_1", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-4F, 0F, -4F, 8, 5, 8), PartPose.offset(0F, 19F, 0F));
        root.addOrReplaceChild("glass_2", CubeListBuilder.create().texOffs(0, 14).mirror().addBox(-4F, 0F, -4F, 8, 5, 8), PartPose.offset(0F, 12F, 0F));
        root.addOrReplaceChild("glass_3", CubeListBuilder.create().texOffs(33, 24).mirror().addBox(-3F, 0F, -3F, 6, 2, 6), PartPose.offset(0F, 17F, 0F));

        root.addOrReplaceChild("rope", CubeListBuilder.create().texOffs(33, 0).mirror().addBox(-3.5F, 0F, -3.5F, 7, 2, 7), PartPose.offset(0F, 17F, 0F));
        root.addOrReplaceChild("knot", CubeListBuilder.create().texOffs(33, 2).mirror().addBox(0F, 0F, -4F, 0.001F, 5, 8), PartPose.offsetAndRotation(-3.5F, 17F, 0F, 0F, 0F, -0.2602503F));
        root.addOrReplaceChild("cork", CubeListBuilder.create().texOffs(33, 16).mirror().addBox(-3F, -0.001F, -3F, 6, 2, 6), PartPose.offset(0F, 10F, 0F));

        CubeDeformation dilation = new CubeDeformation(0.001f);
        root.addOrReplaceChild("interior", CubeListBuilder.create()
            .addBox(-4, 0, -4, 8, 5, 8, dilation)
            .addBox(-3, 5, -3, 6, 2, 6, new CubeDeformation(0.001f, -0.001f, 0.001f))
            .addBox(-4, 7, -4, 8, 2, 8, dilation), PartPose.ZERO);

        return LayerDefinition.create(data, 64, 32);
    }

    public void setAngles(RiftJarRenderState state) {
        cork.x = state.fractionOpen * 2;
        cork.yRot = state.fractionOpen * 0.1F;
        knot.zRot = 0.2602503F + (state.fractionHandleUp * (1 + Mth.sin(state.ticks * 0.1f) * 0.1f)) * 0.5f;
    }

    public void render(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        interior.visible = false;
        tree.render(matrices, vertices, light, overlay, color);
        interior.visible = true;
    }

    public void renderInterior(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        interior.render(matrices, vertices, light, overlay, color);
    }
}
