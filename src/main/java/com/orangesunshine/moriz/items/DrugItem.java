package com.orangesunshine.moriz.items;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.orangesunshine.moriz.drugs.Drug;
import com.orangesunshine.moriz.drugs.DrugInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DrugItem extends Item {
    private final DrugEffectProperties[] effects;
    private final UseAnim useAction;

    public DrugItem(Item.Properties properties) {
        super(properties);
        Properties drugProperties = (Properties)properties;

        effects = drugProperties.attachedDrugs.toArray(new DrugEffectProperties[]{});
        useAction = drugProperties.useAction;
    }

    protected void addDrugs(Player playerEntity) {
        for (DrugEffectProperties properties : effects) {
            if (properties.drug.isPresent()) {
                Drug.addDrug(playerEntity, new DrugInstance(properties.drug.get(), properties.delayTick, properties.potencyPercentage, properties.duration));
            } else {
                //OrangeSunshine.LOGGER.error("{} is not in the drug registry!", properties.drug.toString());
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level world, LivingEntity entity) {
        if (entity instanceof Player playerEntity) {

            if (isEdible()) {
                playerEntity.eat(world, itemStack);
            } else if (!playerEntity.getAbilities().instabuild) {
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
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (isEdible()) {
            return super.use(world, player, hand);
        } else {
            ItemStack itemstack = player.getItemInHand(hand);
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return isEdible() ? super.getUseAnimation(itemStack) : useAction;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return isEdible() ? super.getUseDuration(itemStack) : 32;
    }

    public static class Properties extends Item.Properties {
        private final List<DrugEffectProperties> attachedDrugs = new ArrayList<>();
        private UseAnim useAction = UseAnim.EAT;

        public Properties addDrug(RegistryObject<Drug> drugRegistryObject, int delayTicks, float potencyPercentage, int duration) {
            this.attachedDrugs.add(new DrugEffectProperties(drugRegistryObject, delayTicks, potencyPercentage, duration));
            return this;
        }

        public Properties useAction(UseAnim useAction) {
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
