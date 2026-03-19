/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.hallucination;

import moriz.orangesunshine.PSTags;
import net.minecraft.world.entity.player.Player;

public class MultipleEntityHallucination extends EntityHallucination {
    public MultipleEntityHallucination(Player player) {
        super(player, PSTags.Entities.MULTIPLE_ENTITY_HALLUCINATIONS);
    }
}
