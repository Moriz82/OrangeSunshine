package moriz.orangesunshine.neoforge;

import moriz.orangesunshine.OrangeSunshinePlatform;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(OrangeSunshinePlatform.MOD_ID)
public final class OrangeSunshineNeoForge {
    public OrangeSunshineNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegister);
    }

    private void onRegister(RegisterEvent event) {
        OrangeSunshinePlatform.init("NeoForge");
    }
}
