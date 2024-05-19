package com.orangesunshine.moriz.network;

import com.orangesunshine.moriz.OrangeSunshine;
import com.orangesunshine.moriz.capabilities.IPlayerDrugs;
import com.orangesunshine.moriz.capabilities.PlayerProperties;
import com.orangesunshine.moriz.drugs.Drug;
import com.orangesunshine.moriz.drugs.DrugInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class DrugCapSync {
    private final List<DrugInstance> list;

    public DrugCapSync(List<DrugInstance> drugInstances) {
        list = drugInstances;
    }

    public DrugCapSync(FriendlyByteBuf buffer) {
        int listSize = buffer.readInt();
        list = new ArrayList<>(listSize);

        for (int i = 0; i < listSize; i++) {
            Drug drug = null;
            for (RegistryObject<Drug> e : OrangeSunshine.DRUGS.getEntries()) {
                if (e.getId().equals(buffer.readResourceLocation())) {
                    drug = e.get();
                    break;
                }
            }
            list.add(new DrugInstance(drug, buffer.readInt(), buffer.readFloat(), buffer.readInt(), buffer.readInt()));
        }
    }

    public void encode(FriendlyByteBuf packetBuffer) {
        packetBuffer.writeInt(list.size());

        for (DrugInstance drugInstance : list) {

            ResourceLocation name = new ResourceLocation("");
            for (RegistryObject<Drug> e : OrangeSunshine.DRUGS.getEntries()) {
                if (e.get().equals(drugInstance.getDrug())) {
                    name = e.getId();
                    break;
                }
            }

            packetBuffer.writeResourceLocation(Objects.requireNonNull(name));
            packetBuffer.writeInt(drugInstance.getDelayTime());
            packetBuffer.writeFloat(drugInstance.getPotency());
            packetBuffer.writeInt(drugInstance.getDuration());
            packetBuffer.writeInt(drugInstance.getTimeActive());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            IPlayerDrugs playerDrugs = PlayerProperties.getPlayerDrugs(player);
            playerDrugs.setSources(list);
        });
        ctx.setPacketHandled(true);
    }
}
