package moriz.orangesunshine.util;

import net.minecraft.nbt.CompoundTag;

public interface NbtSerialisable {

    default CompoundTag toNbt() {
        CompoundTag tagCompound = new CompoundTag();
        toNbt(tagCompound);
        return tagCompound;
    }

    void toNbt(CompoundTag compound);

    void fromNbt(CompoundTag compound);
}
