/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.screen;

import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotDryingTableResult extends Slot {
    private final Player player;
    private int amount;

    public SlotDryingTableResult(Player player, Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.player = player;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack remove(int amount) {
        if (hasItem()) {
            this.amount += Math.min(amount, getItem().getCount());
        }
        return super.remove(amount);
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        checkTakeAchievements(stack);
        super.onTake(player, stack);
    }

    @Override
    protected void onQuickCraft(ItemStack stack, int amount) {
        this.amount += amount;
        checkTakeAchievements(stack);
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        stack.onCraftedBy(player, amount);

        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            int amount = this.amount;
            float xpPerItem = 5;
            int xp;

            if (xpPerItem == 0) {
                amount = 0;
            } else if (xpPerItem < 1) {
                xp = Mth.floor(amount * xpPerItem);

                // bonus experience
                if (xp < Mth.ceil(amount * xpPerItem) && (float)Math.random() < amount * xpPerItem - xp) {
                    ++xp;
                }

                amount = xp;
            }

            ExperienceOrb.award(serverPlayer.level(), player.position(), amount);
        }

        this.amount = 0;
    }
}
