package com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.rendering;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.EntityShmokeStackz;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.models.ModelShmokeStackz;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderShmokeStackz extends RenderLiving<EntityShmokeStackz>
{
    public static final ResourceLocation TEXTURES = new ResourceLocation(OrangeSunshine.MODID+"\\textures\\entity\\shmoke_stackz.png");

    public RenderShmokeStackz(RenderManager manager)
    {
        super(manager, new ModelShmokeStackz(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityShmokeStackz entity)
    {
        return TEXTURES;
    }

    @Override
    protected void applyRotations(EntityShmokeStackz entityLiving, float p_77043_2_, float rotationYaw, float partialTicks)
    {
        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
    }
}
