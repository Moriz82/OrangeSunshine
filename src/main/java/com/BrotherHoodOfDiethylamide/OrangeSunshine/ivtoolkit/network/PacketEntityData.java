/*
 * Copyright 2014 Lukas Tenbrink
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * Created by lukas on 01.07.14.
 */
public class PacketEntityData implements IvPacket
{
    private int entityID;
    private String context;
    private ByteBuf payload;

    public PacketEntityData()
    {
    }

    public PacketEntityData(int entityID, String context, ByteBuf payload)
    {
        this.entityID = entityID;
        this.context = context;
        this.payload = payload;
    }

    public static <UEntity extends Entity & PartialUpdateHandler> PacketEntityData packetEntityData(Entity entity, String context, Object... params)
    {
        UEntity uEntity = (UEntity) entity;
        ByteBuf buf = Unpooled.buffer();
        uEntity.writeUpdateData(buf, context, params);
        return new PacketEntityData(entity.getEntityId(), context, buf);
    }

    public int getEntityID()
    {
        return entityID;
    }

    public void setEntityID(int entityID)
    {
        this.entityID = entityID;
    }

    public String getContext()
    {
        return context;
    }

    public void setContext(String context)
    {
        this.context = context;
    }

    public ByteBuf getPayload()
    {
        return payload;
    }

    public void setPayload(ByteBuf payload)
    {
        this.payload = payload;
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        entityID = buf.readInt();
        context = buf.readString(1000);
        payload = IvPacketHelper.readByteBuffer(buf);
    }

    @Override
    public void decode(PacketBuffer buf)
    {
        buf.writeInt(entityID);
        buf.writeString(context);
        IvPacketHelper.writeByteBuffer(buf, payload);
    }

}
