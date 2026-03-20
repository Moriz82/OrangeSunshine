/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.fluid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.PSTags;
import moriz.orangesunshine.fluid.container.FluidContainer;
import moriz.orangesunshine.fluid.container.MutableFluidContainer;
import moriz.orangesunshine.fluid.physical.FluidStateManager;
import moriz.orangesunshine.fluid.physical.PhysicalFluid;
import moriz.orangesunshine.fluid.physical.PlacedFluid;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * Created by lukas on 29.10.14.
 * Updated by Sollace
 */
public class SimpleFluid {
    public static final Identifier EMPTY_KEY = OrangeSunshine.id("empty");
    private static final Map<Identifier, SimpleFluid> REGISTRY = new LinkedHashMap<>();
    private static final Map<Identifier, SimpleFluid> VANILLA_FLUIDS = new HashMap<>();
    public static final Codec<SimpleFluid> CODEC = Identifier.CODEC.xmap(SimpleFluid::byId, SimpleFluid::getId);

    protected final Identifier id;

    private final Identifier symbol;

    private final boolean custom;
    private final boolean empty;

    private final Settings settings;

    private final PhysicalFluid physical;

    public SimpleFluid(Identifier id, Settings settings) {
        this(id, settings, false);
    }

    public SimpleFluid(Identifier id, Settings settings, boolean empty) {
        this.id = id;
        this.settings = settings;
        this.symbol = id.withPath(p -> "textures/fluid/" + p + ".png");
        this.custom = true;
        this.empty = empty;
        physical = new PhysicalFluid(id, this);
        REGISTRY.put(id, this);
    }

    private SimpleFluid(Identifier id, int color, PhysicalFluid physical) {
        this.id = id;
        this.empty = false;
        this.settings = new Settings().color(color);
        this.symbol = id.withPath(p -> "textures/fluid/" + p + ".png");
        this.custom = false;
        this.physical = physical;
    }

    @SuppressWarnings("unchecked")
    protected <S extends Settings> S getSettings() {
        return (S)settings;
    }

    public final FluidStateManager getStateManager() {
        return settings.stateManager;
    }

    public final boolean isEmpty() {
        return empty;
    }

    public final Identifier getId() {
        return id;
    }

    public Identifier getSymbol(ItemStack stack) {
        return symbol;
    }

    public Optional<Identifier> getFlowTexture(ItemStack stack) {
        return Optional.empty();
    }

    public final ItemStack getStack(StateHolder<?, ?> state, FluidContainer container) {
        return getStateManager().writeStack(state, container.getDefaultStack(this));
    }

    public final FluidState getFluidState(ItemStack stack) {
        return getStateManager().readStack(getPhysical().getDefaultState(), stack);
    }

    public PhysicalFluid getPhysical() {
        return physical;
    }

    public boolean isCustomFluid() {
        return custom;
    }

    public int getColor(ItemStack stack) {
        return settings.color;
    }

    public int getViscocity() {
        return settings.viscocity;
    }

    protected String getTranslationKey() {
        return Util.makeDescriptionId(isCustomFluid() ? "fluid" : "block", id);
    }

    public final ItemStack getDefaultStack(FluidContainer container) {
        return getDefaultStack(container, container.getMaxCapacity());
    }

    public final ItemStack getDefaultStack() {
        return getDefaultStack(FluidContainer.UNLIMITED);
    }

    public final ItemStack getDefaultStack(int level) {
        return getDefaultStack(FluidContainer.UNLIMITED, level);
    }

    public ItemStack getDefaultStack(FluidContainer container, int level) {
        return container.toMutable(container.getDefaultStack(this)).withLevel(level).asStack();
    }

    public void getDefaultStacks(FluidContainer container, Consumer<ItemStack> consumer) {
        consumer.accept(getDefaultStack(container));
    }

    public Component getName(ItemStack stack) {
        return Component.translatable(getTranslationKey());
    }

    public void appendTooltip(ItemStack stack, @Nullable Level world, List<Component> tooltip, Item.TooltipContext context) {

    }

    public boolean isSuitableContainer(FluidContainer container) {
        return !container.asItem().builtInRegistryHolder().is(PSTags.Items.BARRELS);
    }

    public void randomDisplayTick(Level world, net.minecraft.core.BlockPos pos, FluidState state, RandomSource random) {
        if (!custom) {
            state.animateTick(world, pos, random);
        }
    }

    public void onRandomTick(ServerLevel world, net.minecraft.core.BlockPos pos, FluidState state, RandomSource random) {
    }

    public static boolean isEquivalent(SimpleFluid fluidA, ItemStack selfStack, SimpleFluid fluidB, ItemStack otherStack) {
        return fluidA == fluidB && fluidA.getHash(selfStack) == fluidB.getHash(otherStack);
    }

    public int getHash(ItemStack stack) {
        return hashCode();
    }

    public static SimpleFluid byId(@Nullable Identifier id) {
        if (id == null) {
            return PSFluids.EMPTY;
        }
        return Optional.ofNullable(REGISTRY.get(id))
                .orElseGet(() -> BuiltInRegistries.FLUID.getOptional(id).map(SimpleFluid::forVanilla).orElse(PSFluids.EMPTY));
    }

    public static SimpleFluid forVanilla(@Nullable Fluid fluid) {
        if (fluid instanceof PlacedFluid pf) {
            return pf.getType();
        }
        if (fluid == null || fluid == Fluids.EMPTY) {
            return PSFluids.EMPTY;
        }
        Fluid still = toStill(fluid);
        Identifier id = BuiltInRegistries.FLUID.getKey(still);
        return VANILLA_FLUIDS.computeIfAbsent(id, i -> new SimpleFluid(i, 0xFFFFFFFF,
                new PhysicalFluid(still, toFlowing(still), (LiquidBlock)still.defaultFluidState().createLegacyBlock().getBlock())
        ));
    }

    private static Fluid toStill(Fluid fluid) {
        return fluid instanceof FlowingFluid flowing ? flowing.getSource() : fluid;
    }

    private static Fluid toFlowing(Fluid fluid) {
        return fluid instanceof FlowingFluid flowing ? flowing.getFlowing() : fluid;
    }

    public static Iterable<SimpleFluid> all() {
        return REGISTRY.values();
    }

    @SuppressWarnings("unchecked")
    public static class Settings {
        private int color;
        private int viscocity = 1;
        final FluidStateManager stateManager = new FluidStateManager(new HashSet<>());

        public <T extends Settings> T color(int color) {
            this.color = color;
            return (T)this;
        }

        public <T extends Settings> T viscocity(int viscocity) {
            this.viscocity = viscocity;
            return (T)this;
        }

        public <T extends Settings> T with(FluidStateManager.FluidProperty<?> property) {
            stateManager.properties().add(property);
            return (T)this;
        }
    }

    public abstract static class Attribute<T extends Comparable<T>> {
        public abstract T get(ItemStack stack);

        public abstract ItemStack set(ItemStack stack, T value);

        public abstract T get(MutableFluidContainer stack);

        public abstract MutableFluidContainer set(MutableFluidContainer stack, T value);

        public abstract void forEachStep(BiConsumer<T , T> consumer);

        public static Attribute<Integer> ofInt(String name, int min, int max) {
            return new Attribute<>() {
                @Override
                public Integer get(ItemStack stack) {
                    return Mth.clamp(FluidContainer.getFluidAttributesTag(stack).getIntOr(name, 0), min, max);
                }

                @Override
                public ItemStack set(ItemStack stack, Integer value) {
                    FluidContainer.updateFluidAttributes(stack, tag -> tag.putInt(name, value));
                    return stack;
                }

                @Override
                public Integer get(MutableFluidContainer stack) {
                    return Mth.clamp(stack.getAttributes().getIntOr(name, 0), min, max);
                }

                @Override
                public MutableFluidContainer set(MutableFluidContainer stack, Integer value) {
                    CompoundTag attributes = stack.getAttributes().copy();
                    attributes.putInt(name, value);
                    stack.withAttributes(attributes);
                    return stack;
                }

                @Override
                public void forEachStep(BiConsumer<Integer, Integer> consumer) {
                    for (int i = min; i < max; i++) {
                        consumer.accept(i, i + 1);
                    }
                }
            };
        }

        public static Attribute<Boolean> ofBoolean(String name) {
            return new Attribute<>() {
                @Override
                public Boolean get(ItemStack stack) {
                    return FluidContainer.getFluidAttributesTag(stack).getBooleanOr(name, false);
                }

                @Override
                public ItemStack set(ItemStack stack, Boolean value) {
                    FluidContainer.updateFluidAttributes(stack, tag -> tag.putBoolean(name, value));
                    return stack;
                }

                @Override
                public Boolean get(MutableFluidContainer stack) {
                    return stack.getAttributes().getBooleanOr(name, false);
                }

                @Override
                public MutableFluidContainer set(MutableFluidContainer stack, Boolean value) {
                    CompoundTag attributes = stack.getAttributes().copy();
                    attributes.putBoolean(name, value);
                    stack.withAttributes(attributes);
                    return stack;
                }

                @Override
                public void forEachStep(BiConsumer<Boolean, Boolean> consumer) {
                    consumer.accept(false, true);
                }
            };
        }
    }
}
