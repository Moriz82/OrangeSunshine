/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block.entity;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import moriz.orangesunshine.ParticleHelper;
import moriz.orangesunshine.block.MashTubBlock;
import moriz.orangesunshine.fluid.FluidVolumes;
import moriz.orangesunshine.fluid.PSFluids;
import moriz.orangesunshine.fluid.Processable;
import moriz.orangesunshine.fluid.container.FluidContainer;
import moriz.orangesunshine.fluid.container.Resovoir;
import moriz.orangesunshine.particle.BubbleParticleEffect;
import moriz.orangesunshine.recipe.MashingRecipe;
import moriz.orangesunshine.recipe.PSRecipes;
import moriz.orangesunshine.util.MathUtils;
import moriz.orangesunshine.util.NbtSerialisable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 27.10.14.
 */
public class MashTubBlockEntity extends FluidProcessingBlockEntity {
    public ItemStack solidContents = ItemStack.EMPTY;

    private Optional<Stew> currentStew = Optional.empty();
    private Optional<RecipeHolder<MashingRecipe>> expectedRecipe = Optional.empty();
    private final Object2IntMap<Item> suppliedIngredients = new Object2IntOpenHashMap<>();

    public MashTubBlockEntity(BlockPos pos, BlockState state) {
        super(PSBlockEntities.MASH_TUB, pos, state, FluidVolumes.VAT, Processable.ProcessType.FERMENT);
    }

    public Object2IntMap<Item> getSuppliedIngredients() {
        return suppliedIngredients;
    }

    @Override
    protected void onProcessCompleted(ServerLevel world, Resovoir tank, ItemStack solids) {
        if (!solids.isEmpty()) {
            tank.clearContent();
            solidContents = solids;
        }

        super.onProcessCompleted(world, tank, solids);
    }

    @Override
    public void tick(ServerLevel world) {
        super.tick(world);
        currentStew = currentStew.filter(Stew::tick);
    }

    @Override
    public void onIdle(Resovoir resovoir) {
        super.onIdle(resovoir);

        Level level = getLevel();
        if (level == null) {
            return;
        }

        int luminance = resovoir.getFluidType().getPhysical().getDefaultState().createLegacyBlock().getLightEmission();
        int currentLuminance = getBlockState().getValue(MashTubBlock.LIGHT);
        if (luminance != currentLuminance) {
            level.setBlock(getBlockPos(), getBlockState().setValue(MashTubBlock.LIGHT, luminance), 3);
        }
    }

    public void tickAnimations() {
        Level level = getLevel();
        if (level != null
                && !suppliedIngredients.isEmpty()
                && level.getRandom().nextFloat() < 0.33F
                && level.getGameTime() % 3 == 0) {
            spawnBubbles(1 + (int)(suppliedIngredients.size() * 1.5F), 0, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP);
        }
    }

    public DepositResult<ItemStack> depositIngredient(ItemStack stack) {
        Level level = getLevel();
        if (level == null) {
            return DepositResult.pass(stack);
        }

        Resovoir tank = getTank(Direction.UP);
        FluidContainer container = FluidContainer.of(stack, null);
        if (container != null && !container.getFluid(stack).isEmpty()) {
            if (tank.getLevel() < tank.getCapacity()) {
                level.playSound(null, getBlockPos(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1, 1);
                onIdle(tank);
                return DepositResult.success(tank.deposit(stack));
            }
            return DepositResult.fail(stack);
        }

        if (isValidIngredient(stack)) {
            ItemStack consumed = stack.split(1);
            suppliedIngredients.computeInt(consumed.getItem(), (item, count) -> count == null ? 1 : count + 1);
            checkIngredients();
            spawnBubbles(20, 0, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP);
            level.playSound(null, getBlockPos(), SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1, 1);
            onIdle(tank);
            return DepositResult.success(stack);
        }
        return DepositResult.pass(stack);
    }

    public boolean isValidIngredient(ItemStack stack) {
        return FluidContainer.of(stack, null) == null
                && getMashingRecipes()
                        .map(RecipeHolder::value)
                        .filter(recipe -> recipe.getPoolFluid().test(getTank(Direction.UP)))
                        .flatMap(recipe -> recipe.getIngredients().stream())
                        .anyMatch(ingredient -> ingredient.test(stack));
    }

    private void checkIngredients() {
        if (suppliedIngredients.isEmpty() || !(getLevel() instanceof ServerLevel)) {
            return;
        }

        var matchedRecipes = getMashingRecipes()
                .filter(recipe -> recipe.value().getPoolFluid().test(getTank(Direction.UP)))
                .map(recipe -> Map.entry(recipe, recipe.value().matchPartially(suppliedIngredients)))
                .filter(pair -> pair.getValue().isMatch())
                .toList();

        var recipeMatch = expectedRecipe
                .map(recipe -> Map.entry(recipe, recipe.value().matchPartially(suppliedIngredients)))
                .filter(pair -> pair.getValue().isMatch())
                .or(() -> matchedRecipes.stream().findFirst());

        expectedRecipe = recipeMatch.map(Map.Entry::getKey);

        if (recipeMatch.isEmpty()) {
            onCraftingFailed();
            return;
        }

        if (matchedRecipes.size() == 1) {
            currentStew = recipeMatch
                    .filter(pair -> pair.getValue().isCraftable())
                    .map(Map.Entry::getKey)
                    .map(Stew::new);
            markForUpdate();
        }
    }

    private void onCraftingFailed() {
        suppliedIngredients.clear();
        expectedRecipe = Optional.empty();
        currentStew = Optional.empty();
        getTank(Direction.UP).getContents().withFluid(PSFluids.SLURRY);
        spawnBubbles(90, 0.5F, SoundEvents.MUD_BREAK);
        onIdle(getTank(Direction.UP));
    }

    private void spawnBubbles(int count, float spread, SoundEvent sound) {
        Level level = getLevel();
        if (level == null) {
            return;
        }

        RandomSource random = level.getRandom();
        Vec3 center = ParticleHelper.apply(getBlockPos().getCenter(), x -> random.triangle(x, 0.25));

        Resovoir tank = getTank(Direction.UP);
        ParticleHelper.spawnParticles(level,
                new BubbleParticleEffect(MathUtils.unpackRgbVector(tank.getFluidType().getColor(tank.getStack())), 1F),
                () -> ParticleHelper.apply(center, x -> random.triangle(x, 0.5 + spread)).add(0, 0.5, 0),
                Suppliers.ofInstance(new Vec3(
                        random.triangle(0, 0.125),
                        random.triangle(0.1, 0.125),
                        random.triangle(0, 0.125)
                )),
                count
        );

        float volume = 0.5F + random.nextFloat();
        float pitch = 0.3F + random.nextFloat();
        if (level instanceof ServerLevel) {
            level.playSound(null, getBlockPos(), sound, SoundSource.BLOCKS, volume, pitch);
        } else {
            level.playLocalSound(getBlockPos(), sound, SoundSource.BLOCKS, volume, pitch, true);
        }
    }

    @Override
    public List<ItemStack> getDroppedStacks(FluidContainer container) {
        if (!solidContents.isEmpty()) {
            return List.of(solidContents);
        }
        return List.of();
    }

    @Override
    public void onDrain(Resovoir resovoir) {
        if (!solidContents.isEmpty() && resovoir.isEmpty()) {
            setTimeProcessed(0);
        }
        onIdle(resovoir);
    }

    @Override
    public void onFill(Resovoir resovoir, int amountFilled) {
        if (!solidContents.isEmpty()) {
            super.onFill(resovoir, amountFilled);
        } else {
            onIdle(resovoir);
        }
    }

    @Override
    protected void writeNbt(CompoundTag compound) {
        super.writeNbt(compound);
        if (!solidContents.isEmpty()) {
            compound.store("solidContents", ItemStack.OPTIONAL_CODEC, solidContents);
        }
        CompoundTag suppliedIngredientsTag = new CompoundTag();
        suppliedIngredients.forEach((item, count) ->
                suppliedIngredientsTag.putInt(BuiltInRegistries.ITEM.getKey(item).toString(), count));
        compound.put("suppliedIngredients", suppliedIngredientsTag);
    }

    @Override
    protected void readNbt(CompoundTag compound) {
        super.readNbt(compound);
        solidContents = compound.read("solidContents", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);

        CompoundTag suppliedIngredientsTag = compound.getCompoundOrEmpty("suppliedIngredients");
        suppliedIngredients.clear();
        suppliedIngredientsTag.keySet().forEach(key ->
                Optional.ofNullable(Identifier.tryParse(key))
                        .flatMap(BuiltInRegistries.ITEM::getOptional)
                        .filter(Objects::nonNull)
                        .ifPresent(item -> suppliedIngredients.put(item, suppliedIngredientsTag.getIntOr(key, 0))));
    }

    private Stream<RecipeHolder<MashingRecipe>> getMashingRecipes() {
        Level level = getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return Stream.empty();
        }

        RecipeManager recipeManager = serverLevel.recipeAccess();
        return recipeManager.getRecipes().stream()
                .flatMap(recipe -> asMashingRecipe(recipe).stream());
    }

    private Optional<RecipeHolder<MashingRecipe>> asMashingRecipe(RecipeHolder<?> recipe) {
        if (!(recipe.value() instanceof MashingRecipe mashingRecipe)) {
            return Optional.empty();
        }
        return Optional.of(new RecipeHolder<>(recipe.id(), mashingRecipe));
    }

    public static final class DepositResult<T> {
        private final InteractionResult result;
        private final T value;

        private DepositResult(InteractionResult result, T value) {
            this.result = result;
            this.value = value;
        }

        public static <T> DepositResult<T> success(T value) {
            return new DepositResult<>(InteractionResult.SUCCESS, value);
        }

        public static <T> DepositResult<T> fail(T value) {
            return new DepositResult<>(InteractionResult.FAIL, value);
        }

        public static <T> DepositResult<T> pass(T value) {
            return new DepositResult<>(InteractionResult.PASS, value);
        }

        public InteractionResult getResult() {
            return result;
        }

        public T getValue() {
            return value;
        }
    }

    class Stew implements NbtSerialisable {
        private RecipeHolder<MashingRecipe> recipe;
        private int stewTime;

        public Stew(RecipeHolder<MashingRecipe> recipe) {
            this.recipe = recipe;
            Level level = getLevel();
            stewTime = -(2 + (level == null ? 0 : level.getRandom().nextInt(4)));
        }

        public boolean tick() {
            setChanged();

            Level level = getLevel();
            if (level == null || recipe == null) {
                return false;
            }
            if (level.getGameTime() % 30 == 0) {
                spawnBubbles(9, 0.5F, SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE);

                if (++stewTime >= recipe.value().getStewTime()) {
                    suppliedIngredients.clear();
                    expectedRecipe = Optional.empty();
                    getTank(Direction.UP).getContents()
                            .withFluid(recipe.value().getOutputFluid().fluid())
                            .withAttributes(recipe.value().getOutputFluid().attributes());
                    onIdle(getTank(Direction.UP));
                    return false;
                }
            }

            return true;
        }

        @Override
        public void toNbt(CompoundTag compound) {
            compound.putInt("stewTime", stewTime);
            compound.putString("recipe", recipe.id().identifier().toString());
        }

        @Override
        public void fromNbt(CompoundTag compound) {
            stewTime = compound.getIntOr("stewTime", 0);

            Level level = getLevel();
            Identifier id = Identifier.tryParse(compound.getStringOr("recipe", ""));
            if (!(level instanceof ServerLevel serverLevel) || id == null) {
                recipe = null;
                return;
            }

            recipe = serverLevel.recipeAccess()
                    .byKey(ResourceKey.create(Registries.RECIPE, id))
                    .flatMap(MashTubBlockEntity.this::asMashingRecipe)
                    .orElse(null);
        }
    }
}
