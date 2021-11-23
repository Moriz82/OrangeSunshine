/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.network.PacketEntityData;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.network.PartialUpdateHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class PSNetworkHelperServer
{
    public static void  sendEEPUpdatePacketToPlayer(Entity entity, String eepKey, String context, SimpleNetworkWrapper network, EntityPlayer player, Object... params)
    {
        if (!(player instanceof EntityPlayerMP))
            throw new UnsupportedOperationException();

        try{
            network.sendTo((IMessage) PacketEntityData.packetEntityData(entity, eepKey, context, params), (EntityPlayerMP) player);
        }catch (Exception ignore){}
    }

    public static void sendEEPUpdatePacket(Entity entity, String eepKey, String context, SimpleNetworkWrapper network, Object... params)
    {
        if (entity.world.isRemote)
            throw new UnsupportedOperationException();

        for (EntityPlayer player : ((WorldServer) entity.world).getEntityTracker().getTrackingPlayers(entity))
            sendEEPUpdatePacketToPlayer(entity, eepKey, context, network, player, params);

        if (entity instanceof EntityPlayer) // Players don't 'track' themselves
            sendEEPUpdatePacketToPlayer(entity, eepKey, context, network, (EntityPlayer) entity, params);
    }
}
