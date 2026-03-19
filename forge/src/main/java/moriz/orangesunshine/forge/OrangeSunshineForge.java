package moriz.orangesunshine.forge;

import moriz.orangesunshine.OrangeSunshinePlatform;
import net.minecraftforge.fml.common.Mod;

@Mod(OrangeSunshinePlatform.MOD_ID)
public final class OrangeSunshineForge {
    public OrangeSunshineForge() {
        OrangeSunshinePlatform.init("Forge");
    }
}
