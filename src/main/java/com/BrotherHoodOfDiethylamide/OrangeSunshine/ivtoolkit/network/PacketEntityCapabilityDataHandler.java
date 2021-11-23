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

package com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.network;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.tools.IvSideClient;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

/**
 * Created by lukas on 02.07.14.
 */
public class PacketEntityCapabilityDataHandler
{
    /*public static void handle(PacketEntityCapabilityData packet, Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();

        SchedulingMessageHandler.schedule(context, () -> handleClient(packet, context));
    }

    @SideOnly(Side.CLIENT)
    protected static <T> void handleClient(PacketEntityCapabilityData message, NetworkEvent.Context context)
    {
        World world = IvSideClient.getClientWorld();
        Entity entity = world.getEntityByID(message.getEntityID());

        if (entity != null) {
            Capability<T> capability = CapabilityUpdateRegistry.INSTANCE.capability(message.getCapabilityKey());
            T t = entity.getCapability(capability, message.getDirection()).orElse(null);

            if (t instanceof PartialUpdateHandler)
                ((PartialUpdateHandler) t).readUpdateData(message.getPayload(), message.getContext());
        }
    }*/
}
