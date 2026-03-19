package moriz.orangesunshine.block.entity;

import java.util.Optional;

import moriz.orangesunshine.PSTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 16.11.14.
 * Updated by Sollace on 3 Jan 2023
 */
public class BottleRackBlockEntity extends BlockEntityWithInventory {
    private static final int[] SLOTS = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8};

    public BottleRackBlockEntity(BlockPos pos, BlockState state) {
        super(PSBlockEntities.BOTTLE_RACK, pos, state, 9);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return stack.is(PSTags.Items.BOTTLES);
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return direction.getAxis() == Direction.Axis.Y ? NO_SLOTS : SLOTS;
    }

    public InteractionResult insertItem(ItemStack stack, BlockHitResult hit, Direction facing) {
        return getHitPos(hit, facing).map(pos -> {
            int slot = getSlot(pos);
            if (slot >= 0 && slot < 9 && getItem(slot).isEmpty() && canPlaceItem(slot, stack)) {
                setItem(slot, stack.split(1));
                if (level != null) {
                    level.playSound(null, getBlockPos(), SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1, 1.5F);
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        }).orElse(InteractionResult.PASS);
    }

    public ItemStack extractItem(BlockHitResult hit, Direction facing) {
        return getHitPos(hit, facing).map(pos -> {
            int slot = getSlot(pos);
            if (slot < 0 || slot >= 9) {
                return ItemStack.EMPTY;
            }

            ItemStack removed = getItem(slot);
            if (removed.isEmpty()) {
                return ItemStack.EMPTY;
            }

            setItem(slot, ItemStack.EMPTY);
            if (level != null) {
                level.playSound(null, getBlockPos(), SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1, 1);
            }
            return removed;
        }).orElse(ItemStack.EMPTY);
    }

    private static int getSlot(Vec2 pos) {
        int x = (int)(pos.x * 3F);
        int y = (int)(pos.y * 3F);
        return x + (y * 3);
    }

    private static Optional<Vec2> getHitPos(BlockHitResult hit, Direction facing) {
        Direction direction = hit.getDirection();

        if (facing != direction) {
            if (direction.getAxis() == Direction.Axis.Y) {
                BlockPos pos = hit.getBlockPos();
                Vec3 relativePos = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
                float x = (float)relativePos.x;
                float y = (float)relativePos.y;
                float z = (float)relativePos.z;

                return switch (facing) {
                    default -> throw new IllegalStateException();
                    case NORTH -> Optional.of(new Vec2(1 - x, 1 - y));
                    case SOUTH -> Optional.of(new Vec2(x, 1 - y));
                    case WEST -> Optional.of(new Vec2(z, 1 - y));
                    case EAST -> Optional.of(new Vec2(1 - z, 1 - y));
                    case DOWN, UP -> Optional.empty();
                };
            }

            return Optional.empty();
        }

        BlockPos pos = hit.getBlockPos().relative(direction);
        Vec3 relativePos = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        float x = (float)relativePos.x;
        float y = (float)relativePos.y;
        float z = (float)relativePos.z;
        return switch (direction) {
            default -> throw new IllegalStateException();
            case NORTH -> Optional.of(new Vec2(1 - x, 1 - y));
            case SOUTH -> Optional.of(new Vec2(x, 1 - y));
            case WEST -> Optional.of(new Vec2(z, 1 - y));
            case EAST -> Optional.of(new Vec2(1 - z, 1 - y));
            case DOWN, UP -> Optional.empty();
        };
    }
}
