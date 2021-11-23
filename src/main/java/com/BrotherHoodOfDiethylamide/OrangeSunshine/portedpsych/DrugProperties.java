package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.network.PartialUpdateHandler;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.IDrugRenderer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.apache.commons.lang3.tuple.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;

    public class DrugProperties implements EntityProperty, PartialUpdateHandler
{
    public static final UUID drugUUID = UUID.fromString("2da054e7-0fe0-4fb4-bf2c-a185a5f72aa1"); // Randomly gen'd
    public static final String EEP_KEY = "DrugHelper";
    public static final String EEP_CMP_KEY = "drugData";
    public static HashMap<EntityPlayer, DrugProperties> ExtendedProp = new HashMap<>();

    public static boolean waterOverlayEnabled;
    public static boolean hurtOverlayEnabled;
    public static float[] digitalEffectPixelRescale;

    public static Map<String, Drug> drugs = new HashMap<>();
    public List<DrugInfluence> influences;

    public boolean hasChanges;

    public IDrugRenderer renderer;
    public DrugMessageDistorter messageDistorter;
    public com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugHallucinationManager hallucinationManager;
    public DrugMusicManager musicManager;

    public int ticksExisted = 0;

    public int timeBreathingSmoke;
    public float[] breathSmokeColor;

    public int delayUntilHeartbeat;
    public int delayUntilBreath;
    public boolean lastBreathWasIn;

    public DrugProperties(EntityLivingBase entity)
    {
        influences = new ArrayList<>();

        messageDistorter = new DrugMessageDistorter();
        hallucinationManager = new DrugHallucinationManager();
        musicManager = new DrugMusicManager();
    }

    @Nullable
    public static DrugProperties getDrugProperties(EntityPlayer entity)
    {
        if (entity != null)
            return ExtendedProp.get(entity);

        return null;
    }

    public static void initInEntity(EntityPlayer entity)
    {
        if (entity != null)
        {
            ExtendedProp.put(entity, new DrugProperties((EntityLivingBase) entity));
            DrugProperties drugProperties = getDrugProperties(entity);

            if (drugProperties != null)
                OrangeSunshine.instance.createDrugRenderer(drugProperties);
        }
    }

    public void addDrug(String drugName, Drug drug)
    {
        drugs.put(drugName, drug);
    }

    @Nullable
    public Drug getDrug(String drugName)
    {
        return drugs.get(drugName);
    }

    public float getDrugValue(String drugName)
    {
        return (float) drugs.get(drugName).getActiveValue();
    }

    public void addToDrug(String drugName, double effect)
    {
        if (!drugs.containsKey(drugName))
        {
            OrangeSunshine.logger.warn("Tried to add to drug " + drugName);
            return;
        }

        hasChanges = true;
        drugs.get(drugName).addToDesiredValue(effect);
    }

    public void setDrugValue(String drugName, double effect)
    {
        if (!drugs.containsKey(drugName))
        {
            OrangeSunshine.logger.warn("Tried to set drug value " + drugName);
            return;
        }

        hasChanges = true;
        drugs.get(drugName).setDesiredValue(effect);
    }

    public void addToDrug(DrugInfluence influence)
    {
        hasChanges = true;
        influences.add(influence);
    }

    public Collection<Drug> getAllDrugs()
    {
        return drugs.values();
    }

    public Set<String> getAllDrugNames()
    {
        return drugs.keySet();
    }

    public String[] getAllVisibleDrugNames()
    {
        List<String> visibleDrugs = new ArrayList<>();

        for (String s : drugs.keySet())
        {
            if (drugs.get(s).isVisible())
                visibleDrugs.add(s);
        }

        String[] returnArray = new String[visibleDrugs.size()];
        visibleDrugs.toArray(returnArray);

        return returnArray;
    }

    public boolean doesDrugExist(String name)
    {
        return drugs.containsKey(name);
    }

    public void startBreathingSmoke(int time, float[] color)
    {
        if (color != null)
            this.breathSmokeColor = color;
        else
            this.breathSmokeColor = new float[]{1.0f, 1.0f, 1.0f};

        this.timeBreathingSmoke = time + 10; //10 is the time spent breathing in
    }

    public void updateDrugEffects(EntityLivingBase entity)
    {
        Random random = entity.getRNG();

        if (ticksExisted % 5 == 0) //4 times / sec is enough
        {
            for (Iterator<DrugInfluence> iterator = influences.iterator(); iterator.hasNext(); )
            {
                DrugInfluence influence = iterator.next();
                influence.update(this);

                if (influence.isDone())
                    iterator.remove();
            }
        }
        for (String key : drugs.keySet())
        {
            Drug drug = drugs.get(key);
            drug.update(entity, this);
        }

        if (entity.world.isRemote)
        {
            hallucinationManager.update(entity, this);
            musicManager.update(entity, this);

            if (delayUntilHeartbeat > 0)
                delayUntilHeartbeat--;
            if (delayUntilBreath > 0)
                delayUntilBreath--;

            if (delayUntilHeartbeat == 0)
            {
                float heartbeatVolume = 0.0f;
                for (Drug drug : getAllDrugs())
                    heartbeatVolume += drug.heartbeatVolume();

                if (heartbeatVolume > 0.0f)
                {
                    float speed = 1.0f;
                    for (Drug drug : getAllDrugs())
                        speed += drug.heartbeatSpeed();

                    delayUntilHeartbeat = MathHelper.floor(35.0f / (speed - 1.0f));
                    //entity.world.playSound(new BlockPos(entity.posX, entity.posY, entity.posZ), OrangeSunshine.MODID+":" + "heartBeat", heartbeatVolume, speed, false);
                }
            }

            if (delayUntilBreath == 0)
            {
                float breathVolume = 0.0f;
                for (Drug drug : getAllDrugs())
                    breathVolume += drug.breathVolume();

                lastBreathWasIn = !lastBreathWasIn;

                if (breathVolume > 0.0f)
                {
                    float speed = 1.0f;
                    for (Drug drug : getAllDrugs())
                        speed += drug.breathSpeed();
                    delayUntilBreath = MathHelper.floor(30.0f / speed);

                   // entity.world.playSound(entity.posX, entity.posY, entity.posZ, Psychedelicraft.modBase + "breath", breathVolume, speed * 0.1f + 0.9f + (lastBreathWasIn ? 0.15f : 0.0f), false);
                }
            }

            if (entity.onGround)
            {
                float jumpChance = 0.0f;
                for (Drug drug : getAllDrugs())
                    jumpChance += drug.randomJumpChance();

                if (random.nextFloat() < jumpChance)
                    PSAccessHelperEntity.jump(entity);
            }

            if (!entity.isSwingInProgress)
            {
                float punchChance = 0.0f;
                for (Drug drug : getAllDrugs())
                    punchChance += drug.randomPunchChance();

                if (random.nextFloat() < punchChance)
                    entity.swingArm(EnumHand.MAIN_HAND);
            }
        }

        if (timeBreathingSmoke > 0)
        {
            timeBreathingSmoke--;

            if (timeBreathingSmoke > 10 && entity.world.isRemote)
            {
                Vec3d look = entity.getLookVec();

                if (random.nextInt(2) == 0)
                {
                    float s = random.nextFloat() * 0.05f + 0.1f;
                    //entity.world.spawnParticle (entity, breathSmokeColor, look, s, 1.0f);
                }
                if (random.nextInt(5) == 0)
                {
                    float s = random.nextFloat() * 0.05f + 0.1f;
                    //OrangeSunshine.proxy.spawnColoredParticle(entity, breathSmokeColor, look, s, 2.5f);
                }
            }
        }

        renderer.update(this, entity);

        changeDrugModifierMultiply(entity, SharedMonsterAttributes.MOVEMENT_SPEED, getSpeedModifier(entity));

        ticksExisted++;

        if (hasChanges)
        {
            hasChanges = false;

            if (!entity.world.isRemote) {
                  PSNetworkHelperServer.sendEEPUpdatePacket(entity, EEP_KEY, "DrugData", OrangeSunshine.network);
            }
        }
    }

    public void readFromNBT(NBTTagCompound tagCompound, boolean fromPacket)
    {
        NBTTagCompound drugData = tagCompound.hasKey("Drugs", Constants.NBT.TAG_COMPOUND) ? tagCompound.getCompoundTag("Drugs")
                : tagCompound; // legacy
        for (String key : drugs.keySet())
            drugs.get(key).readFromNBT(drugData.getCompoundTag(key));

        influences.clear();
        NBTTagList influenceTagList = tagCompound.getTagList("drugInfluences", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < influenceTagList.tagCount(); i++)
        {
            NBTTagCompound compound = influenceTagList.getCompoundTagAt(i);

            Class<? extends DrugInfluence> influenceClass = DrugRegistry.getClass(compound.getString("influenceClass"));

            if (influenceClass != null)
            {
                DrugInfluence inf = null;

                try
                {
                    inf = influenceClass.newInstance();
                }
                catch (InstantiationException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }

                if (inf != null)
                {
                    inf.readFromNBT(compound);
                    addToDrug(inf);
                }
            }
        }

        this.ticksExisted = tagCompound.getInteger("drugsTicksExisted");

        if (fromPacket)
            hasChanges = true;
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound drugsComp = new NBTTagCompound();
        for (String key : drugs.keySet())
        {
            NBTTagCompound cmp = new NBTTagCompound();
            drugs.get(key).writeToNBT(cmp);
            drugsComp.setTag(key, cmp);
        }
        compound.setTag("Drugs", drugsComp);

        NBTTagList influenceTagList = new NBTTagList();
        for (DrugInfluence influence : influences)
        {
            NBTTagCompound infCompound = new NBTTagCompound();
            influence.writeToNBT(infCompound);
            infCompound.setString("influenceClass", DrugRegistry.getID(influence.getClass()));
            influenceTagList.appendTag(infCompound);
        }
        compound.setTag("drugInfluences", influenceTagList);

        compound.setInteger("drugsTicksExisted", ticksExisted);
    }

    public NBTTagCompound createNBTTagCompound()
    {
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToNBT(tagCompound);
        return tagCompound;
    }

    public void wakeUp(EntityLivingBase entity)
    {
        for (String key : drugs.keySet())
            drugs.get(key).reset(entity, this);
        influences.clear();

        hasChanges = true;
    }

    public void receiveChatMessage(EntityLivingBase entity, String message)
    {
        hallucinationManager.receiveChatMessage(entity, message);
    }

    public float getSpeedModifier(EntityLivingBase entity)
    {
        float modifier = 1.0F;
        for (Drug drug : getAllDrugs())
            modifier *= drug.speedModifier();

        return modifier;
    }

    public float getDigSpeedModifier(EntityLivingBase entity)
    {
        float modifier = 1.0F;
        for (Drug drug : getAllDrugs())
            modifier *= drug.digSpeedModifier();

        return modifier;
    }

    public EntityPlayer.SleepResult getDrugSleepStatus()
    {
        for (Drug drug : getAllDrugs())
        {
            EntityPlayer.SleepResult status = drug.getSleepStatus();
            if (status != null)
                return status;
        }

        return null;
    }

    public float getSoundMultiplier()
    {
        float modifier = 1.0F;
        for (Drug drug : getAllDrugs())
            modifier *= drug.soundVolumeModifier();

        return modifier;
    }

    public float[] getDigitalEffectPixelResize()
    {
        return digitalEffectPixelRescale;
    }

    public void changeDrugModifierMultiply(EntityLivingBase entity, IAttribute attribute, double value)
    {
        // 2: ret *= 1.0 + value
        changeDrugModifier(entity, attribute, value - 1.0, 2);
    }

    public void changeDrugModifier(EntityLivingBase entity, IAttribute attribute, double value, int mode)
    {
        IAttributeInstance speedInstance = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        AttributeModifier oldModifier = speedInstance.getModifier(DrugProperties.drugUUID);

        if (oldModifier != null)
            speedInstance.removeModifier(oldModifier);

        AttributeModifier newModifier = new AttributeModifier(DrugProperties.drugUUID, "Drug Effects", value, mode);
        speedInstance.applyModifier(newModifier);
    }

    // IExtendedEntityProperties

   // @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        compound.setTag(EEP_CMP_KEY, createNBTTagCompound());
    }

   // @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        readFromNBT(compound.getCompoundTag(EEP_CMP_KEY), true);
    }

    //@Override
    public void init(Entity entity, World world)
    {

    }

    @Override
    public void writeUpdateData(ByteBuf buffer, String context, Object... params)
    {
        ByteBufUtils.writeTag(buffer, createNBTTagCompound());
    }

    @Override
    public void readUpdateData(ByteBuf buffer, String context)
    {
        readFromNBT(ByteBufUtils.readTag(buffer), true);
    }

    @Override
    public boolean testProperty(Random random, Entity entity) {
        return false;
    }
}
