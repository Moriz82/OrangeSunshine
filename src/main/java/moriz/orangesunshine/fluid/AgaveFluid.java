package moriz.orangesunshine.fluid;

import moriz.orangesunshine.fluid.container.FluidContainer;
import moriz.orangesunshine.item.PSItems;
import java.util.function.Consumer;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Created by lukas on 25.11.14.
 */
public class AgaveFluid extends AlcoholicFluid {

    public AgaveFluid(Identifier id, moriz.orangesunshine.fluid.AlcoholicFluid.Settings settings) {
        super(id, settings);
    }

    @Override
    public void getDefaultStacks(FluidContainer container, Consumer<ItemStack> consumer) {
        boolean isShot = container.asItem() == PSItems.SHOT_GLASS;
        settings.states.get().forEach(state -> {
            boolean isTequila = "tequila".contentEquals(state.entry().value().drinkName());
            if (isShot == isTequila) {
                consumer.accept(state.apply(getDefaultStack(container)));
            }
        });
    }
}
