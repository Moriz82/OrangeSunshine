/*
 * Copyright 2014 Lukas Tenbrink
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.raytracing;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;

/**
 * Created by lukas on 13.02.14.
 */
public class IvRaytracerMC
{
    public static List<IvRaytracedIntersection> getIntersections(List<IvRaytraceableObject> objects, Entity entity)
    {
        double x = entity.posX;
        double y = entity.posY + entity.getEyeHeight();
        double z = entity.posZ;

        Vec3d lookVec = entity.getLookVec();
        return IvRaytracer.getIntersections(objects, x, y, z, lookVec.x, lookVec.y, lookVec.z);
    }

    public static IvRaytracedIntersection getFirstIntersection(List<IvRaytraceableObject> objects, Entity entity)
    {
        double x = entity.posX;
        double y = entity.posY + entity.getEyeHeight();
        double z = entity.posZ;

        Vec3d lookVec = entity.getLookVec();

        List<IvRaytracedIntersection> intersections = IvRaytracer.getIntersections(objects, x, y, z, lookVec.x, lookVec.y, lookVec.z);

        return IvRaytracer.findFirstIntersection(intersections, x, y, z, lookVec.x, lookVec.y, lookVec.z);
    }

    public static void drawStandardOutlinesFromTileEntity(List<IvRaytraceableObject> objects, double d, double d1, double d2, TileEntity tileEntity)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) d - tileEntity.getPos().getX(), (float) d1 - tileEntity.getPos().getY(), (float) d2 - tileEntity.getPos().getZ());
        IvRaytracer.drawStandardOutlines(objects);
        GlStateManager.popMatrix();
    }
}
