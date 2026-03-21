/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */
package moriz.orangesunshine.client.render.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

/**
 * Migrated to 1.21.11 Mojmap
 */
public class PeyoteModel extends Model {
    private final ModelPart root;

    public PeyoteModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.root = root;
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        root.render(matrices, vertices, light, overlay, color);
    }

    public static LayerDefinition stage0() {
        MeshDefinition data = new MeshDefinition();
        PartDefinition root = data.getRoot();
        root.addOrReplaceChild("one", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1F, -1.5F, -1F, 2, 2, 2), PartPose.offsetAndRotation(0F, 24F, 0F, 0.0371786F, 0.2230717F, 0F));
        return LayerDefinition.create(data, 32, 32);
    }

    public static LayerDefinition stage1() {
        MeshDefinition data = new MeshDefinition();
        PartDefinition root = data.getRoot();
        root.addOrReplaceChild("one", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-2F, -2F, -2F, 3, 3, 3), PartPose.offsetAndRotation(0F, 24F, 0, 0.0371786F, 0.1487144F, 0F));
        root.addOrReplaceChild("two", CubeListBuilder.create().texOffs(0, 7).mirror().addBox(0F, -1F, -1.5F, 2, 2, 2), PartPose.offsetAndRotation(1F, 24F, 0F, 0.1858931F, -0.2230717F, 0F));
        root.addOrReplaceChild("three", CubeListBuilder.create().texOffs(0, 12).mirror().addBox(0F, -1.5F, 0F, 2, 2, 2), PartPose.offsetAndRotation(0F, 24F, 1F, -0.2230717F, 0.3346075F, 0F));
        root.addOrReplaceChild("four", CubeListBuilder.create().texOffs(0, 17).mirror().addBox(1F, 0F, 1F, 1, 1, 1), PartPose.offsetAndRotation(0F, 23F, 0F, 0F, -0.8551081F, 0F));
        return LayerDefinition.create(data, 64, 32);
    }

    public static LayerDefinition stage2() {
        MeshDefinition data = new MeshDefinition();
        PartDefinition root = data.getRoot();
        root.addOrReplaceChild("one", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-2F, -3F, -3F, 4, 4, 4), PartPose.offsetAndRotation(-1F, 24F, -1F, 0.1115358F, 0.1858931F, -0.1487144F));
        root.addOrReplaceChild("two", CubeListBuilder.create().texOffs(0, 9).mirror().addBox(0F, -2.5F, -2F, 3, 3, 3), PartPose.offsetAndRotation(0.5F, 24F, -1F, 0.0371786F, -0.0371786F, 0.1115358F));
        root.addOrReplaceChild("three", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1F, -2.5F, 0.5F, 3, 3, 3), PartPose.offsetAndRotation(0F, 24F, 0F, -0.1487144F, 0.3717861F, 0F));
        root.addOrReplaceChild("four", CubeListBuilder.create().texOffs(0, 23).mirror().addBox(-2.5F, -2F, 0F, 2, 2, 2), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0.0371786F, -0.1115358F));
        root.addOrReplaceChild("five", CubeListBuilder.create().texOffs(0, 28).mirror().addBox(-1F, -1.5F, 0F, 2, 2, 2), PartPose.offsetAndRotation(3F, 24F, 0F, -0.2602503F, 0.6320364F, 0F));
        root.addOrReplaceChild("six", CubeListBuilder.create().texOffs(17, 0).mirror().addBox(0F, -1F, 1F, 1, 1, 1), PartPose.offsetAndRotation(0F, 24F, 2F, 0F, -0.8922867F, 0.2974289F));
        root.addOrReplaceChild("seven", CubeListBuilder.create().texOffs(17, 3).mirror().addBox(-1F, -1F, -1F, 1, 1, 1), PartPose.offsetAndRotation(-3F, 24F, -1F, 0.0743572F, 0.1487144F, 0F));
        return LayerDefinition.create(data, 64, 32);
    }

    public static LayerDefinition stage3() {
        MeshDefinition data = new MeshDefinition();
        PartDefinition root = data.getRoot();
        root.addOrReplaceChild("one", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-2F, -3F, -3F, 4, 4, 4), PartPose.offsetAndRotation(-1F, 24F, -1F, 0.1115358F, 0.1858931F, -0.1487144F));
        root.addOrReplaceChild("two", CubeListBuilder.create().texOffs(0, 9).mirror().addBox(0F, -2.5F, -3F, 4, 3, 4), PartPose.offsetAndRotation(0.5F, 24F, -1F, 0.1858931F, -0.2602503F, 0.1115358F));
        root.addOrReplaceChild("three", CubeListBuilder.create().texOffs(0, 17).mirror().addBox(-2F, -2.5F, 0.5F, 4, 3, 4), PartPose.offsetAndRotation(0F, 24F, 0F, -0.1487144F, 0.3717861F, 0F));
        root.addOrReplaceChild("four", CubeListBuilder.create().texOffs(0, 25).mirror().addBox(-3.5F, -2F, 0F, 3, 3, 3), PartPose.offsetAndRotation(0F, 24F, 0F, -0.1487144F, -0.1335332F, -0.1115358F));
        root.addOrReplaceChild("five", CubeListBuilder.create().texOffs(17, 10).mirror().addBox(-1F, -2F, 0F, 3, 3, 3), PartPose.offsetAndRotation(3F, 24F, 0F, -0.2602503F, 0.6320364F, 0F));
        root.addOrReplaceChild("six", CubeListBuilder.create().texOffs(17, 0).mirror().addBox(0F, -1.5F, 1F, 2, 2, 2), PartPose.offsetAndRotation(0F, 24F, 2F, 0F, -1.375609F, 0.2974289F));
        root.addOrReplaceChild("seven", CubeListBuilder.create().texOffs(17, 5).mirror().addBox(-2F, -1F, -1F, 2, 2, 2), PartPose.offsetAndRotation(-3F, 24F, -1F, 0.0743572F, 0.2230717F, -0.2230717F));
        root.addOrReplaceChild("eight", CubeListBuilder.create().texOffs(17, 16).mirror().addBox(0F, -1.5F, -1F, 2, 2, 2), PartPose.offsetAndRotation(0F, 24F, -5F, 0.1487144F, -0.2602503F, 0F));
        root.addOrReplaceChild("nine", CubeListBuilder.create().texOffs(17, 21).mirror().addBox(-1F, -1F, 0F, 1, 1, 1), PartPose.offsetAndRotation(-2F, 24F, -5F, 0F, -0.2602503F, -0.2974289F));
        root.addOrReplaceChild("ten", CubeListBuilder.create().texOffs(17, 24).mirror().addBox(0F, -1F, 0F, 1, 1, 1), PartPose.offsetAndRotation(0F, 24F, 5F, -0.1487144F, 0.1487144F, 0F));
        root.addOrReplaceChild("flower1", CubeListBuilder.create().texOffs(26, 0).mirror().addBox(0F, -5F, -2.5F, 0, 2, 3), PartPose.offsetAndRotation(-1F, 24F, -1F, 0.111544F, 0.185895F, -0.1487195F));
        root.addOrReplaceChild("flower2", CubeListBuilder.create().texOffs(26, 0).mirror().addBox(-1.5F, -5F, -1F, 3, 2, 0), PartPose.offsetAndRotation(-1F, 24F, -1F, 0.111544F, 0.185895F, -0.1487195F));
        return LayerDefinition.create(data, 64, 32);
    }
}
