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

package com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.blocks.IvRotatableBlockRenderHelper;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.blocks.IvTileEntityMultiBlock;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Created by lukas on 23.04.14.
 */
public class IvMultiBlockRenderHelper
{
    public static void transformFor(IvTileEntityMultiBlock tileEntity, double renderX, double renderY, double renderZ)
    {
        double[] center = tileEntity.getActiveCenterCoords();

        GlStateManager.translate(renderX + center[0] - tileEntity.getPos().getX(), renderY + center[1] - tileEntity.getPos().getY(), renderZ + center[2] - tileEntity.getPos().getZ());
        GlStateManager.rotate(IvRotatableBlockRenderHelper.getAngleFromSouth(tileEntity.getFacing()), 0.0f, 1.0f, 0.0f);
    }
}
