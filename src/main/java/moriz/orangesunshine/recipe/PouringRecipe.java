package moriz.orangesunshine.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import moriz.orangesunshine.fluid.container.FluidContainer;
import moriz.orangesunshine.fluid.container.MutableFluidContainer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Recipe for pouring fluid from one container to another.
 */
public class PouringRecipe extends CustomRecipe {
    public PouringRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public RecipeSerializer<PouringRecipe> getSerializer() {
        return PSRecipes.POUR_DRINK;
    }

    @Override
    public boolean matches(CraftingInput inventory, Level level) {
        List<MutableFluidContainer> recepticals = getRecepticals(inventory)
                .map(slot -> slot.content().getKey().toMutable(slot.content().getValue()))
                .toList();
        if (RecipeUtils.stacks(inventory).count() != recepticals.size() || recepticals.size() < 2) {
            return false;
        }

        return recepticals.get(1).canReceive(recepticals.get(0).getFluid());
    }

    private Stream<RecipeUtils.Slot<Map.Entry<FluidContainer, ItemStack>>> getRecepticals(CraftingInput inventory) {
        return RecipeUtils.recepticalSlots(inventory).limit(2);
    }

    @Override
    public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registries) {
        List<RecipeUtils.Slot<Map.Entry<FluidContainer, ItemStack>>> recepticals = getRecepticals(inventory).toList();
        MutableFluidContainer mutableTo = recepticals.get(1).map(entry -> entry.getKey().toMutable(entry.getValue()));
        recepticals.get(0).map(entry -> entry.getKey().toMutable(entry.getValue()))
                .transfer(mutableTo.getCapacity() - mutableTo.getLevel(), mutableTo, null);
        return mutableTo.asStack();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inventory) {
        List<RecipeUtils.Slot<Map.Entry<FluidContainer, ItemStack>>> recepticals = getRecepticals(inventory).toList();
        if (recepticals.size() < 2) {
            return NonNullList.withSize(inventory.size(), ItemStack.EMPTY);
        }

        RecipeUtils.Slot<Map.Entry<FluidContainer, ItemStack>> from = recepticals.get(0);
        MutableFluidContainer mutableTo = recepticals.get(1).map(entry -> entry.getKey().toMutable(entry.getValue()));
        MutableFluidContainer mutableFrom = from.content().getKey().toMutable(from.content().getValue())
                .transfer(mutableTo.getCapacity() - mutableTo.getLevel(), mutableTo, null);

        NonNullList<ItemStack> remainder = NonNullList.withSize(inventory.size(), ItemStack.EMPTY);
        remainder.set(from.slot(), mutableFrom.asStack());
        return remainder;
    }

    static class Serializer implements RecipeSerializer<PouringRecipe> {
        private static final MapCodec<PouringRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(PouringRecipe::category)
        ).apply(instance, PouringRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, PouringRecipe> STREAM_CODEC = StreamCodec.of(
                (buffer, recipe) -> CraftingBookCategory.STREAM_CODEC.encode(buffer, recipe.category()),
                buffer -> new PouringRecipe(CraftingBookCategory.STREAM_CODEC.decode(buffer))
        );

        @Override
        public MapCodec<PouringRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PouringRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
