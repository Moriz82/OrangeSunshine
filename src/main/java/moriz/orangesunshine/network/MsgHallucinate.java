package moriz.orangesunshine.network;

import java.util.Optional;

import com.sollace.fabwork.api.packets.HandledPacket;

import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.hallucination.AbstractEntityHallucination;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public record MsgHallucinate (
        int entityId,
        Identifier type,
        Optional<BlockPos> position
    ) implements HandledPacket<Player> {

    public MsgHallucinate(FriendlyByteBuf buffer) {
        this(buffer.readVarInt(), buffer.readIdentifier(), buffer.readOptional(buf -> buf.readBlockPos()));
    }

    @Override
    public void toBuffer(FriendlyByteBuf buffer) {
        buffer.writeVarInt(entityId);
        buffer.writeIdentifier(type);
        buffer.writeOptional(position, (buf, pos) -> buf.writeBlockPos(pos));
    }

    @Override
    public void handle(Player sender) {
        DrugProperties.of(sender.level().getEntity(entityId)).ifPresent(properties -> {
            if (properties.getHallucinations().getEntities().addHallucination(type, true) instanceof AbstractEntityHallucination e) {
                position.map(BlockPos::getCenter).ifPresent(e.getEntity()::setPos);
            }
        });
    }
}
