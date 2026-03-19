package moriz.orangesunshine.item;

import moriz.orangesunshine.PSTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

import java.util.function.Consumer;

public class PaperBagItem extends Item {

    public PaperBagItem(Item.Properties settings) {
        super(settings);
        DispenserBlock.registerBehavior(this, new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource pointer, ItemStack stack) {
                Contents contents = getContents(stack);
                if (contents.isEmpty() || stack.getCount() > 1) {
                    return super.execute(pointer, stack);
                }

                Contents.Builder builder = new Contents.Builder(contents);
                ItemStack dispensed = builder.split(1);
                setContents(stack, builder.build());
                return super.execute(pointer, dispensed);
            }
        });
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack bag, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.SECONDARY || bag.getCount() != 1) {
            return false;
        }

        Contents contents = getContents(bag);
        Contents.Builder builder = new Contents.Builder(contents);
        ItemStack slotStack = slot.getItem();

        if (slotStack.isEmpty()) {
            if (!contents.isEmpty()) {
                builder.add(slot.safeInsert(builder.split(contents.stack().getMaxStackSize())));
                player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 1, 1);
                setContents(bag, builder.build());
                return true;
            }
        } else if (canPickUp(slotStack) && builder.canAdd(slotStack)) {
            int maxTaken = getMaxCountForItem(slotStack.getItem()) - contents.count();
            builder.add(slot.safeTake(slotStack.getCount(), maxTaken, player));
            player.playSound(SoundEvents.BUNDLE_INSERT, 1, 1);
            setContents(bag, builder.build());
            return true;
        }

        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack bag, ItemStack cursorStack, Slot slot, ClickAction action, Player player, SlotAccess cursorAccess) {
        if (action != ClickAction.SECONDARY || !(cursorStack.getItem() instanceof PaperBagItem || slot.allowModification(player)) || bag.getCount() != 1) {
            return false;
        }

        Contents contents = getContents(bag);
        Contents.Builder builder = new Contents.Builder(contents);

        if (cursorStack.isEmpty()) {
            if (!cursorAccess.set(builder.split(64))) {
                return false;
            }
            player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 1, 1);
            setContents(bag, builder.build());
            return true;
        }

        if (builder.canAdd(cursorStack) && canPickUp(cursorStack)) {
            ItemStack cursorCopy = cursorStack.copy();
            builder.add(cursorCopy);
            if (!cursorAccess.set(cursorCopy)) {
                return false;
            }
            player.playSound(SoundEvents.BUNDLE_INSERT, 1, 1);
            setContents(bag, builder.build());
            return true;
        }

        return false;
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {
        ItemStack bag = user.getItemInHand(hand);
        Contents contents = getContents(bag);

        if (!contents.isEmpty()) {
            int removedCount = user.isShiftKeyDown() ? 1 : contents.stack().getMaxStackSize();
            user.drop(removeItems(bag, contents, removedCount), false);
            user.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 1, 1);
            user.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        ItemStack bag = context.getItemInHand();
        Contents contents = getContents(bag);
        Contents.Builder builder = new Contents.Builder(contents);

        boolean pickedUpAny = !context.getLevel().getEntities(
                player,
                new AABB(context.getClickLocation(), context.getClickLocation()).inflate(0.25),
                entity -> entity instanceof ItemEntity itemEntity && canPickUp(itemEntity.getItem())
        ).stream().filter(entity -> {
            ItemEntity itemEntity = (ItemEntity)entity;
            if (builder.add(itemEntity.getItem())) {
                if (itemEntity.getItem().isEmpty()) {
                    itemEntity.discard();
                }
                return true;
            }
            return false;
        }).findAny().isEmpty();

        if (!pickedUpAny) {
            if (!contents.isEmpty()) {
                int removedCount = player.isShiftKeyDown() ? 1 : contents.stack().getMaxStackSize();
                player.drop(removeItems(bag, contents, removedCount), false);
                player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 1, 1);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        }

        if (bag.getCount() > 1) {
            ItemStack splitBag = bag.split(1);
            setContents(splitBag, builder.build());
            player.getInventory().add(splitBag);
        } else {
            setContents(bag, builder.build());
        }
        player.playSound(SoundEvents.BUNDLE_INSERT, 1, 1);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot equipmentSlot) {
        if (equipmentSlot != EquipmentSlot.MAINHAND || stack.getCount() != 1) {
            return;
        }

        if (entity instanceof Player player) {
            Inventory inventory = player.getInventory();
            int slot = inventory.getSelectedSlot();

            if (inventory.getItem(slot) != stack) {
                return;
            }

            Contents contents = getContents(stack);
            if (contents.isFull() || contents.isEmpty()) {
                return;
            }

            Contents.Builder builder = new Contents.Builder(contents);
            boolean changed = false;

            if (slot < inventory.getContainerSize() - 1) {
                changed |= builder.add(inventory.getItem(slot + 1));
            }
            if (slot > 0) {
                changed |= builder.add(inventory.getItem(slot - 1));
            }

            if (changed) {
                player.playSound(SoundEvents.BUNDLE_INSERT, 1, 1);
                setContents(stack, builder.build());
                inventory.setItem(slot, stack);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
        Contents contents = getContents(stack);
        if (!contents.isEmpty()) {
            consumer.accept(Component.literal(contents.count() + " x ").append(contents.stack().getHoverName()));
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        Contents contents = getContents(stack);
        if (!contents.isEmpty()) {
            return Component.translatable(getDescriptionId() + ".filled", contents.stack().getHoverName());
        }
        return super.getName(stack);
    }

    private static ItemStack removeItems(ItemStack bag, Contents contents, int count) {
        ItemStack dispensed = contents.stack().copyWithCount(Math.min(contents.count(), count));
        setContents(bag, new Contents(contents.stack(), contents.count() - dispensed.getCount()));
        return dispensed;
    }

    private boolean canPickUp(ItemStack incomingStack) {
        return incomingStack.is(PSTags.Items.CAN_GO_INTO_PAPER_BAG);
    }

    public static Contents getContents(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains("contents")) {
            return Contents.EMPTY;
        }
        return Contents.fromNbt(tag.getCompoundOrEmpty("contents"));
    }

    public static void setContents(ItemStack stack, Contents contents) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            if (contents.isEmpty()) {
                tag.remove("contents");
                return;
            }

            CompoundTag contentsTag = new CompoundTag();
            contents.writeNbt(contentsTag);
            tag.put("contents", contentsTag);
        });
    }

    public static int getMaxCountForItem(Item item) {
        if (item instanceof BottleItem) {
            return 1;
        }
        return 64 * 1000;
    }

    public record Contents(ItemStack stack, int count) {
        static final Contents EMPTY = new Contents(ItemStack.EMPTY, 0);

        public static Contents fromNbt(CompoundTag nbt) {
            if (!nbt.contains("stack")) {
                return EMPTY;
            }

            ItemStack stack = ItemStack.CODEC.parse(NbtOps.INSTANCE, nbt.get("stack")).result().orElse(ItemStack.EMPTY);
            return from(stack, Math.max(0, nbt.getIntOr("count", 0)));
        }

        public static Contents from(ItemStack stack, int count) {
            if (count <= 0 || stack.isEmpty()) {
                return EMPTY;
            }
            return new Contents(stack, count);
        }

        public boolean isFull() {
            return count >= getMaxCountForItem(stack.getItem());
        }

        public boolean isEmpty() {
            return stack.isEmpty() || count <= 0;
        }

        public CompoundTag writeNbt(CompoundTag nbt) {
            ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, stack).result().ifPresent(tag -> nbt.put("stack", tag));
            nbt.putInt("count", count);
            return nbt;
        }

        static class Builder {
            private ItemStack stack;
            private int count;

            Builder(Contents contents) {
                this.stack = contents.stack();
                this.count = contents.count();
            }

            public boolean canAdd(ItemStack incomingStack) {
                if (incomingStack.getItem() instanceof PaperBagItem) {
                    return false;
                }
                if (this.stack.isEmpty()) {
                    return true;
                }
                return ItemStack.isSameItemSameComponents(this.stack, incomingStack) && count < getMaxCountForItem(incomingStack.getItem());
            }

            public boolean add(ItemStack incomingStack) {
                if (!canAdd(incomingStack)) {
                    return false;
                }

                if (this.stack.isEmpty()) {
                    this.stack = incomingStack.copyWithCount(1);
                }

                count += incomingStack.split(getMaxCountForItem(incomingStack.getItem()) - count).getCount();
                return true;
            }

            public ItemStack split(int count) {
                ItemStack dispensed = stack.copyWithCount(Math.min(this.count, count));
                this.count -= dispensed.getCount();
                return dispensed;
            }

            public Contents build() {
                return Contents.from(stack, count);
            }
        }
    }
}
