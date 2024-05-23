/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import moriz.orangesunshine.block.PlacedDrinksBlock;
import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.fluid.ConsumableFluid;
import moriz.orangesunshine.fluid.FluidVolumes;
import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.fluid.container.FluidContainer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Sollace on Jan 1 2023
 */
public class DrugClearItem extends Item {

    UseAction action = UseAction.NONE;
    int useTime = 0;
    public DrugClearItem(Settings settings, UseAction action, int useTime) {
        super(settings.maxCount(1));
        this.action = action;
        this.useTime = useTime;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return action;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity entity) {
        if (!(entity instanceof PlayerEntity)) {return null;}
        DrugProperties properties = DrugProperties.of((PlayerEntity) entity);
        for (Drug drug:properties.getAllDrugs()) {
            properties.setDrugValue(drug.getType(), 0);
        }
        return PSItems.SYRINGE.getDefaultStack();
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return useTime;
    }
}
