/*

package com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SchedulingMessageHandler
{
    public static <MSG> BiConsumer<MSG, Supplier<FMLNetworkEvent.>> wrap(BiConsumer<MSG, NetworkEvent.Context> handler)
    {
        return (msg, contextSupplier) -> {
            NetworkEvent.Context context = contextSupplier.get();
            schedule(context, () -> handler.accept(msg, context));
        };
    }

    public static void schedule(NetworkEvent.Context context, Runnable runnable)
    {
        if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            scheduleClient(runnable);
        }
        else {
            scheduleServer(context, runnable);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void scheduleClient(Runnable runnable)
    {
        Minecraft.getInstance().addScheduledTask(runnable);
    }

    public static void scheduleServer(NetworkEvent.Context context, Runnable runnable)
    {
        WorldServer world = context.getSender().getServerWorld();
        world.addScheduledTask(runnable);
    }
}*/
