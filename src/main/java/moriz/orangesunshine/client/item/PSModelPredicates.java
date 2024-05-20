package moriz.orangesunshine.client.item;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.client.render.FluidBoxRenderer;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.fluid.container.FluidContainer;
import moriz.orangesunshine.item.*;
import moriz.orangesunshine.item.BongItem;
import moriz.orangesunshine.item.PSItems;
import moriz.orangesunshine.item.PaperBagItem;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.DyeableItem;

/**
 * @author Sollace
 * @since 1 Jan 2023
 */
public interface PSModelPredicates {
    static void bootstrap() {
        ModelPredicateProviderRegistry.register(OrangeSunshine.id("using"), (stack, world, entity, seed) -> {
            if (entity == null || entity.getActiveItem() != stack) {
                return 0;
            }
            return entity.getItemUseTimeLeft() > 0 ? 1 : 0;
        });
        ModelPredicateProviderRegistry.register(OrangeSunshine.id("flying"), (stack, world, entity, seed) -> {
            return stack.hasNbt() && stack.getNbt().getBoolean("flying") ? 1 : 0;
        });
        ModelPredicateProviderRegistry.register(OrangeSunshine.id("tripping"), (stack, world, entity, seed) -> {
            return DrugProperties.of(entity).filter(DrugProperties::isTripping).isPresent() ? 1 : 0;
        });
        ModelPredicateProviderRegistry.register(PSItems.WINE_GRAPE_LATTICE, OrangeSunshine.id("age"), (stack, world, entity, seed) -> stack.getDamage() / 10F);
        ModelPredicateProviderRegistry.register(PSItems.MORNING_GLORY_LATTICE, OrangeSunshine.id("age"), (stack, world, entity, seed) -> stack.getDamage() / 10F);
        ModelPredicateProviderRegistry.register(OrangeSunshine.id("filled"), (stack, world, entity, seed) -> {
            if (stack.getItem() instanceof PaperBagItem item) {
                PaperBagItem.Contents contents = PaperBagItem.getContents(stack);
                return contents.isEmpty() ? 0 : contents.count() > 16000 ? 1 : 0.5F;
            }
            if (stack.getItem() instanceof BongItem item) {
                return item.hasUsableConsumable(entity) ? 1 : 0;
            }
            return FluidContainer.of(stack).getFluid(stack).isEmpty() ? 0 : 1;
        });
        ColorProviderRegistry.ITEM.register((stack, layer) -> layer > 0 ? -1 : PSItems.HARMONIUM.getColor(stack), PSItems.HARMONIUM);
        ColorProviderRegistry.ITEM.register((stack, layer) -> {
            if (layer == 0 && stack.getItem() instanceof DyeableItem dyeable) {
                return ((DyeableItem)stack.getItem()).getColor(stack);
            }
            if (layer == 1) {
                SimpleFluid fluid = FluidContainer.of(stack).getFluid(stack);
                if (!fluid.isEmpty()) {
                    return FluidBoxRenderer.FluidAppearance.getItemColor(fluid, stack);
                }
            }
            return -1;
        }, PSItems.BOTTLE, PSItems.MOLOTOV_COCKTAIL, PSItems.GLASS_CHALICE, PSItems.STONE_CUP, PSItems.WOODEN_MUG, PSItems.FILLED_BUCKET, PSItems.FILLED_BOWL, PSItems.SYRINGE);
        ColorProviderRegistry.ITEM.register((stack, layer) -> {
            if (layer == 0) {
                SimpleFluid fluid = FluidContainer.of(stack).getFluid(stack);
                if (!fluid.isEmpty()) {
                    return FluidBoxRenderer.FluidAppearance.getItemColor(fluid, stack);
                }
            }
            return -1;
        }, PSItems.FILLED_GLASS_BOTTLE);
    }
}
