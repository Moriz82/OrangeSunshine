/*
 * Copyright 2016 Lukas Tenbrink
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

package com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.blocks;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.tools.MCRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.IvToolkit;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import scala.Dynamic;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Map;

/**
 * Created by lukas on 06.05.16.
 */
public class BlockStates
{
    @Nonnull
    public static IBlockState readBlockState(@Nonnull MCRegistry registry, @Nonnull NBTTagCompound compound)
    {
        if (!compound.hasKey("Name", 8)) {
            return Blocks.AIR.getDefaultState();
        }
        else {
            Block block = registry.blockFromID(new ResourceLocation(compound.getString("Name")));
            BlockStateBase iblockstate =(BlockStateBase) block.getDefaultState();

            if (compound.hasKey("Properties", 10)) {
                NBTTagCompound propertyCompound = compound.getCompoundTag ("Properties");
                BlockStateContainer stateContainer = block.getBlockState();

                for (String name : propertyCompound.getKeySet()) {
                    IProperty<?> iproperty = stateContainer.getProperty(name);
                    if (iproperty != null) {
                        iblockstate = setValueHelper(iblockstate, iproperty, name, propertyCompound, compound);
                    }
                }
            }

            return iblockstate;
        }

    }

    private static <S extends BlockStateBase, T extends Comparable<T>> S setValueHelper(S val, IProperty<T> property, String name, NBTTagCompound propertyCompound, NBTTagCompound p_193590_4_)
    {
        Optional<T> optional = property.parseValue(propertyCompound.getString(name));
        if (optional.isPresent()) {
            return (S) (val.withProperty(property, (T) (optional.get())));
        }
        else {
            IvToolkit.logger.warn("Unable to read property: {} with value: {} for blockstate: {}", name, propertyCompound.getString(name), p_193590_4_.toString());
            return val;
        }
    }

    @Nonnull
    public static NBTTagCompound writeBlockState(@Nonnull MCRegistry registry, @Nonnull IBlockState state)
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setString("Name", registry.idFromBlock(state.getBlock()).toString());

        ImmutableMap<IProperty<?>, Comparable<?>> properties = state.getProperties();
        if (!properties.isEmpty()) {
            NBTTagCompound propertyCompound = new NBTTagCompound();

            for (Map.Entry<IProperty<?>, Comparable<?>> entry : properties.entrySet()) {
                IProperty<?> property = entry.getKey();

                propertyCompound.setString(
                        property.getName(),
                        ((IProperty) property).getName(entry.getValue())
                );
            }

            nbttagcompound.setTag("Properties", propertyCompound);
        }

        return nbttagcompound;
    }

    @Nonnull
    public static IBlockState readBlockState(@Nonnull MCRegistry registry, @Nonnull PacketBuffer buf) throws IOException {
        NBTTagCompound tag = buf.readCompoundTag();
        return tag != null
                ? readBlockState(registry, tag)
                : Blocks.AIR.getDefaultState();
    }

    public static void writeBlockState(@Nonnull MCRegistry registry, @Nonnull PacketBuffer buf, @Nonnull IBlockState state)
    {
        buf.writeCompoundTag(writeBlockState(registry, state));
    }

 /*   public static IBlockState fromLegacyMetadata(String blockID, byte metadata)
    {
        int legacyBlockID = Integer.parseInt(blockID);
        Dynamic dynamicNBT = Blck .getFixedNBTForID((legacyBlockID << 4) + metadata & 15);
        NBTTagCompound compound = (NBTTagCompound) dynamicNBT.getValue();
        return NBTUtil.readBlockState(compound);
    }*/
}
