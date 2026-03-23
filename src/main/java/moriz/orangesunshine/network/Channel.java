package moriz.orangesunshine.network;

import com.sollace.fabwork.api.packets.HandledPacket;
import com.sollace.fabwork.api.packets.S2CPacketType;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * Server-to-client packets. On Fabric, {@code fabric/.../FabricS2CNetworking} replaces
 * the no-op implementations with real networking during mod initialization.
 */
public final class Channel {
    public static S2CPacketType<MsgDrugProperties> UPDATE_DRUG_PROPERTIES = new NoopS2CPackets<>();
    public static S2CPacketType<MsgHallucinate> HALLUCINATE = new NoopS2CPackets<>();

    private Channel() {
    }

    public static void bootstrap() {
    }

    private static final class NoopS2CPackets<T extends HandledPacket<? super Player>> implements S2CPacketType<T> {
        @Override
        public void sendToPlayer(T packet, ServerPlayer player) {
        }

        @Override
        public void sendToSurroundingPlayers(T packet, Entity entity) {
        }
    }
}
