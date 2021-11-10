package com.BrotherHoodOfDiethylamide.OrangeSunshine.entities;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.utils.RegistryHandler;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import javax.annotation.Nullable;

public class EntityShmokeStackz extends EntityVillager
{
    public EntityShmokeStackz(World worldIn)
    {
        super(worldIn);
        this.setSize(0.9F, 2.8F);
        this.setProfession(RegistryHandler.shmokestackz);
    }

    @Override
    public VillagerRegistry.VillagerProfession getProfessionForge(){
        return RegistryHandler.shmokestackz;
    }

    @Override
    public IEntityLivingData finalizeMobSpawn(DifficultyInstance p_190672_1_, @Nullable IEntityLivingData p_190672_2_, boolean p_190672_3_) {
        super.finalizeMobSpawn(p_190672_1_, p_190672_2_, p_190672_3_);
        p_190672_2_ = super.onInitialSpawn(p_190672_1_, p_190672_2_);
        this.setProfession(RegistryHandler.shmokestackz);
        return p_190672_2_;
    }

    @Override
    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIVillagerMate(this));
        this.tasks.addTask(2, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1000.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
    }

    @Override
    public float getEyeHeight()
    {
        return 2.6F;
    }

    @Override
    public EntityShmokeStackz createChild(EntityAgeable ageable)
    {
        return new EntityShmokeStackz(world);
    }

   /* @Override
    protected ResourceLocation getLootTable()
    {
        return null;
        //return LootTableHandler.CENTAUR;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return null;
        //return SoundsHandler.ENTITY_CENTAUR_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source)
    {
        return null;
        //return SoundsHandler.ENTITY_CENTAUR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return null;
       // return SoundsHandler.ENTITY_CENTAUR_DEATH;
    }*/
}