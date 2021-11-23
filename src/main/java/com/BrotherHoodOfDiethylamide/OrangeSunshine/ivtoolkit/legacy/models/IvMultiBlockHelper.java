/*
 * Copyright 2019 Lukas Tenbrink
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

package com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.models;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.blocks.BlockPositions;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.blocks.IvTileEntityMultiBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IvMultiBlockHelper implements Iterable<BlockPos>
{
    private Iterator<BlockPos> iterator;
    private List<BlockPos> childLocations;
    private IvTileEntityMultiBlock parentTileEntity = null;

    private World world;
    private IBlockState blockState;

    private EnumFacing direction;
    private double[] center;
    private double[] size;

    public IvMultiBlockHelper()
    {

    }

    public boolean beginPlacing(List<BlockPos> positions, World world, BlockPos pos, EnumFacing blockSide, ItemStack itemStack, EntityPlayer player, IBlockState state, EnumFacing direction)
    {
        List<BlockPos> validLocations = IvMultiBlockHelper.getBestPlacement(positions, world, pos, blockSide, itemStack, player, state);

        if (validLocations == null)
            return false;

        return beginPlacing(validLocations, world, state, direction);
    }

    public boolean beginPlacing(List<BlockPos> validLocations, World world, IBlockState state, EnumFacing direction)
    {
        this.world = world;
        this.parentTileEntity = null;
        this.direction = direction;

        this.blockState = state;
        this.center = IvMultiBlockHelper.getTileEntityCenter(validLocations);
        this.size = IvMultiBlockHelper.getTileEntitySize(validLocations);
        this.childLocations = validLocations;

        this.iterator = validLocations.iterator();

        return true;
    }

    @Override
    public Iterator<BlockPos> iterator()
    {
        return iterator;
    }

    public IvTileEntityMultiBlock placeBlock(BlockPos pos)
    {
        return placeBlock(pos, this.parentTileEntity == null);
    }

    private IvTileEntityMultiBlock placeBlock(BlockPos pos, boolean parent)
    {
        world.setBlockState(pos, blockState, 3);
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof IvTileEntityMultiBlock)
        {
            IvTileEntityMultiBlock tileEntityMB = (IvTileEntityMultiBlock) tileEntity;

            if (parent)
            {
                parentTileEntity = tileEntityMB;
                childLocations.remove(parentTileEntity.getPos());
                parentTileEntity.becomeParent(childLocations);
            }
            else
            {
                tileEntityMB.becomeChild(parentTileEntity);
            }

            tileEntityMB.facing = direction;
            tileEntityMB.centerCoords = Arrays.asList(new Double[]{center[0] - pos.getX(), center[1] - pos.getY(), center[2] - pos.getZ()});
            tileEntityMB.centerCoordsSize = Arrays.asList(toArr(size));

            return tileEntityMB;
        }

        return null;
    }

    private Double[] toArr(double[] arr){
        Double[] a = new Double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            a[i] = arr[i];
        }
        return a;
    }

    public static double[] getTileEntityCenter(List<BlockPos> positions)
    {
        double[] result = getCenter(positions);

        return new double[]{result[0] + 0.5f, result[1] + 0.5f, result[2] + 0.5f};
    }

    public static double[] getTileEntitySize(List<BlockPos> positions)
    {
        return getSize(positions);
    }

    public static double[] getCenter(List<BlockPos> positions)
    {
        if (positions.size() > 0)
        {
            BlockPos min = BlockPositions.getLowerCorner(positions);
            BlockPos max = BlockPositions.getHigherCorner(positions);

            double[] result = new double[3];
            result[0] = (min.getX() + max.getX()) * 0.5;
            result[1] = (min.getY() + max.getY()) * 0.5;
            result[2] = (min.getZ() + max.getZ()) * 0.5;

            return result;
        }

        return null;
    }

    public static double[] getSize(List<BlockPos> positions)
    {
        if (positions.size() > 0)
        {
            BlockPos min = BlockPositions.getLowerCorner(positions);
            BlockPos max = BlockPositions.getHigherCorner(positions);

            double[] result = new double[3];
            result[0] = (min.getX() - max.getX() + 1) * 0.5;
            result[1] = (min.getY() - max.getY() + 1) * 0.5;
            result[2] = (min.getZ() - max.getZ() + 1) * 0.5;

            return result;
        }

        return null;
    }

    public static int[] getLengths(List<BlockPos> positions)
    {
        BlockPos min = BlockPositions.getLowerCorner(positions);
        BlockPos max = BlockPositions.getHigherCorner(positions);

        return new int[]{max.getX() - min.getX(), max.getY() - min.getY(), max.getZ() - min.getZ()};
    }

    @Deprecated
    public static boolean canPlace(World world, IBlockState state, List<BlockPos> positions, Entity entity, ItemStack stack)
    {
        return canPlace(world, state, positions, entity);
    }

    public static boolean canPlace(World world, IBlockState state, List<BlockPos> positions, Entity entity)
    {
        for (BlockPos position : positions)
        {
            if (!world.mayPlace(state.getBlock(), position, false, EnumFacing.DOWN, entity))
                return false;
        }

        return true;
    }

    public static List<List<BlockPos>> getValidPlacements(List<BlockPos> positions, World world, BlockPos pos, EnumFacing side, ItemStack itemStack, EntityPlayer player, IBlockState state)
    {
        IBlockState var11 = world.getBlockState(pos);

        IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (block == Blocks.SNOW_LAYER && ((Integer)iblockstate.getValue(BlockSnow.LAYERS)).intValue() < 1)
        {
            side = EnumFacing.UP;
        }
        else if (!block.isReplaceable(world, pos))
        {
            pos = pos.offset(side);
        }

        if (!player.canPlayerEdit(pos, side, itemStack))
        {
            return new ArrayList<>();
        }
        else if (pos.getY() == world.getHeight() && state.getMaterial().isSolid())
        {
            return new ArrayList<>();
        }
        else
        {
            int[] lengths = getLengths(positions);
            BlockPos min = BlockPositions.getLowerCorner(positions);

            // Run from min+length (maximimum) being the placed pos to minimum being the pos
            ArrayList<List<BlockPos>> validPlacements = new ArrayList<>();
            for (int xShift = min.getX() - lengths[0]; xShift <= min.getX(); xShift++)
            {
                for (int yShift = min.getY() - lengths[1]; yShift <= min.getY(); yShift++)
                {
                    for (int zShift = min.getZ() - lengths[2]; zShift <= min.getZ(); zShift++)
                    {
                        ArrayList<BlockPos> validPositions = new ArrayList<>();

                        for (BlockPos position : positions)
                            validPositions.add(position.add(xShift, yShift, zShift).add(pos));

                        if (canPlace(world, state, validPositions, null, itemStack))
                            validPlacements.add(validPositions);
                    }
                }
            }

            return validPlacements;
        }
    }

    public static List<BlockPos> getBestPlacement(List<BlockPos> positions, World world, BlockPos pos, EnumFacing side, ItemStack itemStack, EntityPlayer player, IBlockState state)
    {
        int[] lengths = getLengths(positions);

        List<List<BlockPos>> validPlacements = getValidPlacements(positions, world, pos, side, itemStack, player, state);

        if (validPlacements.size() > 0)
        {
            float[] center = new float[]{pos.getX() - lengths[0] * 0.5f, pos.getY() - lengths[1] * 0.5f, pos.getZ() - lengths[2] * 0.5f};
            List<BlockPos> preferredPositions = validPlacements.get(0);
            for (int i = 1; i < validPlacements.size(); i++)
            {
                BlockPos referenceBlock = validPlacements.get(i).get(0);
                BlockPos referenceBlockOriginal = preferredPositions.get(0);

                if (distanceSquared(BlockPositions.toIntArray(referenceBlock), center) < distanceSquared(BlockPositions.toIntArray(referenceBlockOriginal), center))
                    preferredPositions = validPlacements.get(i);
            }

            return preferredPositions;
        }

        return null;
    }

    private static float distanceSquared(int[] referenceBlock, float[] center)
    {
        float distX = referenceBlock[0] - center[0];
        float distY = referenceBlock[1] - center[1];
        float distZ = referenceBlock[2] - center[2];

        return distX * distX + distY * distY + distZ * distZ;
    }
}
