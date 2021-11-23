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

package com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.tools;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

/**
 * Created by lukas on 14.10.14.
 */
public class IvAABBs
{
    public static AxisAlignedBB intersection(AxisAlignedBB bb, BlockPos pos)
    {
        return new AxisAlignedBB(Math.max(bb.minX, pos.getX()), Math.max(bb.minY, pos.getY()), Math.max(bb.minZ, pos.getZ()),
                Math.min(bb.maxX, pos.getX() + 1), Math.min(bb.maxY, pos.getY() + 1), Math.min(bb.maxZ, pos.getZ() + 1));
    }

    public static AxisAlignedBB boundsIntersection(AxisAlignedBB bb, BlockPos pos)
    {
        return new AxisAlignedBB(Math.max(bb.minX - pos.getX(), 0), Math.max(bb.minY - pos.getY(), 0), Math.max(bb.minZ - pos.getZ(), 0),
                Math.min(bb.maxX - pos.getX(), 1), Math.min(bb.maxY - pos.getY(), 1), Math.min(bb.maxZ - pos.getZ(), 1));
    }
}
