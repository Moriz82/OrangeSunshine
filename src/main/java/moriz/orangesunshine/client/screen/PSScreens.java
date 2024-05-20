package moriz.orangesunshine.client.screen;

import moriz.orangesunshine.block.entity.FlaskBlockEntity;
import moriz.orangesunshine.screen.PSScreenHandlers;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

/**
 * @author Sollace
 * @since 13 Jan 2023
 */
public interface PSScreens {
    static void bootstrap() {
        HandledScreens.register(PSScreenHandlers.DRYING_TABLE, DryingTableScreen::new);
        HandledScreens.register(PSScreenHandlers.BARREL, BarrelScreen::new);
        HandledScreens.register(PSScreenHandlers.DISTILLERY, DistilleryScreen::new);
        HandledScreens.register(PSScreenHandlers.FLASK, FlaskScreen<FlaskBlockEntity>::new);
        HandledScreens.register(PSScreenHandlers.MASH_TUB, MushTubScreen::new);
    }
}
