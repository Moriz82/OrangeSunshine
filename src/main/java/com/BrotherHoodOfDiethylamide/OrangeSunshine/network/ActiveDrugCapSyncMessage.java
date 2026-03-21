package com.BrotherHoodOfDiethylamide.OrangeSunshine.network;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.IPlayerDrugs;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.PlayerProperties;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

public class ActiveDrugCapSyncMessage implements IMessage {
    private Map<Drug, Float> map;

    public ActiveDrugCapSyncMessage() {
        map = new HashMap<>();
    }

    public ActiveDrugCapSyncMessage(Map<Drug, Float> activeDrugs) {
        this.map = activeDrugs;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int mapSize = buf.readInt();
        map = new HashMap<>();
        for (int i = 0; i < mapSize; i++) {
            String drugName = ByteBufUtils.readUTF8String(buf);
            Drug drug = Drug.byName(drugName);
            float effect = buf.readFloat();
            if (drug != null) {
                map.put(drug, effect);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(map.size());
        map.forEach((drug, effect) -> {
            ByteBufUtils.writeUTF8String(buf, Drug.toName(drug));
            buf.writeFloat(effect);
        });
    }

    public static class Handler implements IMessageHandler<ActiveDrugCapSyncMessage, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(ActiveDrugCapSyncMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                if (player == null) return;
                IPlayerDrugs playerDrugs = PlayerProperties.getPlayerDrugs(player);
                playerDrugs.setActives(message.map);
            });
            return null;
        }
    }
}
