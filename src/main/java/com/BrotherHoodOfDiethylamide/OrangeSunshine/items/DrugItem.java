package com.BrotherHoodOfDiethylamide.OrangeSunshine.items;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugInstance;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DrugItem extends Item {
    private final DrugEffectProperties[] effects;
    private final UseAction useAction;

    public DrugItem(Item.Properties properties) {
        super(properties);
        Properties drugProperties = (Properties)properties;

        effects = drugProperties.attachedDrugs.toArray(new DrugEffectProperties[]{});
        useAction = drugProperties.useAction;
    }

    protected void addDrugs(PlayerEntity playerEntity) {
        for (DrugEffectProperties properties : effects) {
            if (properties.drug.isPresent()) {
                Drug.addDrug(playerEntity, new DrugInstance(properties.drug.get(), properties.delayTick, properties.potencyPercentage, properties.duration));
            } else {
                //OrangeSunshine.LOGGER.error("{} is not in the drug registry!", properties.drug.toString());
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, World world, LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) entity;

            if (isEdible()) {
                playerEntity.eat(world, itemStack);
            } else if (!playerEntity.abilities.instabuild) {
                itemStack.hurt(1, entity.getRandom(), null);
                //itemStack.shrink(1);
            }
            if (itemStack.getDamageValue() >= itemStack.getMaxDamage()){
                itemStack.shrink(1);
            }

            addDrugs(playerEntity);
        }

        return itemStack;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (isEdible()) {
            return super.use(world, player, hand);
        } else {
            ItemStack itemstack = player.getItemInHand(hand);
            player.startUsingItem(hand);
            return ActionResult.consume(itemstack);
        }
    }

    @Override
    public UseAction getUseAnimation(ItemStack itemStack) {
        return isEdible() ? super.getUseAnimation(itemStack) : useAction;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return isEdible() ? super.getUseDuration(itemStack) : 32;
    }

    public static class Properties extends Item.Properties {
        private final List<DrugEffectProperties> attachedDrugs = new ArrayList<>();
        private UseAction useAction = UseAction.EAT;

        public Properties addDrug(RegistryObject<Drug> drugRegistryObject, int delayTicks, float potencyPercentage, int duration) {
            this.attachedDrugs.add(new DrugEffectProperties(drugRegistryObject, delayTicks, potencyPercentage, duration));
            return this;
        }

        public Properties useAction(UseAction useAction) {
            this.useAction = useAction;
            return this;
        }
    }

    private static class DrugEffectProperties {
        private final RegistryObject<Drug> drug;
        private final int delayTick;
        private final float potencyPercentage;
        private final int duration;

        public DrugEffectProperties(RegistryObject<Drug> drug, int delayTick, float potencyPercentage, int duration) {
            this.drug = drug;
            this.delayTick = delayTick;
            this.potencyPercentage = potencyPercentage;
            this.duration = duration;
        }
    }
}
