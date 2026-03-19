package moriz.orangesunshine.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class SyncedBlockEntity extends BlockEntity {

    protected SyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public final Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public final CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag compound = super.getUpdateTag(registries);
        CompoundTag data = new CompoundTag();
        writeNbt(data);
        if (!data.isEmpty()) {
            compound.store("data", CompoundTag.CODEC, data);
        }
        return compound;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        CompoundTag data = new CompoundTag();
        writeNbt(data);
        if (!data.isEmpty()) {
            output.store("data", CompoundTag.CODEC, data);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.read("data", CompoundTag.CODEC).ifPresent(this::readNbt);
    }

    protected void writeNbt(CompoundTag compound) {
    }

    protected void readNbt(CompoundTag compound) {
    }
}
