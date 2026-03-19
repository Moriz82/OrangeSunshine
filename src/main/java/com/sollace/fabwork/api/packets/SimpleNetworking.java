package com.sollace.fabwork.api.packets;

import java.util.function.Function;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class SimpleNetworking {
    private SimpleNetworking() {
    }

    public static <T extends HandledPacket<? super Player>> S2CPacketType<T> serverToClient(Identifier id, Function<FriendlyByteBuf, T> decoder) {
        return new S2CPacketType<>() {
            @Override
            public void sendToPlayer(T packet, ServerPlayer player) {
            }

            @Override
            public void sendToSurroundingPlayers(T packet, Entity entity) {
            }

            @Override
            public String toString() {
                return "S2CPacketType[" + id + "]";
            }
        };
    }
}
