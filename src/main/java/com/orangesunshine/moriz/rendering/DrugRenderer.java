package com.orangesunshine.moriz.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.orangesunshine.moriz.OrangeSunshine;
import com.orangesunshine.moriz.drugs.Drug;
import com.orangesunshine.moriz.mixin.client.InvokerConfigOF;
import com.orangesunshine.moriz.rendering.shaders.RenderUtil;
import com.orangesunshine.moriz.rendering.shaders.ShaderRenderer;
import com.orangesunshine.moriz.rendering.shaders.post.PostShaders;
import net.minecraft.client.Minecraft;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = OrangeSunshine.MODID)
public class DrugRenderer {
    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (RenderUtil.hasOptifine) { // Checks for optifine shaders
            if (InvokerConfigOF.callIsShaders() && ShaderRenderer.useShader) {
                ShaderRenderer.clear(true);
                ShaderRenderer.useShader = false;
                PostShaders.useShaders = false;
            } else if (!InvokerConfigOF.callIsShaders() && !ShaderRenderer.useShader) {
                ShaderRenderer.setup();
                ShaderRenderer.useShader = true;
                PostShaders.useShaders = true;
            }
        }

        if (mc.level == null) return;

        if (event.phase == TickEvent.Phase.START) {
            Map<Drug, Float> activeDrugs = Drug.getActiveDrugs(mc.player);

            activeDrugs.forEach((drug, effect) -> {
                if (effect > 0) drug.renderTick(Drug.getDrugEffects(), effect);
            });

            MouseSmootherEffect.INSTANCE.setAmplifier(Drug.getDrugEffects().CAMERA_INERTIA.getClamped());
        } else {
            Drug.getDrugEffects().reset(true);
        }
    }

    private static final CameraTrembleEffect trembleEffect = new CameraTrembleEffect();
    @SubscribeEvent
    public static void onBobHurt(LivingHurtEvent event) {
        /*PoseStack
        if (event.getEntity() instanceof Player){
            trembleEffect.setAmplitude(Drug.getDrugEffects().CAMERA_TREMBLE.getValue());
           trembleEffect.tick((Player)(event.getEntity().get);
        }*/
    }
}
