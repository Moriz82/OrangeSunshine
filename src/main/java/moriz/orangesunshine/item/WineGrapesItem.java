/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.item;

import moriz.orangesunshine.block.LatticeBlock;
import moriz.orangesunshine.block.PSBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public class WineGrapesItem extends SpecialFoodItem {
    public WineGrapesItem(Item.Properties settings, int eatSpeed) {
        super(settings, eatSpeed);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || context.isSecondaryUseActive() || !player.mayUseItemAt(context.getClickedPos(), context.getClickedFace(), context.getItemInHand())) {
            return InteractionResult.PASS;
        }

        BlockPos pos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(pos);

        if (state.is(PSBlocks.LATTICE)) {
            context.getLevel().playSound(null, pos, SoundEvents.AZALEA_LEAVES_HIT, SoundSource.BLOCKS, 1, 1);
            context.getLevel().setBlock(pos, LatticeBlock.copyStateProperties(PSBlocks.WINE_GRAPE_LATTICE.defaultBlockState(), state), 3);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
