package moriz.orangesunshine.client.screen;

import moriz.orangesunshine.block.entity.FlaskBlockEntity;
import moriz.orangesunshine.screen.PSScreenHandlers;
import net.minecraft.client.gui.screens.MenuScreens;

/**
 * @author Sollace
 * @since 13 Jan 2023
 */
public interface PSScreens {
    static void bootstrap() {
        MenuScreens.register(PSScreenHandlers.DRYING_TABLE, DryingTableScreen::new);
        MenuScreens.register(PSScreenHandlers.BARREL, BarrelScreen::new);
        MenuScreens.register(PSScreenHandlers.DISTILLERY, DistilleryScreen::new);
        MenuScreens.register(PSScreenHandlers.FLASK, FlaskScreen<FlaskBlockEntity>::new);
        MenuScreens.register(PSScreenHandlers.MASH_TUB, MushTubScreen::new);
        MenuScreens.register(PSScreenHandlers.MORTAR_PESTLE, MortarPestleScreen::new);
        MenuScreens.register(PSScreenHandlers.MIXING_TABLE, MixingTableScreen::new);
    }
}
