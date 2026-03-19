package com.sollace.fabwork.api.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface S2CPacketType<T extends HandledPacket<? super Player>> {
    void sendToPlayer(T packet, ServerPlayer player);

    void sendToSurroundingPlayers(T packet, Entity entity);
}
