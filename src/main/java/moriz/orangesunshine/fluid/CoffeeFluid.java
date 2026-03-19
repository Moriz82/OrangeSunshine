/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.fluid;

import moriz.orangesunshine.PSTags;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.entity.drug.influence.DrugInfluence;
import moriz.orangesunshine.fluid.container.FluidContainer;
import moriz.orangesunshine.fluid.container.MutableFluidContainer;
import moriz.orangesunshine.fluid.container.Resovoir;
import moriz.orangesunshine.fluid.physical.FluidStateManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

/**
 * Created by lukas on 22.10.14.
 */
public class CoffeeFluid extends DrugFluid implements Processable {
    public static final Attribute<Integer> WARMTH = Attribute.ofInt("warmth", 0, 2);
    private static final FluidStateManager.FluidProperty<Integer> TEMPERATURE =
            new FluidStateManager.FluidProperty<>(IntegerProperty.create("temperature", 0, 2), WARMTH::set, WARMTH::get);

    public CoffeeFluid(Identifier id, moriz.orangesunshine.fluid.DrugFluid.Settings settings) {
        super(id, settings.with(TEMPERATURE));
    }

    @Override
    public void getDrugInfluencesPerLiter(ItemStack stack, Consumer<DrugInfluence> consumer) {
        super.getDrugInfluencesPerLiter(stack, consumer);
        float warmth = (float)WARMTH.get(stack) / 2F;
        consumer.accept(new DrugInfluence(DrugType.CAFFEINE, 20, 0.002, 0.001, 0.25F + warmth * 0.05F));
        consumer.accept(new DrugInfluence(DrugType.WARMTH, 0, 0, 0.1, 0.8F * warmth));
    }

    @Override
    public void onRandomTick(ServerLevel world, BlockPos pos, FluidState state, net.minecraft.util.RandomSource random) {
        int temperature = state.getValue(TEMPERATURE.property());
        if (temperature > 0 && world.getBlockState(pos).getBlock() instanceof LiquidBlock) {
            world.setBlockAndUpdate(pos, state.setValue(TEMPERATURE.property(), temperature - 1).createLegacyBlock());
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(getTranslationKey() + ".temperature." + WARMTH.get(stack));
    }

    @Override
    public void getDefaultStacks(FluidContainer container, Consumer<ItemStack> consumer) {
        super.getDefaultStacks(container, consumer);
        consumer.accept(WARMTH.set(getDefaultStack(container), 1));
        consumer.accept(WARMTH.set(getDefaultStack(container), 2));
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isSuitableContainer(FluidContainer container) {
        return container.asItem().builtInRegistryHolder().is(PSTags.Items.SUITABLE_HOT_DRINK_RECEPTICALS);
    }

    @Override
    public int getProcessingTime(Resovoir tank, ProcessType type, @Nullable Resovoir complement) {
        return type == ProcessType.FERMENT && WARMTH.get(tank.getContents()) > 0 ? 300 : UNCONVERTABLE;
    }

    @Override
    public ItemStack process(Resovoir tank, ProcessType type, @Nullable Resovoir complement) {

        if (type == ProcessType.FERMENT) {
            MutableFluidContainer contents = tank.getContents();
            WARMTH.set(contents, Math.max(1, WARMTH.get(contents) - 1));
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void getProcessStages(ProcessType type, ProcessStageConsumer consumer) {
        if (type == ProcessType.FERMENT) {
            consumer.accept(300, -1,
                    stack -> List.of(WARMTH.set(stack, 2)),
                    stack -> List.of(WARMTH.set(stack, 1))
            );
        }
    }

    @Override
    public int getHash(ItemStack stack) {
        return Objects.hash(this, WARMTH.get(stack));
    }
}
