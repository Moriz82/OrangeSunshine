package moriz.orangesunshine.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import moriz.orangesunshine.block.CannabisPlantBlock;
import moriz.orangesunshine.block.PSBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class TilledPatchFeature extends Feature<TilledPatchFeature.Config> {
    public TilledPatchFeature() {
        super(TilledPatchFeature.Config.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        final WorldGenLevel world = context.level();

        final RandomSource random = context.random();

        Config config = context.config();

        BlockPos.MutableBlockPos mutablePos = context.origin().mutable();

        findTerrainLevel(world, mutablePos);

        if (world.isOutsideBuildHeight(mutablePos) || (!world.isEmptyBlock(mutablePos) && !isReplaceable(world, mutablePos))) {
            return false;
        }

        final BlockPos origin = mutablePos.immutable();
        mutablePos.move(Direction.DOWN);

        if (isDirt(world.getBlockState(mutablePos)) && (!config.needsWater() || isWaterNearby(world, mutablePos))) {
            final int patchSize = context.random().nextInt(3) + 1;
            final int patchSizeSq = patchSize * patchSize;

            for (int xOffset = -patchSize; xOffset <= patchSize; xOffset++) {
                for (int zOffset = -patchSize; zOffset <= patchSize; zOffset++) {

                    if (xOffset * xOffset + zOffset * zOffset > patchSizeSq) {
                        continue;
                    }
                    mutablePos.set(origin);
                    mutablePos.move(xOffset, 0, zOffset);

                    findTerrainLevel(world, mutablePos);

                    if (world.isOutsideBuildHeight(mutablePos) || !isReplaceable(world, mutablePos)) {
                        continue;
                    }
                    mutablePos.move(Direction.DOWN);

                    if (isDirt(world.getBlockState(mutablePos)) && random.nextInt(3) == 0) {
                        placeCrop(world, config, random, mutablePos);
                        mutablePos.move(Direction.DOWN);
                        BlockState roots = world.getBlockState(mutablePos);
                        if (isDirt(roots) || isStone(roots)) {
                            setBlock(world, mutablePos, Blocks.ROOTED_DIRT.defaultBlockState());
                        }
                    }
                }
            }

            return true;
        }

        return false;
    }

    private void placeCrop(WorldGenLevel world, Config config, RandomSource random, BlockPos.MutableBlockPos mutablePos) {
        int plantHeight = Math.min(2 + random.nextInt(random.nextInt(3) + 1), config.block().getMaxHeight());

        for (int i = 0; i < plantHeight; ++i) {
            BlockState state = config.block().getStateForHeight(i).setValue(CannabisPlantBlock.NATURAL, true);

            int age = config.block().getMaxAge(state);
            if (i == plantHeight - 1) {
                age = random.nextInt(age + 1);
            }

            state = state.setValue(config.block().getAgeProperty(), age);

            mutablePos.move(Direction.UP);

            if (!state.canSurvive(world, mutablePos)) {
                break;
            }

            setBlock(world, mutablePos, state);
        }
    }

    static boolean isReplaceable(WorldGenLevel world, BlockPos pos) {
        return world.isEmptyBlock(pos) || world.getBlockState(pos).is(BlockTags.REPLACEABLE_BY_TREES);
    }

    static boolean isWaterNearby(WorldGenLevel world, BlockPos pos) {
        return BlockPos.findClosestMatch(pos, 1, 1, p -> world.getFluidState(p).is(FluidTags.WATER)).isPresent();
    }

    static void findTerrainLevel(WorldGenLevel world, BlockPos.MutableBlockPos mutablePos) {
        if (isReplaceable(world, mutablePos)) {
            do {
                mutablePos.move(Direction.DOWN);
            } while (isReplaceable(world, mutablePos) && !world.isOutsideBuildHeight(mutablePos));

            mutablePos.move(Direction.UP);
        }

        if (!isReplaceable(world, mutablePos)) {
            do {
                mutablePos.move(Direction.UP);
            } while (!isReplaceable(world, mutablePos) && !world.isOutsideBuildHeight(mutablePos));
        }
    }

    public static record Config (boolean needsWater, CannabisPlantBlock block) implements FeatureConfiguration {
        private static Identifier blockId(CannabisPlantBlock block) {
            if (block == (CannabisPlantBlock) PSBlocks.HOP) {
                return Identifier.parse("orangesunshine:hop");
            }
            if (block == (CannabisPlantBlock) PSBlocks.TOBACCO) {
                return Identifier.parse("orangesunshine:tobacco");
            }
            if (block == (CannabisPlantBlock) PSBlocks.COFFEA) {
                return Identifier.parse("orangesunshine:coffea");
            }
            if (block == (CannabisPlantBlock) PSBlocks.COCA) {
                return Identifier.parse("orangesunshine:coca");
            }
            return Identifier.parse("orangesunshine:cannabis");
        }

        private static CannabisPlantBlock resolveBlock(Identifier id) {
            String path = id.getPath();
            return switch (path) {
                case "hop" -> (CannabisPlantBlock) PSBlocks.HOP;
                case "tobacco" -> (CannabisPlantBlock) PSBlocks.TOBACCO;
                case "coffea" -> (CannabisPlantBlock) PSBlocks.COFFEA;
                case "coca" -> (CannabisPlantBlock) PSBlocks.COCA;
                default -> (CannabisPlantBlock) PSBlocks.CANNABIS;
            };
        }

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> {
            return instance.group(
                    Codec.BOOL.fieldOf("needsWater").orElse(true).forGetter(Config::needsWater),
                    Identifier.CODEC.fieldOf("block").xmap(Config::resolveBlock, Config::blockId).forGetter(Config::block)
            ).apply(instance, (needsWater, block) -> new Config(
                    (boolean)needsWater,
                    block
            ));
        });
    }
}
