/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block.entity;

import java.util.Optional;
import java.util.stream.IntStream;
import moriz.orangesunshine.recipe.DryingRecipe;
import moriz.orangesunshine.recipe.PSRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class DryingTableBlockEntity extends BlockEntityWithInventory {
    private static final int OUTPUT_SLOT_INDEX = 0;
    private static final int[] INPUT_SLOTS = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9};
    private static final int[] OUTPUT_SLOTS = new int[] {OUTPUT_SLOT_INDEX};

    private int ticksAlive;
    private float heatRatio;
    private float dryingProgress;
    private boolean isCooking;

    public final ContainerData propertyDelegate = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> (int)(heatRatio * 1000);
                case 1 -> (int)(dryingProgress * 1000);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> heatRatio = value / 1000F;
                case 1 -> dryingProgress = value / 1000F;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public DryingTableBlockEntity(BlockPos pos, BlockState state) {
        super(PSBlockEntities.DRYING_TABLE, pos, state, 10);
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, DryingTableBlockEntity entity) {
        entity.tick(level);
    }

    public float getHeatRatio() {
        return heatRatio;
    }

    public float getDryingProgress() {
        return dryingProgress;
    }

    public void tick(ServerLevel level) {
        float oldProgress = dryingProgress;
        float oldHeat = heatRatio;
        BlockPos pos = getBlockPos();

        if (++ticksAlive % 30 == 0) {
            float light = level.getMaxLocalRawBrightness(pos) / 15F;
            float temperature = !level.isEmptyBlock(pos)
                    ? level.getBiome(pos).value().getBaseTemperature() * 0.75F + 0.25F
                    : 0;
            heatRatio = Mth.clamp((light * light * temperature) * (light * light * temperature), 0, 1);

            if (level.getRainLevel(1) > 0
                    && level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() == pos.getY() + 1) {
                dryingProgress = 0;
            }

            if (!Mth.equal(oldHeat, heatRatio)) {
                level.updateNeighbourForOutputSignal(pos, getBlockState().getBlock());
            }
        }

        if (isCooking) {
            dryingProgress += 0.05F;

            int delta = (int)((1 - Mth.clamp(dryingProgress, 0, 1)) * 100);
            if (delta == 0 || level.getGameTime() % delta == 0) {
                for (int i = 0; i < 5; i++) {
                    level.sendParticles(ParticleTypes.SMOKE,
                            pos.getX() + level.getRandom().triangle(0.5F, 0.5F),
                            pos.getY() + 0.6F,
                            pos.getZ() + level.getRandom().triangle(0.5F, 0.5F),
                            2, 0, 0, 0, 0);
                }
            }

            if (dryingProgress >= 1) {
                endDryingProcess();
                level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
            }
        } else {
            dryingProgress = 0;
        }

        if (!Mth.equal(oldProgress, dryingProgress) || !Mth.equal(oldHeat, heatRatio)) {
            level.updateNeighborsAt(pos, getBlockState().getBlock());
            level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
            setChanged();
        }
    }

    public Optional<RecipeHolder<DryingRecipe>> getRecipe() {
        if (!getItem(OUTPUT_SLOT_INDEX).isEmpty() || level == null || level.getServer() == null) {
            return Optional.empty();
        }
        return level.getServer().getRecipeManager().getRecipeFor(PSRecipes.DRYING_TYPE, getCraftingInput(), level);
    }

    private CraftingInput getCraftingInput() {
        return CraftingInput.of(3, 3, IntStream.rangeClosed(1, 9).mapToObj(this::getItem).toList());
    }

    public ItemStack craft() {
        if (level == null) {
            return ItemStack.EMPTY;
        }
        return getRecipe()
                .map(recipe -> recipe.value().assemble(getCraftingInput(), level.registryAccess()))
                .orElse(ItemStack.EMPTY);
    }

    public void endDryingProcess() {
        ItemStack result = craft();
        ItemStack output = getItem(OUTPUT_SLOT_INDEX);
        clearContent();

        if (output.isEmpty()) {
            output = result;
        } else {
            output.grow(result.getCount());
        }
        setItem(OUTPUT_SLOT_INDEX, output);
    }

    @Override
    protected void writeNbt(CompoundTag compound) {
        super.writeNbt(compound);
        compound.putBoolean("cooking", isCooking);
        compound.putFloat("heatRatio", heatRatio);
        compound.putFloat("dryingProgress", dryingProgress);
    }

    @Override
    protected void readNbt(CompoundTag compound) {
        super.readNbt(compound);
        isCooking = compound.getBooleanOr("cooking", false);
        heatRatio = compound.getFloatOr("heatRatio", 0);
        dryingProgress = compound.getFloatOr("dryingProgress", 0);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void onInventoryChanged() {
        if (level != null && !level.isClientSide()) {
            isCooking = getRecipe().isPresent();
            dryingProgress = 0;
        }

        super.onInventoryChanged();
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return direction == Direction.UP ? OUTPUT_SLOTS : INPUT_SLOTS;
    }

    public ItemStack getStack(int slot) {
        return getItem(slot);
    }

    public void setStack(int slot, ItemStack stack) {
        setItem(slot, stack);
    }

    public void clear() {
        clearContent();
    }

    public void markDirty() {
        setChanged();
    }

    public int size() {
        return getContainerSize();
    }

    public int[] getAvailableSlots(Direction direction) {
        return getSlotsForFace(direction);
    }
}
