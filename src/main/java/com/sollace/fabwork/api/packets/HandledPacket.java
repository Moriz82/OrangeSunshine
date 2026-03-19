package com.sollace.fabwork.api.packets;

import net.minecraft.network.FriendlyByteBuf;

public interface HandledPacket<S> {
    void toBuffer(FriendlyByteBuf buffer);

    void handle(S sender);
}
