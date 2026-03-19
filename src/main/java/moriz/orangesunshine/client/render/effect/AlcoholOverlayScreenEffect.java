package moriz.orangesunshine.client.render.effect;

import moriz.orangesunshine.client.render.RenderUtil;
import moriz.orangesunshine.entity.drug.*;
import moriz.orangesunshine.entity.drug.type.AlcoholDrug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Mth;

public class AlcoholOverlayScreenEffect extends DrugOverlayScreenEffect<AlcoholDrug> {
    public AlcoholOverlayScreenEffect() {
        super(DrugType.ALCOHOL);
    }

    @Override
    protected void render(PoseStack matrices, MultiBufferSource vertices, int width, int height, float ticks, DrugProperties properties, AlcoholDrug drug) {
        float alcohol = (float)drug.getActiveValue();
        if (alcohol <= 0) {
            return;
        }

        float overlayAlpha = Math.min(0.8F, (Mth.sin(ticks / 80F) * alcohol * 0.5F + alcohol));
        Sprite sprite = MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelParticleSprite(Blocks.NETHER_PORTAL.getDefaultState());
        RenderUtil.drawOverlay(matrices, overlayAlpha * 0.25f, width, height, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
                sprite.getMinU(),
                sprite.getMinV(),
                sprite.getMaxU(),
                sprite.getMaxV(),
                0
        );
    }
}
