package com.BrotherHoodOfDiethylamide.OrangeSunshine.network;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.IPlayerDrugs;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.PlayerProperties;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugInstance;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class DrugCapSync implements IMessage {
    private final List<DrugInstance> list;

    public DrugCapSync(List<DrugInstance> drugInstances) {
        list = drugInstances;
    }

    public DrugCapSync(PacketBuffer buffer) {
        int listSize = buffer.readInt();
        list = new ArrayList<>(listSize);

        for (int i = 0; i < listSize; i++) {
            list.add(new DrugInstance(DrugRegistry.DRUGS.getValue(buffer.readResourceLocation()), buffer.readInt(), buffer.readFloat(), buffer.readInt(), buffer.readInt()));
        }
    }

    @Override
    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeInt(list.size());

        for (DrugInstance drugInstance : list) {
            packetBuffer.writeResourceLocation(Objects.requireNonNull(DrugRegistry.DRUGS.getKey(drugInstance.getDrug())));
            packetBuffer.writeInt(drugInstance.getDelayTime());
            packetBuffer.writeFloat(drugInstance.getPotency());
            packetBuffer.writeInt(drugInstance.getDuration());
            packetBuffer.writeInt(drugInstance.getTimeActive());
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player == null) return;
            IPlayerDrugs playerDrugs = PlayerProperties.getPlayerDrugs(player);
            playerDrugs.setSources(list);
        });
        ctx.get().setPacketHandled(true);
    }
}
