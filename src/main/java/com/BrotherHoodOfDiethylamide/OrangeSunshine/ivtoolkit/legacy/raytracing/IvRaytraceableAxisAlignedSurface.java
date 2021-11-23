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

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import java.util.List;

public class IvRaytraceableAxisAlignedSurface extends IvRaytraceableObject
{
    private double x, y, z;
    private double width, height, depth;

    private Object hitInfo;

    public IvRaytraceableAxisAlignedSurface(Object userInfo, Object hitInfo, double y, double z, double width, double height, double depth, double x)
    {
        super(userInfo);
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.hitInfo = hitInfo;
    }

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getZ()
    {
        return z;
    }

    public void setZ(double z)
    {
        this.z = z;
    }

    public double getWidth()
    {
        return width;
    }

    public void setWidth(double width)
    {
        this.width = width;
    }

    public double getHeight()
    {
        return height;
    }

    public void setHeight(double height)
    {
        this.height = height;
    }

    public double getDepth()
    {
        return depth;
    }

    public void setDepth(double depth)
    {
        this.depth = depth;
    }

    public Object getHitInfo()
    {
        return hitInfo;
    }

    public void setHitInfo(Object hitInfo)
    {
        this.hitInfo = hitInfo;
    }

    @Override
    public void addRaytracedIntersectionsForLineToList(List<IvRaytracedIntersection> list, double x, double y, double z, double xDir, double yDir, double zDir)
    {
        checkValid();

        double t = 0;
        boolean cantHit = false;
        boolean onPlane = false;

        if (width == 0)
        {
            double xDist = (this.x - x);

            if (xDist == 0 && xDir == 0)
            {
                onPlane = true;
            }
            else if (xDist != 0 && xDir == 0)
            {
                cantHit = true;
            }
            else
            {
                t = xDist / xDir;
            }
        }
        else if (height == 0)
        {
            double yDist = (this.y - y);

            if (yDist == 0 && yDir == 0)
            {
                onPlane = true;
            }
            else if (yDist != 0 && yDir == 0)
            {
                cantHit = true;
            }
            else
            {
                t = yDist / yDir;
            }
        }
        else if (depth == 0)
        {
            double zDist = (this.z - z);

            if (zDist == 0 && zDir == 0)
            {
                onPlane = true;
            }
            else if (zDist != 0 && zDir == 0)
            {
                cantHit = true;
            }
            else
            {
                t = zDist / zDir;
            }
        }

        if (onPlane)
        {
            return; // For our purposes, this is not hit :<
        }

        if (!cantHit)
        {
            double[] hitPoint = new double[]{x + t * xDir, y + t * yDir, z + t * zDir};

            if (withinBounds(hitPoint[0], this.x, this.x + this.width) && withinBounds(hitPoint[1], this.y, this.y + this.height) && withinBounds(hitPoint[2], this.z, this.z + this.depth))
            {
                list.add(new IvRaytracedIntersection(this, hitInfo, hitPoint));
            }
        }
    }

    private boolean withinBounds(double value, double min, double max)
    {
        return value >= (min - 0.0001f) && value <= (max + 0.0001f);
    }

    private void checkValid()
    {
        int lengths0 = 0;
        if (width == 0)
        {
            lengths0++;
        }
        if (height == 0)
        {
            lengths0++;
        }
        if (depth == 0)
        {
            lengths0++;
        }

        if (lengths0 != 1)
        {
            throw new ArithmeticException("Axis aligned surface must have exactly one length that is zero. (But has: " + lengths0 + ")");
        }
    }

    @Override
    public void drawOutlines()
    {
        BufferBuilder renderer = Tessellator.getInstance().getBuffer();

        renderer.begin(3, DefaultVertexFormats.POSITION);
        if (width == 0)
        {
            renderer.pos(x, y, z).endVertex();
            renderer.pos(x, y + height, z).endVertex();
            renderer.pos(x, y + height, z + depth).endVertex();
            renderer.pos(x, y, z + depth).endVertex();
            renderer.pos(x, y, z).endVertex();
        }
        if (height == 0)
        {
            renderer.pos(x, y, z).endVertex();
            renderer.pos(x + width, y, z).endVertex();
            renderer.pos(x + width, y, z + depth).endVertex();
            renderer.pos(x, y, z + depth).endVertex();
            renderer.pos(x, y, z).endVertex();
        }
        if (depth == 0)
        {
            renderer.pos(x, y, z).endVertex();
            renderer.pos(x + width, y, z).endVertex();
            renderer.pos(x + width, y + height, z).endVertex();
            renderer.pos(x, y + height, z).endVertex();
            renderer.pos(x, y, z).endVertex();
        }
        Tessellator.getInstance().draw();
    }
}
