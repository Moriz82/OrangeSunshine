/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;

/**
 * Created by lukas on 25.03.14.
 */
public class EntityFakeSun extends Entity
{
    public Entity prevViewEntity;

    public EntityFakeSun(World par1World)
    {
        super(par1World);
    }

    @Override
    protected void entityInit() {

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {

    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return null;
    }


    @Override
    public void setItemStackToSlot(EntityEquipmentSlot entityEquipmentSlot, ItemStack itemStack) {

    }

    public EntityFakeSun(Entity prevViewEntity)
    {
        super(prevViewEntity.world);

        this.prevViewEntity = prevViewEntity;

        NBTTagCompound cmp = new NBTTagCompound();
        prevViewEntity.writeToNBT(cmp);
        readFromNBT(cmp);
    }
}
