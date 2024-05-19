package com.orangesunshine.moriz.network;

import com.mojang.brigadier.Message;
import com.orangesunshine.moriz.OrangeSunshine;
import com.orangesunshine.moriz.capabilities.IPlayerDrugs;
import com.orangesunshine.moriz.capabilities.PlayerDrugs;
import com.orangesunshine.moriz.capabilities.PlayerProperties;
import com.orangesunshine.moriz.drugs.Drug;
import com.orangesunshine.moriz.drugs.DrugRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActiveDrugCapSync {
    private final Map<Drug, Float> map;

    public ActiveDrugCapSync(Map<Drug, Float> activeDrugs) {
        map = activeDrugs;
    }

    public ActiveDrugCapSync(FriendlyByteBuf buffer) {
        map = new HashMap<>();

        int mapSize = buffer.readInt();
        for (int i = 0; i < mapSize; i++) {
            Drug drug = null;
            for (RegistryObject<Drug> e : OrangeSunshine.DRUGS.getEntries()) {
                if (e.getId().equals(buffer.readResourceLocation())) {
                    drug = e.get();
                    break;
                }
            }
            map.put(drug, buffer.readFloat());
        }
    }

    public void encode(FriendlyByteBuf packetBuffer) {
        packetBuffer.writeInt(map.size());

        map.forEach((drug, effect) -> {

            ResourceLocation name = new ResourceLocation("");
            for (RegistryObject<Drug> e : OrangeSunshine.DRUGS.getEntries()) {
                if (e.get().equals(drug)) {
                    name = e.getId();
                    break;
                }
            }
            packetBuffer.writeResourceLocation(name);
            packetBuffer.writeFloat(effect);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            IPlayerDrugs playerDrugs = PlayerProperties.getPlayerDrugs(player);
            playerDrugs.setActives(map);
        });
        ctx.setPacketHandled(true);
    }
}
