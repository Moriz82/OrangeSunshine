/*
 * Copyright 2015 Lukas Tenbrink
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

package com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.rendering.grid;

import com.google.common.base.Function;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.*;

import static net.minecraft.util.EnumFacing.*;

/**
 * Created by lukas on 20.03.15.
 */
public class GridQuadCache<T> implements Iterable<GridQuadCache.CachedQuadLevel<T>>
{
    protected final List<CachedQuadLevel<T>> cachedQuadLevels = new ArrayList<>();

    protected float[] size;

    public static int[] getCacheAxes(EnumFacing direction, int... axes)
    {
        switch (direction) {
            case DOWN:
            case UP:
                return new int[]{axes[1], axes[0], axes[2]};
            case WEST:
            case EAST:
                return new int[]{axes[0], axes[2], axes[1]};
            case NORTH:
            case SOUTH:
                return new int[]{axes[2], axes[1], axes[0]};
        }

        throw new IllegalArgumentException();
    }

    public static int[] getNormalAxes(EnumFacing direction, int... axes)
    {
        return getCacheAxes(direction, axes);
    }

    public static float[] getCacheAxes(EnumFacing direction, float... axes)
    {
        switch (direction) {
            case DOWN:
            case UP:
                return new float[]{axes[1], axes[0], axes[2]};
            case WEST:
            case EAST:
                return new float[]{axes[0], axes[2], axes[1]};
            case NORTH:
            case SOUTH:
                return new float[]{axes[2], axes[1], axes[0]};
        }

        throw new IllegalArgumentException();
    }

    public static float[] getNormalAxes(EnumFacing direction, float... axes)
    {
        return getCacheAxes(direction, axes);
    }

    public static <T> GridQuadCache<T> createQuadCache(int[] size, float[] scale, Function<Pair<BlockPos, EnumFacing>, T> mapper)
    {
        return createQuadCacheGreedy(size, scale, mapper);
    }

    protected static <T> GridQuadCache<T> createQuadCacheGreedy(int[] size, float[] scale, Function<Pair<BlockPos, EnumFacing>, T> mapper)
    {
        Map<QuadContext<T>, CoordGrid> partialCache = new HashMap<>();

        for (int x = 0; x < size[0]; x++)
            for (int y = 0; y < size[1]; y++)
                for (int z = 0; z < size[2]; z++) {
                    BlockPos coord = new BlockPos(x, y, z);
                    addToCache(partialCache, mapper, UP, coord);
                    addToCache(partialCache, mapper, DOWN, coord);
                    addToCache(partialCache, mapper, NORTH, coord);
                    addToCache(partialCache, mapper, EAST, coord);
                    addToCache(partialCache, mapper, SOUTH, coord);
                    addToCache(partialCache, mapper, WEST, coord);
                }

        Set<Map.Entry<QuadContext<T>, CoordGrid>> quads = partialCache.entrySet();
        GridQuadCache<T> cache = new GridQuadCache<>();
        cache.size = new float[3];
        for (int i = 0; i < 3; i++)
            cache.size[i] = size[i] * scale[i];

        for (Map.Entry<QuadContext<T>, CoordGrid> entry : quads) {
            QuadContext<T> context = entry.getKey();

            int[] sAxes = getCacheAxes(context.direction, size);
            float[] scAxes = getCacheAxes(context.direction, scale);

            QuadCollection mesh = entry.getValue().computeMesh(0, 0, sAxes[1], sAxes[2]);
            FloatBuffer cachedQuadCoords = BufferUtils.createFloatBuffer(mesh.quadCount() * 4);

            float pxAxis = scAxes[1];
            float pzAxis = scAxes[2];

            for (int i = 0; i < mesh.quadCount(); i++) {
                cachedQuadCoords.put(mesh.x1(i) * pxAxis)
                        .put(mesh.y1(i) * pzAxis)
                        .put((mesh.x2(i) + 1) * pxAxis)
                        .put((mesh.y2(i) + 1) * pzAxis);
            }
            cachedQuadCoords.position(0);

            float zLevel;
            zLevel = (context.direction.getFrontOffsetX() + context.direction.getFrontOffsetY() + context.direction.getFrontOffsetZ() > 0
                    ? context.layer + 1 : context.layer) * scAxes[0];

            cache.cachedQuadLevels.add(new CachedQuadLevel<>(zLevel, context.direction, context.t, cachedQuadCoords));
        }

        return cache;
    }

    protected static <T> void addToCache(Map<QuadContext<T>, CoordGrid> cache, Function<Pair<BlockPos, EnumFacing>, T> mapper, EnumFacing direction, BlockPos coord)
    {
        T t = mapper.apply(Pair.of(coord, direction));
        if (t != null) {
            int[] sAxes = getCacheAxes(direction, coord.getX(), coord.getY(), coord.getZ());
            addToCache(cache, new QuadContext<>(sAxes[0], direction, t), sAxes[1], sAxes[2]);
        }
    }

    protected static <T> void addToCache(Map<QuadContext<T>, CoordGrid> cache, QuadContext<T> context, int x, int y)
    {
        CoordGrid quad = cache.get(context);
        if (quad == null)
            cache.put(context, quad = new CoordGrid());
        quad.addCoord(x, y);
    }

    public float[] getSize()
    {
        return size.clone();
    }

    @Override
    public Iterator<CachedQuadLevel<T>> iterator()
    {
        return cachedQuadLevels.iterator();
    }

    public static class QuadContext<T>
    {
        public final int layer;
        public final EnumFacing direction;
        public final T t;

        public QuadContext(int layer, EnumFacing direction, T t)
        {
            this.layer = layer;
            this.direction = direction;
            this.t = t;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            QuadContext that = (QuadContext) o;

            if (layer != that.layer) return false;
            if (direction != that.direction) return false;
            if (!t.equals(that.t)) return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = layer;
            result = 31 * result + direction.hashCode();
            result = 31 * result + t.hashCode();
            return result;
        }
    }

    public static class CoordGrid extends IntArrayList
    {
        public CoordGrid()
        {
        }

        private static boolean isFree(boolean[][] mask, int lX, int hX, int y)
        {
            for (int tX = lX; tX <= hX; tX++)
                if (!mask[tX][y])
                    return false;

            return true;
        }

        public void addCoord(int x, int y)
        {
            add(x);
            add(y);
        }

        public int coordCount()
        {
            return size() / 2;
        }

        public int x(int index)
        {
            return get(index * 2);
        }

        public int y(int index)
        {
            return get(index * 2 + 1);
        }

        public QuadCollection computeMesh(int minX, int minY, int maxX, int maxY)
        {
            boolean[][] mask = new boolean[maxX - minX][maxY - minY];
            QuadCollection collection = new QuadCollection();

            for (int c = 0; c < coordCount(); c++)
                mask[x(c)][y(c)] = true;

            for (int x = minX; x < maxX; x++)
                for (int y = minY; y < maxY; y++) {
                    if (mask[x][y]) {
                        // Expand X
                        int lX = x, hX = x, lY = y, hY = y;
                        while (lX > minX && mask[lX - 1][y])
                            lX--;
                        while (hX < maxX - 1 && mask[hX + 1][y])
                            hX++;

                        // Expand Y
                        while (lY > minY && isFree(mask, lX, hX, lY - 1))
                            lY--;
                        while (hY < maxY - 1 && isFree(mask, lX, hX, hY + 1))
                            hY++;

                        // Fill mask
                        for (int tX = lX; tX <= hX; tX++)
                            for (int tY = lY; tY <= hY; tY++)
                                mask[tX][tY] = false;

                        collection.addQuad(lX, lY, hX, hY);
                    }
                }

            return collection;
        }
    }

    public static class QuadCollection extends IntArrayList
    {
        public QuadCollection()
        {
        }

        public void addQuad(int x1, int y1, int x2, int y2)
        {
            add(x1);
            add(y1);
            add(x2);
            add(y2);
        }

        public int x1(int index)
        {
            return get(index * 4);
        }

        public int x2(int index)
        {
            return get(index * 4 + 2);
        }

        public int y1(int index)
        {
            return get(index * 4 + 1);
        }

        public int y2(int index)
        {
            return get(index * 4 + 3);
        }

        public int quadCount()
        {
            return size() / 4;
        }
    }

    public static class CachedQuadLevel<T>
    {
        public final float zLevel;
        public final EnumFacing direction;
        public final T t;

        public final FloatBuffer quads;

        public CachedQuadLevel(float zLevel, EnumFacing direction, T t, FloatBuffer quads)
        {
            this.zLevel = zLevel;
            this.direction = direction;
            this.t = t;
            this.quads = quads;
        }
    }
}
