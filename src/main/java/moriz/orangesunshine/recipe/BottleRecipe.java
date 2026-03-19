package moriz.orangesunshine.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public class BottleRecipe extends ShapedRecipe {
    public static final Map<Item, DyeColor> COLORS = Util.make(new HashMap<>(), map -> {
        map.put(Items.WHITE_STAINED_GLASS, DyeColor.WHITE);
        map.put(Items.ORANGE_STAINED_GLASS, DyeColor.ORANGE);
        map.put(Items.MAGENTA_STAINED_GLASS, DyeColor.MAGENTA);
        map.put(Items.LIGHT_BLUE_STAINED_GLASS, DyeColor.LIGHT_BLUE);
        map.put(Items.YELLOW_STAINED_GLASS, DyeColor.YELLOW);
        map.put(Items.LIME_STAINED_GLASS, DyeColor.LIME);
        map.put(Items.PINK_STAINED_GLASS, DyeColor.PINK);
        map.put(Items.GRAY_STAINED_GLASS, DyeColor.GRAY);
        map.put(Items.LIGHT_GRAY_STAINED_GLASS, DyeColor.LIGHT_GRAY);
        map.put(Items.CYAN_STAINED_GLASS, DyeColor.CYAN);
        map.put(Items.PURPLE_STAINED_GLASS, DyeColor.PURPLE);
        map.put(Items.BLUE_STAINED_GLASS, DyeColor.BLUE);
        map.put(Items.BROWN_STAINED_GLASS, DyeColor.BROWN);
        map.put(Items.GREEN_STAINED_GLASS, DyeColor.GREEN);
        map.put(Items.RED_STAINED_GLASS, DyeColor.RED);
        map.put(Items.BLACK_STAINED_GLASS, DyeColor.BLACK);
    });

    private final ShapedRecipePattern pattern;
    private final ItemStack result;

    public BottleRecipe(
            String group,
            CraftingBookCategory category,
            ShapedRecipePattern pattern,
            ItemStack result,
            boolean showNotification
    ) {
        super(group, category, pattern, result, showNotification);
        this.pattern = pattern;
        this.result = result;
    }

    public String getGroup() {
        return group();
    }

    public CraftingBookCategory getCategory() {
        return category();
    }

    public ShapedRecipePattern getPattern() {
        return pattern;
    }

    public ItemStack getResult() {
        return result;
    }

    @Override
    public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registries) {
        ItemStack output = result.copy();
        RecipeUtils.stacks(inventory)
                .map(ItemStack::getItem)
                .distinct()
                .map(COLORS::get)
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(color -> output.set(DataComponents.DYED_COLOR, new DyedItemColor(color.getTextColor())));
        return output;
    }

    @Override
    public RecipeSerializer<BottleRecipe> getSerializer() {
        return PSRecipes.CRAFTING_SHAPED;
    }

    public static class Serializer implements RecipeSerializer<BottleRecipe> {
        private static final MapCodec<BottleRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(BottleRecipe::getGroup),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(BottleRecipe::getCategory),
                ShapedRecipePattern.MAP_CODEC.forGetter(BottleRecipe::getPattern),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(BottleRecipe::getResult),
                com.mojang.serialization.Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(BottleRecipe::showNotification)
        ).apply(instance, BottleRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, BottleRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                BottleRecipe::getGroup,
                CraftingBookCategory.STREAM_CODEC,
                BottleRecipe::getCategory,
                ShapedRecipePattern.STREAM_CODEC,
                BottleRecipe::getPattern,
                ItemStack.STREAM_CODEC,
                BottleRecipe::getResult,
                ByteBufCodecs.BOOL,
                BottleRecipe::showNotification,
                BottleRecipe::new
        );

        @Override
        public MapCodec<BottleRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BottleRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
