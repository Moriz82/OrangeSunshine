package moriz.orangesunshine.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public class GLStateProxy {
    public static boolean isColorSafeMode() {
        return Minecraft.getInstance().options.graphicsMode().equals(RenderSystem.GraphicsDebugState.FAST);
    }
}
