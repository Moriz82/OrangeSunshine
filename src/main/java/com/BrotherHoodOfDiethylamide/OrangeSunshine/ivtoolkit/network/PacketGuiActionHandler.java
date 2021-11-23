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
import net.minecraft.inventory.Container;

import java.util.function.Supplier;

/**
 * Created by lukas on 02.07.14.
 */
public class PacketGuiActionHandler
{
    /*public static void handle(PacketGuiAction packet, Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();

        SchedulingMessageHandler.schedule(context, () -> handleServer(packet, context));
    }

    protected static <T> void handleServer(ivorius.ivtoolkit.network.PacketGuiAction message, NetworkEvent.Context context)
    {
        Container container = context.getSender().openContainer;
        
        if (container instanceof ivorius.ivtoolkit.network.PacketGuiAction.ActionHandler) {
            ((PacketGuiAction.ActionHandler) container).handleAction(message.getContext(), message.getPayload());
        }
    }*/
}
