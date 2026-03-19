package moriz.orangesunshine.block;

import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.mojang.serialization.MapCodec;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.PSTags;
import moriz.orangesunshine.block.entity.PSBlockEntities;
import moriz.orangesunshine.block.entity.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PlacedDrinksBlock extends BaseEntityBlock implements EntityBlock {
    public static final MapCodec<PlacedDrinksBlock> CODEC = simpleCodec(PlacedDrinksBlock::new);
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 1, 16);

    protected PlacedDrinksBlock(BlockBehaviour.Properties settings) {
        super(settings.noCollision());
    }

    @Override
    public MapCodec<? extends PlacedDrinksBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return SHAPE;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return OrangeSunshine.getCrossHairTarget()
                .filter(hit -> hit.getType() == HitResult.Type.BLOCK)
                .map(hit -> (BlockHitResult)hit)
                .filter(hit -> hit.getBlockPos().equals(pos))
                .flatMap(Data::getHitPos)
                .flatMap(hitPos -> world.getBlockEntity(pos) instanceof Data data ? data.getDrink(hitPos) : Optional.empty())
                .orElseGet(() -> super.getCloneItemStack(world, pos, state, includeData));
    }

    public static boolean canPlace(ItemStack stack) {
        return stack.is(PSTags.Items.PLACEABLE);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return world.getBlockEntity(pos, PSBlockEntities.PLACED_DRINK).flatMap(be -> Data.getHitPos(hit).map(hitPos -> {
            if (world.isClientSide()) {
                return be.hasDrink(hitPos) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
            }

            DrinkAction extracted = be.removeDrink(hitPos);
            if (!extracted.stack().isEmpty() && !player.addItem(extracted.stack())) {
                player.drop(extracted.stack(), false);
            }
            return extracted.result();
        })).orElse(InteractionResult.FAIL);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!canPlace(stack)) {
            return InteractionResult.FAIL;
        }

        return world.getBlockEntity(pos, PSBlockEntities.PLACED_DRINK).flatMap(be -> Data.getHitPos(hit).map(hitPos -> {
            if (world.isClientSide()) {
                return be.canPlaceDrink(hitPos) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
            }

            ItemStack toInsert = player.isCreative() ? stack.copyWithCount(1) : stack;
            return be.placeDrink(hitPos, toInsert, player.getYHeadRot()).result();
        })).orElse(InteractionResult.FAIL);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof Data data) {
            data.dropAllDrinks();
        }
        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean inside) {
        if (!world.isClientSide()
                && entity instanceof LivingEntity
                && (entity.getY() > pos.getY() || (!entity.isShiftKeyDown() && entity.getY() >= pos.getY()))
                && Math.max(Math.abs(entity.getX() - entity.xo), Math.abs(entity.getZ() - entity.zo)) >= 0.003F) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Data data) {
                data.dropAllDrinks();
            }
            world.removeBlock(pos, false);
            world.playSound(null, pos, SoundEvents.CANDLE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Data(pos, state);
    }

    public static InteractionResult tryPlace(UseOnContext context) {
        if (!canPlace(context.getItemInHand())) {
            return InteractionResult.PASS;
        }

        Level world = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = world.getBlockState(clickedPos);
        boolean replaceable = clickedState.canBeReplaced(new BlockPlaceContext(context));

        if (!replaceable && context.getClickedFace() != Direction.UP) {
            return InteractionResult.PASS;
        }

        if (clickedState.is(PSBlocks.PLACED_DRINK)) {
            return clickedState.useItemOn(
                    context.getItemInHand(),
                    world,
                    context.getPlayer(),
                    context.getHand(),
                    new BlockHitResult(context.getClickLocation(), context.getClickedFace(), clickedPos, true)
            );
        }

        BlockPos blockPos = replaceable ? clickedPos : clickedPos.relative(context.getClickedFace());
        if (!replaceable && !world.isEmptyBlock(blockPos)) {
            return InteractionResult.PASS;
        }

        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockPos hitPos = Data.getHitPos(blockPos, context.getClickLocation());
        if (!world.setBlock(blockPos, PSBlocks.PLACED_DRINK.defaultBlockState(), Block.UPDATE_ALL)) {
            return InteractionResult.FAIL;
        }

        Player player = context.getPlayer();
        ItemStack stack = player != null && player.isCreative()
                ? context.getItemInHand().copyWithCount(1)
                : context.getItemInHand();
        float yaw = player != null ? player.getYHeadRot() : context.getRotation();

        return world.getBlockEntity(blockPos, PSBlockEntities.PLACED_DRINK)
                .map(be -> be.placeDrink(hitPos, stack, yaw).result())
                .orElse(InteractionResult.FAIL);
    }

    private record DrinkAction(InteractionResult result, ItemStack stack) {
    }

    public static class Data extends SyncedBlockEntity {
        static final int MAX_COORD = 16;
        static final int MAX_INDEX = MAX_COORD * MAX_COORD;
        static final int MAX_STACK_HEIGHT = 5;

        private final IntObjectMap<Stack<Entry>> entries = new IntObjectHashMap<>(MAX_INDEX);

        public Data(BlockPos pos, BlockState state) {
            super(PSBlockEntities.PLACED_DRINK, pos, state);
        }

        public void forEachDrink(DrinkConsumer consumer) {
            entries.values().forEach(list -> {
                final float[] y = new float[1];
                list.forEach(drink -> y[0] += consumer.accept(y[0], drink));
            });
        }

        public DrinkAction removeDrink(BlockPos center) {
            return nearbyPositions(center).map(pos -> {
                int index = getIndex(pos);
                Stack<Entry> list = entries.get(index);
                if (list != null && !list.isEmpty()) {
                    Entry entry = list.pop();
                    if (list.isEmpty()) {
                        entries.remove(index);
                    }

                    onContentsChanged();
                    if (entries.isEmpty() && level != null) {
                        level.removeBlock(getBlockPos(), false);
                    }
                    return new DrinkAction(InteractionResult.SUCCESS_SERVER, entry.stack());
                }
                return new DrinkAction(InteractionResult.FAIL, ItemStack.EMPTY);
            }).filter(result -> result.result().consumesAction()).findFirst().orElse(new DrinkAction(InteractionResult.FAIL, ItemStack.EMPTY));
        }

        public boolean hasDrink(BlockPos center) {
            return nearbyPositions(center)
                    .map(pos -> entries.get(getIndex(pos)))
                    .anyMatch(list -> list != null && !list.isEmpty());
        }

        public Optional<ItemStack> getDrink(BlockPos center) {
            return nearbyPositions(center)
                    .map(pos -> entries.get(getIndex(pos)))
                    .filter(list -> list != null && !list.isEmpty())
                    .map(Stack::peek)
                    .map(Entry::stack)
                    .findFirst();
        }

        public boolean canPlaceDrink(BlockPos position) {
            Stack<Entry> list = entries.get(getIndex(position));
            return list == null || list.size() < MAX_STACK_HEIGHT;
        }

        public DrinkAction placeDrink(BlockPos position, ItemStack stack, float yaw) {
            if (stack.isEmpty()) {
                return new DrinkAction(InteractionResult.FAIL, ItemStack.EMPTY);
            }

            int index = getIndex(position);
            Stack<Entry> list = entries.get(index);
            if (list == null) {
                list = new Stack<>();
                entries.put(index, list);
            }
            if (list.size() >= MAX_STACK_HEIGHT) {
                return new DrinkAction(InteractionResult.FAIL, stack);
            }

            ItemStack storedStack = stack.split(1);
            if (storedStack.isEmpty()) {
                return new DrinkAction(InteractionResult.FAIL, stack);
            }

            list.add(new Entry(position.getX() / 16.0F - 0.5F, position.getZ() / 16.0F - 0.5F, (-yaw) % 360.0F, storedStack));
            if (level != null) {
                level.playSound(null, getBlockPos(), SoundEvents.CANDLE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            onContentsChanged();
            return new DrinkAction(InteractionResult.SUCCESS_SERVER, stack);
        }

        @Override
        protected void readNbt(CompoundTag nbt) {
            readEntriesFromNbt(nbt.getCompoundOrEmpty("entries"));
        }

        @Override
        protected void writeNbt(CompoundTag nbt) {
            nbt.put("entries", writeEntriesToNbt(new CompoundTag()));
        }

        private void readEntriesFromNbt(CompoundTag nbt) {
            entries.clear();
            nbt.keySet().forEach(key -> {
                int index = Integer.parseInt(key);
                ListTag list = nbt.getListOrEmpty(key);
                if (!list.isEmpty()) {
                    entries.put(index, list.compoundStream().map(Entry::new).collect(Collectors.toCollection(Stack::new)));
                }
            });
        }

        private CompoundTag writeEntriesToNbt(CompoundTag nbt) {
            entries.entries().forEach(entry -> {
                ListTag list = new ListTag();
                entry.value().forEach(drink -> list.add(drink.toNbt(new CompoundTag())));
                nbt.put(Integer.toString(entry.key()), list);
            });
            return nbt;
        }

        public static Optional<BlockPos> getHitPos(BlockHitResult hit) {
            Direction direction = hit.getDirection();
            if (direction.getAxis() != Direction.Axis.Y) {
                return Optional.empty();
            }
            return Optional.of(getHitPos(hit.getBlockPos().relative(direction), hit.getLocation()));
        }

        public static BlockPos getHitPos(BlockPos pos, Vec3 relativePos) {
            return BlockPos.containing(
                    (relativePos.x - pos.getX()) * MAX_COORD,
                    0,
                    (relativePos.z - pos.getZ()) * MAX_COORD
            );
        }

        private static Stream<BlockPos> nearbyPositions(BlockPos center) {
            return StreamSupport.stream(BlockPos.spiralAround(center, 2, Direction.EAST, Direction.NORTH).spliterator(), false)
                    .map(pos -> (BlockPos)pos);
        }

        private static int getIndex(BlockPos position) {
            return ((Math.max(0, position.getX()) * MAX_COORD) + Math.max(0, position.getZ())) % MAX_INDEX;
        }

        private void dropAllDrinks() {
            if (level == null) {
                entries.clear();
                return;
            }

            forEachDrink((y, entry) -> {
                Block.popResource(level, getBlockPos(), entry.stack());
                return 0;
            });
            entries.clear();
        }

        private void onContentsChanged() {
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }

        public record Entry(float x, float z, float rotation, ItemStack stack) {

            public Entry(CompoundTag compound) {
                this(
                        compound.getFloatOr("x", 0.0F),
                        compound.getFloatOr("z", 0.0F),
                        compound.getFloatOr("rotation", 0.0F),
                        compound.read("stack", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY)
                );
            }

            public CompoundTag toNbt(CompoundTag compound) {
                compound.putFloat("x", x);
                compound.putFloat("z", z);
                compound.putFloat("rotation", rotation);
                compound.store("stack", ItemStack.OPTIONAL_CODEC, stack);
                return compound;
            }
        }

        public interface DrinkConsumer {
            float accept(float y, Entry drink);
        }
    }
}
