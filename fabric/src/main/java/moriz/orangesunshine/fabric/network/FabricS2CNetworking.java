package moriz.orangesunshine.fabric.network;

import java.util.Optional;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.hallucination.AbstractEntityHallucination;
import com.sollace.fabwork.api.packets.S2CPacketType;

import moriz.orangesunshine.network.MsgDrugProperties;
import moriz.orangesunshine.network.MsgHallucinate;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
/**
 * Wires {@link moriz.orangesunshine.network.Channel} S2C packets to Fabric's payload API.
 * The previous {@code SimpleNetworking} stub never sent packets, so client {@link DrugProperties}
 * stayed empty and drug visuals / client-side logic did not run.
 */
public final class FabricS2CNetworking {
    private FabricS2CNetworking() {
    }

    public static void registerPayloadTypes() {
        PayloadTypeRegistry.playS2C().register(DrugPropertiesSyncPayload.TYPE, DrugPropertiesSyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(HallucinatePayload.TYPE, HallucinatePayload.CODEC);
    }

    public static void registerClientReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(DrugPropertiesSyncPayload.TYPE, (payload, context) -> {
            if (context.client().level == null) {
                return;
            }
            Entity entity = context.client().level.getEntity(payload.entityId());
            if (entity == null) {
                OrangeSunshine.LOGGER.warn("[OrangeSunshine] S2C DrugSync: entity {} not found in client level", payload.entityId());
                return;
            }
            DrugProperties.of(entity).ifPresent(properties -> {
                OrangeSunshine.LOGGER.info("[OrangeSunshine] S2C DrugSync received for entity {} — applying NBT ({} keys)",
                        payload.entityId(), payload.compound().size());
                properties.fromNbt(payload.compound());
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(HallucinatePayload.TYPE, (payload, context) -> {
            if (context.client().level == null) {
                return;
            }
            Entity entity = context.client().level.getEntity(payload.entityId());
            DrugProperties.of(entity).ifPresent(properties -> {
                if (properties.getHallucinations().getEntities().addHallucination(payload.hallucinationId(), true) instanceof AbstractEntityHallucination e) {
                    payload.position().map(BlockPos::getCenter).ifPresent(e.getEntity()::setPos);
                }
            });
        });
    }

    public static void bindChannelPackets() {
        moriz.orangesunshine.network.Channel.UPDATE_DRUG_PROPERTIES = new DrugPropertiesChannel();
        moriz.orangesunshine.network.Channel.HALLUCINATE = new HallucinateChannel();
    }

    private static final class DrugPropertiesChannel implements S2CPacketType<MsgDrugProperties> {
        @Override
        public void sendToPlayer(MsgDrugProperties packet, ServerPlayer player) {
            ServerPlayNetworking.send(player, new DrugPropertiesSyncPayload(packet.entityId(), packet.compound()));
        }

        @Override
        public void sendToSurroundingPlayers(MsgDrugProperties packet, Entity entity) {
            if (!(entity.level() instanceof ServerLevel)) {
                return;
            }
            DrugPropertiesSyncPayload payload = new DrugPropertiesSyncPayload(packet.entityId(), packet.compound());
            for (ServerPlayer p : PlayerLookup.tracking(entity)) {
                ServerPlayNetworking.send(p, payload);
            }
        }
    }

    private static final class HallucinateChannel implements S2CPacketType<MsgHallucinate> {
        @Override
        public void sendToPlayer(MsgHallucinate packet, ServerPlayer player) {
            ServerPlayNetworking.send(player, new HallucinatePayload(packet.entityId(), packet.type(), packet.position()));
        }

        @Override
        public void sendToSurroundingPlayers(MsgHallucinate packet, Entity entity) {
            HallucinatePayload payload = new HallucinatePayload(packet.entityId(), packet.type(), packet.position());
            for (ServerPlayer p : PlayerLookup.tracking(entity)) {
                ServerPlayNetworking.send(p, payload);
            }
        }
    }

    private record DrugPropertiesSyncPayload(int entityId, CompoundTag compound) implements CustomPacketPayload {
        static final CustomPacketPayload.Type<DrugPropertiesSyncPayload> TYPE = new CustomPacketPayload.Type<>(OrangeSunshine.id("update_drug_properties"));
        static final StreamCodec<RegistryFriendlyByteBuf, DrugPropertiesSyncPayload> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                DrugPropertiesSyncPayload::entityId,
                ByteBufCodecs.COMPOUND_TAG,
                DrugPropertiesSyncPayload::compound,
                DrugPropertiesSyncPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private record HallucinatePayload(int entityId, Identifier hallucinationId, Optional<BlockPos> position) implements CustomPacketPayload {
        static final CustomPacketPayload.Type<HallucinatePayload> TYPE = new CustomPacketPayload.Type<>(OrangeSunshine.id("hallucinate"));
        static final StreamCodec<RegistryFriendlyByteBuf, HallucinatePayload> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                HallucinatePayload::entityId,
                Identifier.STREAM_CODEC,
                HallucinatePayload::hallucinationId,
                ByteBufCodecs.optional(BlockPos.STREAM_CODEC),
                HallucinatePayload::position,
                HallucinatePayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
