package moriz.orangesunshine.fluid.container;

import java.util.function.Consumer;

import moriz.orangesunshine.fluid.PSFluids;
import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.util.MathUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;

/**
 * @author Sollace
 * @since 1 Jan 2023
 */
public interface FluidContainer extends ItemLike {
    FluidContainer UNLIMITED = new FluidContainer() {
        @Override
        public Item asItem() {
            return Items.STONE;
        }

        @Override
        public int getMaxCapacity() {
            return Integer.MAX_VALUE;
        }
    };

    static FluidContainer withCapacity(Item item, int capacity) {
        return new FluidContainer() {
            @Override
            public Item asItem() {
                return item;
            }

            @Override
            public int getMaxCapacity() {
                return capacity;
            }
        };
    }

    static FluidContainer of(ItemStack stack) {
        return of(stack.getItem());
    }

    static FluidContainer of(ItemStack stack, FluidContainer fallback) {
        return of(stack.getItem(), fallback);
    }

    static FluidContainer of(Item item) {
        return of(item, UNLIMITED);
    }

    static FluidContainer of(Item item, FluidContainer fallback) {
        return item instanceof FluidContainer c ? c : FluidContainerRegistry.getContainer(item).orElse(fallback);
    }

    int getMaxCapacity();

    default Item asEmpty() {
        return asItem();
    }

    default Item asFilled(SimpleFluid fluid) {
        return asItem();
    }

    default MutableFluidContainer toMutable(ItemStack stack) {
        return new MutableFluidContainer(this, getFluid(stack), getLevel(stack), getFluidAttributesTag(stack), getCustomData(stack));
    }

    default int getMaxCapacity(ItemStack stack) {
        return getMaxCapacity();
    }

    default float getFillPercentage(ItemStack stack) {
        return MathUtils.inverseLerp(getLevel(stack), 0, getMaxCapacity(stack));
    }

    default ItemStack getDefaultStack(SimpleFluid fluid) {
        Item bucketItem = this != UNLIMITED || !fluid.isCustomFluid() ? asItem() : of(fluid.getPhysical().getStandingFluid().getBucket()).asItem();
        return of(bucketItem).toMutable(bucketItem.getDefaultInstance())
                .withFluid(fluid)
                .withLevel(getMaxCapacity())
                .asStack();
    }

    default SimpleFluid getFluid(ItemStack stack) {
        CompoundTag fluidTag = getFluidTag(stack);
        if (getLevel(stack) == 0) {
            return PSFluids.EMPTY;
        }
        return SimpleFluid.byId(Identifier.tryParse(fluidTag.getStringOr("id", "")));
    }

    default int getLevel(ItemStack stack) {
        CompoundTag fluidTag = getFluidTag(stack);
        return fluidTag.contains("id")
                && !SimpleFluid.byId(Identifier.tryParse(fluidTag.getStringOr("id", ""))).isEmpty()
                ? fluidTag.getIntOr("level", 0)
                : 0;
    }

    CompoundTag EMPTY_NBT = new CompoundTag();

    static CompoundTag getCustomData(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    static CompoundTag getFluidAttributesTag(ItemStack stack) {
        return getFluidTag(stack).getCompoundOrEmpty("attributes");
    }

    static CompoundTag getFluidTag(ItemStack stack) {
        return getCustomData(stack).getCompoundOrEmpty("fluid");
    }

    static void updateFluidAttributes(ItemStack stack, Consumer<CompoundTag> consumer) {
        updateFluidTag(stack, fluidTag -> {
            CompoundTag attributes = fluidTag.contains("attributes")
                    ? fluidTag.getCompoundOrEmpty("attributes")
                    : new CompoundTag();
            consumer.accept(attributes);
            fluidTag.put("attributes", attributes);
        });
    }

    static void updateFluidTag(ItemStack stack, Consumer<CompoundTag> consumer) {
        CompoundTag customData = getCustomData(stack);
        CompoundTag fluidTag = customData.contains("fluid")
                ? customData.getCompoundOrEmpty("fluid")
                : new CompoundTag();
        consumer.accept(fluidTag);
        if (fluidTag.isEmpty()) {
            customData.remove("fluid");
        } else {
            customData.put("fluid", fluidTag);
        }

        if (customData.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(customData));
        }
    }
}
