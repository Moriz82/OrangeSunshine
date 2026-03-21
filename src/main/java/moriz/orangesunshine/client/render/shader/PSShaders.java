package moriz.orangesunshine.client.render.shader;

import org.jetbrains.annotations.Nullable;
import net.minecraft.client.renderer.ShaderInstance;

public class PSShaders {
    @Nullable
    private static ShaderInstance renderTypeZeroMatterProgram;

    public static ShaderInstance getRenderTypeZeroMatterProgram() {
        return renderTypeZeroMatterProgram;
    }

    public static void bootstrap() {
        renderTypeZeroMatterProgram = null;
    }
}
