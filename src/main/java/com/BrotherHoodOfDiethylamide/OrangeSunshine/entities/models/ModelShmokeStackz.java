package com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.entity.Entity;

public class ModelShmokeStackz extends ModelBase {
    public ModelRenderer head;
    public ModelRenderer rightArm;
    public ModelRenderer leftArm;
    public ModelRenderer middleArm;
    public ModelRenderer leftLeg;
    public ModelRenderer chest;
    public ModelRenderer body;
    public ModelRenderer rightLeg;
    public ModelRenderer nose;

    public ModelShmokeStackz() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.leftLeg = new ModelRenderer(this, 0, 22);
        this.leftLeg.mirror = true;
        this.leftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.rightLeg = new ModelRenderer(this, 0, 22);
        this.rightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.rightArm = new ModelRenderer(this, 44, 22);
        this.rightArm.setRotationPoint(0.0F, 3.0F, -1.0F);
        this.rightArm.addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F);
        this.setRotateAngle(rightArm, -0.7499679795819634F, 0.0F, 0.0F);
        this.middleArm = new ModelRenderer(this, 40, 38);
        this.middleArm.setRotationPoint(0.0F, 3.0F, -1.0F);
        this.middleArm.addBox(-4.0F, 2.0F, -2.0F, 8, 4, 4, 0.0F);
        this.setRotateAngle(middleArm, -0.7499679795819634F, 0.0F, 0.0F);
        this.head = new ModelRenderer(this, 0, 0);
        this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.head.addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, 0.0F);
        this.leftArm = new ModelRenderer(this, 44, 22);
        this.leftArm.setRotationPoint(0.0F, 3.0F, -1.0F);
        this.leftArm.addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F);
        this.setRotateAngle(leftArm, -0.7499679795819634F, 0.0F, 0.0F);
        this.nose = new ModelRenderer(this, 24, 0);
        this.nose.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.nose.addBox(-1.0F, -1.0F, -6.0F, 2, 4, 2, 0.0F);
        this.chest = new ModelRenderer(this, 16, 20);
        this.chest.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.chest.addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, 0.0F);
        this.body = new ModelRenderer(this, 0, 38);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, 0.5F);
        this.head.addChild(this.nose);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.leftLeg.render(f5);
        this.rightLeg.render(f5);
        this.rightArm.render(f5);
        this.middleArm.render(f5);
        this.head.render(f5);
        this.leftArm.render(f5);
        this.chest.render(f5);
        this.body.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}