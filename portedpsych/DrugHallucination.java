package com.BrotherHoodOfDiethylamide.OrangeSunshine.effects.portedpsych;

import ivorius.ivtoolkit.logic.IvChatBot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

public abstract class DrugHallucination
{
    public EntityPlayer playerEntity;

    public int entityTicksAlive;

    public IvChatBot chatBot;

    public DrugHallucination(EntityPlayer playerEntity)
    {
        this.playerEntity = playerEntity;
    }

    public void update()
    {
        entityTicksAlive++;

        if (this.chatBot != null)
        {
            String sendString = this.chatBot.update();

            if (sendString != null)
            {
                playerEntity.sendMessage((new TextComponentString(sendString)));
            }
        }
    }

    public void receiveChatMessage(String message, EntityLivingBase entity)
    {
        if (this.chatBot != null)
        {
            this.chatBot.receiveChatMessage(message);
        }
    }

    public abstract void render(float par1, float dAlpha);

    public abstract boolean isDead();

    public abstract int getMaxHallucinations();
}
