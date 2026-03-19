package moriz.orangesunshine.fluid.physical;

import moriz.orangesunshine.fluid.SimpleFluid;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public final class PhysicalFluid {
    private final Fluid standing;
    private final Fluid flowing;
    private final Block block;

    @Nullable
    private final SimpleFluid type;

    public PhysicalFluid(Fluid standing, Fluid flowing, LiquidBlock block) {
        this.standing = standing;
        this.flowing = flowing;
        this.block = block;
        this.type = null;
    }

    public PhysicalFluid(Identifier id, SimpleFluid type) {
        @SuppressWarnings("unused") Object o = Fluids.EMPTY;
        this.type = type;
        standing = Registry.register(BuiltInRegistries.FLUID, id, PlacedFluid.still(this));
        flowing = Registry.register(BuiltInRegistries.FLUID, id.withPath(p -> "flowing_" + p), PlacedFluid.flowing(this));
        block = type.isEmpty() ? Blocks.AIR : Registry.register(BuiltInRegistries.BLOCK, id,
                PlacedFluidBlock.create(this, ResourceKey.create(Registries.BLOCK, id)));
    }

    public Fluid getStandingFluid() {
        return standing;
    }

    public Fluid getFlowingFluid() {
        return flowing;
    }

    public Block getBlock() {
        return block;
    }

    @Nullable
    public SimpleFluid getType() {
        return type;
    }

    public FluidState getDefaultState() {
        return getStandingFluid().defaultFluidState();
    }

    public boolean isIn(TagKey<Fluid> tag) {
        return standing.is(tag);
    }

    public boolean isOf(Fluid fluid) {
        return getStandingFluid().isSame(fluid);
    }
}
