package moriz.orangesunshine.client.render;

import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public final class FluidBoxRenderer {
    private FluidBoxRenderer() {
    }

    public record FluidAppearance(Identifier texture, TextureAtlasSprite sprite, int color) {
        public static FluidAppearance of(SimpleFluid fluid, ItemStack stack) {
            Minecraft client = Minecraft.getInstance();
            TextureAtlas atlas = (TextureAtlas) client.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
            TextureAtlasSprite sprite = fluid.getFlowTexture(stack)
                    .map(atlas::getSprite)
                    .orElseGet(() -> client.getBlockRenderer().getBlockModelShaper().getParticleIcon(Blocks.WATER.defaultBlockState()));

            Identifier texture = sprite.atlasLocation();
            return new FluidAppearance(texture, sprite, fluid.getColor(stack));
        }

        public static int getItemColor(SimpleFluid fluid, ItemStack stack) {
            return fluid.getColor(stack);
        }

        public float[] rgba() {
            return new float[] {
                    MathUtils.r(color),
                    MathUtils.g(color),
                    MathUtils.b(color),
                    1
            };
        }
    }
}
