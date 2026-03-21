package com.BrotherHoodOfDiethylamide.OrangeSunshine.network;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.IPlayerDrugs;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.PlayerProperties;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugInstance;
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

import java.util.ArrayList;
import java.util.List;

public class DrugCapSyncMessage implements IMessage {
    private List<DrugInstance> list;

    public DrugCapSyncMessage() {
        list = new ArrayList<>();
    }

    public DrugCapSyncMessage(List<DrugInstance> drugInstances) {
        this.list = drugInstances;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int listSize = buf.readInt();
        list = new ArrayList<>(listSize);
        for (int i = 0; i < listSize; i++) {
            String drugName = ByteBufUtils.readUTF8String(buf);
            Drug drug = Drug.byName(drugName);
            if (drug == null) continue;
            int delay = buf.readInt();
            float potency = buf.readFloat();
            int duration = buf.readInt();
            int timeActive = buf.readInt();
            list.add(new DrugInstance(drug, delay, potency, duration, timeActive));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(list.size());
        for (DrugInstance drugInstance : list) {
            ByteBufUtils.writeUTF8String(buf, drugInstance.toName());
            buf.writeInt(drugInstance.getDelayTime());
            buf.writeFloat(drugInstance.getPotency());
            buf.writeInt(drugInstance.getDuration());
            buf.writeInt(drugInstance.getTimeActive());
        }
    }

    public static class Handler implements IMessageHandler<DrugCapSyncMessage, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(DrugCapSyncMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                if (player == null) return;
                IPlayerDrugs playerDrugs = PlayerProperties.getPlayerDrugs(player);
                playerDrugs.setSources(message.list);
            });
            return null;
        }
    }
}
