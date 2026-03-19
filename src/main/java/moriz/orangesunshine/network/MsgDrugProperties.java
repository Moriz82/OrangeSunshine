package moriz.orangesunshine.network;

import com.sollace.fabwork.api.packets.HandledPacket;

import moriz.orangesunshine.entity.drug.DrugProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public record MsgDrugProperties (
        int entityId,
        CompoundTag compound
    ) implements HandledPacket<Player> {

    public MsgDrugProperties(DrugProperties properties) {
        this(properties.asEntity().getId(), properties.toNbt());
    }

    public MsgDrugProperties(FriendlyByteBuf buffer) {
        this(buffer.readVarInt(), buffer.readNbt());
    }

    @Override
    public void toBuffer(FriendlyByteBuf buffer) {
        buffer.writeVarInt(entityId);
        buffer.writeNbt(compound);
    }

    @Override
    public void handle(Player sender) {
        DrugProperties.of(sender.level().getEntity(entityId)).ifPresent(properties -> properties.fromNbt(compound));
    }
}
