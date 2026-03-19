/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PeyoteBlockEntity extends SyncedBlockEntity {
    public PeyoteBlockEntity(BlockPos pos, BlockState state) {
        super(PSBlockEntities.PEYOTE, pos, state);
    }
}
