package com.BrotherHoodOfDiethylamide.OrangeSunshine.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface IMessage {
    void encode(PacketBuffer buffer);
    void handle(Supplier<NetworkEvent.Context> supplier);
}
