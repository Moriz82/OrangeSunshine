package com.BrotherHoodOfDiethylamide.OrangeSunshine.items;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugInstance;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class DrugItem extends ItemFood {
    private final DrugEffectProperties[] effects;
    private final EnumAction useAction;

    public DrugItem(String name, DrugEffectProperties[] effects) {
        this(name, effects, EnumAction.EAT, 64);
    }

    public DrugItem(String name, DrugEffectProperties[] effects, EnumAction useAction, int stackSize) {
        super(0, 0F, false);
        this.effects = effects;
        this.useAction = useAction;
        setAlwaysEdible();
        setRegistryName(OrangeSunshine.MODID, name);
        setUnlocalizedName(OrangeSunshine.MODID + "." + name);
        setCreativeTab(OrangeSunshine.CreativeTab);
        setMaxStackSize(stackSize);
    }

    public DrugItem(String name, DrugEffectProperties[] effects, int healAmount, float saturation) {
        super(healAmount, saturation, false);
        this.effects = effects;
        this.useAction = EnumAction.EAT;
        setAlwaysEdible();
        setRegistryName(OrangeSunshine.MODID, name);
        setUnlocalizedName(OrangeSunshine.MODID + "." + name);
        setCreativeTab(OrangeSunshine.CreativeTab);
    }

    protected void addDrugs(EntityPlayer player) {
        for (DrugEffectProperties prop : effects) {
            Drug drug = Drug.byName(prop.drugName);
            if (drug != null) {
                Drug.addDrug(player, new DrugInstance(drug, prop.delayTick, prop.potencyPercentage, prop.duration));
            }
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (useAction == EnumAction.EAT) {
                super.onItemUseFinish(stack, world, entity);
            } else if (!player.capabilities.isCreativeMode) {
                if (getMaxDamage(stack) > 0) {
                    stack.damageItem(1, player);
                    if (stack.getItemDamage() >= stack.getMaxDamage()) {
                        stack.shrink(1);
                    }
                } else {
                    stack.shrink(1);
                }
            }
            addDrugs(player);
        }
        return stack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (useAction == EnumAction.EAT) {
            return super.onItemRightClick(world, player, hand);
        }
        ItemStack itemstack = player.getHeldItem(hand);
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return useAction;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return useAction == EnumAction.EAT ? 32 : 32;
    }

    public static class DrugEffectProperties {
        public final String drugName;
        public final int delayTick;
        public final float potencyPercentage;
        public final int duration;

        public DrugEffectProperties(String drugName, int delayTick, float potencyPercentage, int duration) {
            this.drugName = drugName;
            this.delayTick = delayTick;
            this.potencyPercentage = potencyPercentage;
            this.duration = duration;
        }
    }
}
