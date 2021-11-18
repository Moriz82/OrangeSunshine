package com.BrotherHoodOfDiethylamide.OrangeSunshine.network;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.IPlayerDrugs;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.PlayerProperties;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class ActiveDrugCapSync implements IMessage {
    private final Map<Drug, Float> map;

    public ActiveDrugCapSync(Map<Drug, Float> activeDrugs) {
        map = activeDrugs;
    }

    public ActiveDrugCapSync(PacketBuffer buffer) {
        map = new HashMap<>();

        int mapSize = buffer.readInt();
        for (int i = 0; i < mapSize; i++) {
            map.put(DrugRegistry.DRUGS.getValue(buffer.readResourceLocation()), buffer.readFloat());
        }
    }

    @Override
    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeInt(map.size());

        map.forEach((drug, effect) -> {
            packetBuffer.writeResourceLocation(Objects.requireNonNull(DrugRegistry.DRUGS.getKey(drug)));
            packetBuffer.writeFloat(effect);
        });
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player == null) return;
            IPlayerDrugs playerDrugs = PlayerProperties.getPlayerDrugs(player);
            playerDrugs.setActives(map);
        });
        ctx.get().setPacketHandled(true);
    }
}
