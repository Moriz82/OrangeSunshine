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

import java.util.List;

public class IvRaytraceableSurface extends IvRaytraceableObject
{
    public double x, y, z;
    public double x1, y1, z1;
    public double x2, y2, z2;

    public IvRaytraceableSurface(Object userInfo, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        super(userInfo);

        this.x = x;
        this.y = y;
        this.z = z;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    @Override
    public void addRaytracedIntersectionsForLineToList(List<IvRaytracedIntersection> list, double x, double y, double z, double xDir, double yDir, double zDir)
    {
        // La - S0 = Lv * t + (S1 - S0) * u + (S2 - S0) * v

        // ->
        // x - this.x = xDir * t + (x1 - x) * u + (x2 - x) * v
        // y - this.y = yDir * t + (y1 - y) * u + (y2 - y) * v
        // z - this.z = zDir * t + (z1 - z) * u + (z2 - z) * v

        // ->
        // t = (x - this.x - (x1 - x) * u - (x2 - x) * v) / xDir
        // u = (y - this.y - yDir * t - (y2 - y) * v) / (y1 - y)
        // v = (z - this.z - zDir * t - (z1 - z) * u) / (z2 - z)

        // ->
        // t = (x - this.x - (x1 - x) * u - (x2 - x) * v) / xDir

        // u = (y - this.y - yDir * (x - this.x - (x1 - x) * u - (x2 - x) * v) / xDir - (y2 - y) * v) / (y1 - y)
        // u = (y - this.y - yDir * (x - this.x - (x2 - x) * v) / xDir - (x1 - x) * u / xDir - (y2 - y) * v) / (y1 - y)
        // u = (y - this.y - yDir * (x - this.x - (x2 - x) * v) / xDir - (y2 - y) * v) / (y1 - y) - (x1 - x) * u / xDir / (y1 - y)
        // u + (x1 - x) * u / xDir / (y1 - y) = (y - this.y - yDir * (x - this.x - (x2 - x) * v) / xDir - (y2 - y) * v) / (y1 - y)
        // (x1 - x + 1) * u / xDir / (y1 - y) = (y - this.y - yDir * (x - this.x - (x2 - x) * v) / xDir - (y2 - y) * v) / (y1 - y)
        // (x1 - x + 1) * u = xDir * (y1 - y) * (y - this.y - yDir * (x - this.x - (x2 - x) * v) / xDir - (y2 - y) * v) / (y1 - y)
        // (x1 - x + 1) * u = xDir * (y - this.y - yDir * (x - this.x - (x2 - x) * v) / xDir - (y2 - y) * v)
        // u = (x1 - x + 1) * xDir * (y - this.y - yDir * (x - this.x - (x2 - x) * v) / xDir - (y2 - y) * v)

        // v = (z - this.z - zDir * t - (z1 - z) * u) / (z2 - z)
        // v = (z - this.z - zDir * (x - this.x - (x1 - x) * u - (x2 - x) * v) / xDir - (z1 - z) * u) / (z2 - z)
        // v = (z - this.z - zDir * (x - this.x - (x1 - x) * (x1 - x + 1) * xDir * (y - this.y - yDir * (x - this.x - (x2 - x) * v) / xDir - (y2 - y) * v) - (x2 - x) * v) / xDir - (z1 - z) * (x1 - x + 1) * xDir * (y - this.y - yDir * (x - this.x - (x2 - x) * v) / xDir - (y2 - y) * v)) / (z2 - z)
        // v = (z - this.z - zDir * (x - this.x - (x1 - x) * (x1 - x + 1) * xDir * (y - this.y - yDir * (x - this.x) / xDir - yDir * (x2 - x) * v / xDir - (y2 - y) * v) - (x2 - x) * v) / xDir - (z1 - z) * (x1 - x + 1) * xDir * (y - this.y - yDir * (x - this.x - (x2 - x) * v) / xDir - (y2 - y) * v)) / (z2 - z)
        // v = (z - this.z - zDir * (x - this.x - (x1 - x) * (x1 - x + 1) * xDir * (y - this.y - yDir * (x - this.x) / xDir) - yDir * (x2 - x) * v / xDir - (y2 - y) * v - (x2 - x) * v) / xDir - (z1 - z) * (x1 - x + 1) * xDir * (y - this.y - yDir * (x - this.x - (x2 - x) * v) / xDir - (y2 - y) * v)) / (z2 - z)
    }

}
