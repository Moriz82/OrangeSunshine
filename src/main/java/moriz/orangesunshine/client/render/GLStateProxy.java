package moriz.orangesunshine.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.GraphicsPreset;

public class GLStateProxy {
    public static boolean isColorSafeMode() {
        return Minecraft.getInstance().options.graphicsPreset().get() == GraphicsPreset.FAST;
    }
}
