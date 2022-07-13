package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.GlazedTerracottaBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.IPlantable;

public class SanPedroBlock extends CactusBlock {
	public SanPedroBlock(Properties p_i48435_1_) {
		super(p_i48435_1_);
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {
		BlockState plant = plantable.getPlant(world, pos.relative(facing));
		net.minecraftforge.common.PlantType type = plantable.getPlantType(world, pos.relative(facing));

		if (plant.getBlock() == Blocks.CACTUS || plant.getBlock() == ModBlocks.SAN_PEDRO_BLOCK.get())
			return state.is(Blocks.CACTUS) || state.is(Blocks.SAND) || state.is(Blocks.RED_SAND) || state.is(ModBlocks.SAN_PEDRO_BLOCK.get());

		if (plant.getBlock() == Blocks.SUGAR_CANE && this == Blocks.SUGAR_CANE)
			return true;

		if (net.minecraftforge.common.PlantType.DESERT.equals(type)) {
			return this.getBlock() == Blocks.SAND || this.getBlock() == Blocks.TERRACOTTA || this.getBlock() instanceof GlazedTerracottaBlock;
		} else if (net.minecraftforge.common.PlantType.NETHER.equals(type)) {
			return this.getBlock() == Blocks.SOUL_SAND;
		} else if (net.minecraftforge.common.PlantType.CROP.equals(type)) {
			return state.is(Blocks.FARMLAND);
		} else if (net.minecraftforge.common.PlantType.CAVE.equals(type)) {
			return state.isFaceSturdy(world, pos, Direction.UP);
		} else if (net.minecraftforge.common.PlantType.PLAINS.equals(type)) {
			return this.getBlock() == Blocks.GRASS_BLOCK || net.minecraftforge.common.Tags.Blocks.DIRT.contains(this) || this.getBlock() == Blocks.FARMLAND;
		} else if (net.minecraftforge.common.PlantType.WATER.equals(type)) {
			return state.getMaterial() == net.minecraft.block.material.Material.WATER; //&& state.getValue(BlockLiquidWrapper)
		} else if (net.minecraftforge.common.PlantType.BEACH.equals(type)) {
			boolean isBeach = state.is(Blocks.GRASS_BLOCK) || net.minecraftforge.common.Tags.Blocks.DIRT.contains(this) || state.is(Blocks.SAND) || state.is(Blocks.RED_SAND);
			boolean hasWater = false;
			for (Direction face : Direction.Plane.HORIZONTAL) {
				BlockState blockState = world.getBlockState(pos.relative(face));
				net.minecraft.fluid.FluidState fluidState = world.getFluidState(pos.relative(face));
				hasWater |= blockState.is(Blocks.FROSTED_ICE);
				hasWater |= fluidState.is(net.minecraft.tags.FluidTags.WATER);
				if (hasWater)
					break; //No point continuing.
			}
			return isBeach && hasWater;
		}
		return false;
	}
}
