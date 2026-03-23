package moriz.orangesunshine.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Matches vanilla {@code CampfireRenderer} / {@code ShelfRenderer} item extraction and submission.
 */
public final class ItemSubmitHelper {
    private ItemSubmitHelper() {
    }

    public static void updateForBlock(ItemModelResolver resolver, ItemStackRenderState target, ItemStack stack, ItemDisplayContext ctx, Level level, int seed) {
        target.clear();
        resolver.updateForTopItem(target, stack, ctx, level, null, seed);
    }

    public static void submit(ItemStackRenderState state, PoseStack poseStack, SubmitNodeCollector collector, int packedLight) {
        state.submit(poseStack, collector, packedLight, OverlayTexture.NO_OVERLAY, 0);
    }
}
