/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class BurdenedLatticeBlock extends LatticeBlock implements BonemealableBlock {
    public static final MapCodec<BurdenedLatticeBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("spreads").forGetter(block -> block.spreads),
            BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("stem").forGetter(block -> Optional.ofNullable(block.stem)),
            Codec.INT.fieldOf("shearedAge").forGetter(block -> block.shearedAge),
            propertiesCodec()
    ).apply(instance, (spreads, stem, shearedAge, settings) -> new BurdenedLatticeBlock(spreads, stem.orElse(null), shearedAge, settings)));

    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
    public static final int MAX_AGE = 3;

    private final boolean spreads;

    @Nullable
    private final Block stem;

    private final int shearedAge;

    public BurdenedLatticeBlock(boolean spreads, @Nullable Block stem, int shearedAge, BlockBehaviour.Properties settings) {
        super(settings);
        this.spreads = spreads;
        this.stem = stem;
        this.shearedAge = shearedAge;
        registerDefaultState(defaultBlockState().setValue(AGE, 0).setValue(PERSISTENT, false));
    }

    @Override
    public MapCodec<? extends BurdenedLatticeBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE, PERSISTENT);
    }

    @Override
    protected SoundType getSoundType(BlockState state) {
        return state.getValue(AGE) > 0 ? SoundType.GRASS : super.getSoundType(state);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack tool = builder.getOptionalParameter(LootContextParams.TOOL);
        if (tool != null && !tool.isEmpty()) {
            Holder<Enchantment> silkTouch = builder.getLevel().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.SILK_TOUCH);
            if (EnchantmentHelper.getItemEnchantmentLevel(silkTouch, tool) > 0) {
                ItemStack drop = asItem().getDefaultInstance();
                drop.setDamageValue(state.getValue(AGE));
                return List.of(drop);
            }
        }
        return super.getDrops(state, builder);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(Items.SHEARS)) {
            return InteractionResult.PASS;
        }

        if (state.getValue(AGE) < MAX_AGE || state.getValue(PERSISTENT)) {
            return InteractionResult.FAIL;
        }

        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel)world;
        Identifier farmingLootId = getLootTable()
                .orElseThrow()
                .identifier()
                .withPath(path -> path + "_farming");
        ResourceKey<net.minecraft.world.level.storage.loot.LootTable> farmingLootKey = ResourceKey.create(Registries.LOOT_TABLE, farmingLootId);

        world.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);

        LootParams lootParams = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, stack)
                .withParameter(LootContextParams.BLOCK_STATE, state)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, world.getBlockEntity(pos))
                .create(LootContextParamSets.BLOCK);

        serverLevel.getServer().reloadableRegistries().getLootTable(farmingLootKey).getRandomItems(lootParams, dropped -> {
            Block.popResource(serverLevel, pos, dropped);
        });

        BlockState newState = state.setValue(AGE, shearedAge);
        world.setBlock(pos, newState, Block.UPDATE_ALL);
        world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));

        if (!player.isCreative()) {
            EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            stack.hurtAndBreak(1, player, slot);
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (state.getValue(PERSISTENT)) {
            return;
        }

        if (checkConnectivity(world, state, pos)) {
            if (world.getMaxLocalRawBrightness(pos) >= 9 && random.nextInt(35) == 0) {
                if (state.getValue(AGE) < MAX_AGE) {
                    world.setBlock(pos, state.cycle(AGE), Block.UPDATE_ALL);
                }

                if (state.getValue(AGE) > 0) {
                    trySpread(state, world, pos);
                }
            }
        } else if (state.getValue(AGE) > 0) {
            world.setBlock(pos, state.setValue(AGE, state.getValue(AGE) - 1), Block.UPDATE_ALL);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return (state.getValue(AGE) < MAX_AGE || canSpread(state, world, pos)) && !state.getValue(PERSISTENT);
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        if (state.getValue(AGE) < MAX_AGE) {
            world.setBlock(pos, state.setValue(AGE, MAX_AGE), Block.UPDATE_ALL);
        } else {
            trySpread(state, world, pos);
        }
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        ItemStack stack = super.getCloneItemStack(world, pos, state, includeData);
        stack.setDamageValue(state.getValue(AGE));
        return stack;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState placedState = super.getStateForPlacement(ctx);
        if (placedState == null) {
            return null;
        }
        return placedState
                .setValue(AGE, ctx.getItemInHand().getDamageValue() % (MAX_AGE + 1))
                .setValue(PERSISTENT, true);
    }

    private void trySpread(BlockState state, Level world, BlockPos pos) {
        if (!spreads) {
            return;
        }
        visitNeighbours(state, pos)
                .filter(neighbourPos -> world.getBlockState(neighbourPos).is(PSBlocks.LATTICE))
                .findFirst()
                .ifPresent(neighbourPos -> world.setBlock(neighbourPos, copyStateProperties(defaultBlockState(), world.getBlockState(neighbourPos)), Block.UPDATE_ALL));
    }

    private boolean canSpread(BlockState state, LevelReader world, BlockPos pos) {
        return spreads && visitNeighbours(state, pos).anyMatch(neighbourPos -> world.getBlockState(neighbourPos).is(PSBlocks.LATTICE));
    }

    public boolean checkConnectivity(LevelReader world, BlockState state, BlockPos pos) {
        if (stem == null) {
            return true;
        }

        Set<BlockPos> visitedPositions = new HashSet<>();
        visitedPositions.add(pos.immutable());
        return checkConnectivity(world, state, pos, visitedPositions, 20);
    }

    private boolean checkConnectivity(LevelReader world, BlockState state, BlockPos pos, Set<BlockPos> visitedPositions, int maxDepth) {
        if (state.is(stem)) {
            return true;
        }

        if (!state.is(this)) {
            return false;
        }

        if (getFreeConnections(state).anyMatch(facing -> world.getBlockState(pos.relative(facing)).is(stem))) {
            return true;
        }

        if (maxDepth > 1) {
            return visitNeighbours(state, pos)
                    .filter(visitedPositions::add)
                    .anyMatch(neighbourPos -> checkConnectivity(world, world.getBlockState(neighbourPos), neighbourPos, visitedPositions, maxDepth - 1));
        }

        return false;
    }

    private static Stream<BlockPos> visitNeighbours(BlockState state, BlockPos pos) {
        return getConnections(state).flatMap(direction -> {
            BlockPos onLevel = pos.relative(direction);
            return Stream.of(onLevel.above(), onLevel, onLevel.below());
        });
    }
}
