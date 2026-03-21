package com.BrotherHoodOfDiethylamide.OrangeSunshine.client;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugEffects;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugInstance;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugHallucinationManager;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Map;

/**
 * In-game debug HUD for drug visual effects. Toggle with F7.
 *
 * Shows:
 *  1. Active drugs and their ADSR envelope levels
 *  2. All DrugEffects channels (what values go to the shader)
 *  3. Portedpsych hallucination system state (legacy random-based effects)
 *
 * Use /setdrug <name> to apply drugs, /setdrug clear to reset.
 */
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = OrangeSunshine.MODID)
public class DrugDebugOverlay {

    public static boolean visible = false;
    private static boolean prevKeyState = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_F7);
        if (keyDown && !prevKeyState) {
            visible = !visible;
        }
        prevKeyState = keyDown;
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!visible) return;
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) return;

        EntityPlayer player = mc.player;
        FontRenderer fr = mc.fontRenderer;
        ScaledResolution res = new ScaledResolution(mc);

        int x = res.getScaledWidth() - 140;
        int y = 2;
        int lineH = fr.FONT_HEIGHT + 1;

        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();

        // ---- SECTION 1: Active drugs ----
        fr.drawStringWithShadow("§6=== DRUGS ===", x, y, 0xFFFFFF);
        y += lineH;

        Map<Drug, Float> actives = Drug.getActiveDrugs(player);
        List<DrugInstance> sources = Drug.getDrugSources(player);

        if (actives.isEmpty()) {
            fr.drawStringWithShadow("§7none (/setdrug <name>)", x, y, 0xFFFFFF);
            y += lineH;
        } else {
            for (Map.Entry<Drug, Float> entry : actives.entrySet()) {
                float lvl = entry.getValue();
                int col = lvl > 0.7f ? 0x55FF55 : (lvl > 0.3f ? 0xFFAA00 : 0xFF5555);
                String name = Drug.toName(entry.getKey());
                String bar = buildBar(lvl, 6);
                fr.drawStringWithShadow(shorten(name, 10) + " " + bar, x, y, col);
                y += lineH;
            }
        }
        fr.drawStringWithShadow("§7" + sources.size() + " src(s)", x, y, 0xFFFFFF);
        y += lineH + 1;

        // ---- SECTION 2: DrugEffects channels ----
        fr.drawStringWithShadow("§b=== FX CHANNELS ===", x, y, 0xFFFFFF);
        y += lineH;

        DrugEffects de = Drug.getDrugEffects(player);

        y = drawChannel(fr, x, y, lineH, "BIG_WAVES", de.BIG_WAVES.getValue(), 0x4499FF);
        y = drawChannel(fr, x, y, lineH, "SMALL_WVS", de.SMALL_WAVES.getValue(), 0x4499FF);
        y = drawChannel(fr, x, y, lineH, "WIGGLE", de.WIGGLE_WAVES.getValue(), 0x4499FF);
        y = drawChannel(fr, x, y, lineH, "WLD_DEFRM", de.WORLD_DEFORMATION.getValue(), 0x4499FF);
        y = drawChannel(fr, x, y, lineH, "SATURATN", de.SATURATION.getValue(), 0xFF88FF);
        y = drawChannel(fr, x, y, lineH, "HUE_AMP", de.HUE_AMPLITUDE.getValue(), 0xFF88FF);
        y = drawChannel(fr, x, y, lineH, "KALEIDO", de.KALEIDOSCOPE_INTENSITY.getValue(), 0xFF44AA);
        y = drawChannel(fr, x, y, lineH, "RECURSION", de.RECURSION.getValue(), 0xFF44AA);
        y = drawChannel(fr, x, y, lineH, "BLOOM", de.BLOOM_RADIUS.getValue(), 0xFFFF44);
        y = drawChannel(fr, x, y, lineH, "BRIGHTNSS", de.BRIGHTNESS.getValue(), 0xFFFF44);
        y = drawChannel(fr, x, y, lineH, "CAM_TRMBL", de.CAMERA_TREMBLE.getValue(), 0xFF8844);
        y = drawChannel(fr, x, y, lineH, "CAM_INERT", de.CAMERA_INERTIA.getValue(), 0xFF8844);
        y = drawChannel(fr, x, y, lineH, "BUMPY", de.BUMPY.getValue(), 0xAA44FF);
        y = drawChannel(fr, x, y, lineH, "MOV_SPEED", de.MOVEMENT_SPEED.getValue(), 0x44FF88);
        y += 1;

        // ---- SECTION 3: Portedpsych hallucination state ----
        DrugProperties dp = DrugProperties.getDrugProperties(player);
        if (dp != null) {
            fr.drawStringWithShadow("§d=== PSYCH STATE ===", x, y, 0xFFFFFF);
            y += lineH;

            String activeList = dp.hallucinationManager.activeHallucinations.toString();
            fr.drawStringWithShadow("§7active:" + activeList, x, y, 0xFFFFFF);
            y += lineH;

            // Show non-zero hallucination values
            String[] hNames = {"ENT","DESAT","SAT+","SLOWCOL","QKCOL","BIGWV","SMWV","WIGL","PLS","FRCL","WRLD","BLOOM","CBLM","CNTR"};
            for (int k = 0; k < hNames.length; k++) {
                float val = dp.hallucinationManager.hallucinationValues.getOrDefault(k, 0f);
                if (val > 0.001f) {
                    boolean active = dp.hallucinationManager.activeHallucinations.contains(k);
                    int col = active ? 0x55FF55 : 0x888888;
                    fr.drawStringWithShadow(String.format("§7[%d]%-5s %s", k, hNames[k], fmt(val)), x, y, col);
                    y += lineH;
                }
            }
        }

        y += 2;
        fr.drawStringWithShadow("§7[F7] hide  /setdrug <name|clear|all>", x, y, 0xFFFFFF);

        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    private static int drawChannel(FontRenderer fr, int x, int y, int lineH, String label, float value, int activeColor) {
        boolean active = Math.abs(value) > 0.001f;
        String bar = buildBar(Math.min(Math.abs(value), 1f), 5);
        String line = String.format("%-9s %s %s", label, bar, fmt(value));
        fr.drawStringWithShadow(line, x, y, active ? activeColor : 0x444444);
        return y + lineH;
    }

    /** Builds a mini bar like [####  ] from 0..1 */
    private static String buildBar(float v, int width) {
        int filled = Math.round(v * width);
        filled = Math.max(0, Math.min(filled, width));
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < width; i++) sb.append(i < filled ? '#' : ' ');
        sb.append("]");
        return sb.toString();
    }

    private static String fmt(float v) {
        return String.format("%.2f", v);
    }

    private static String shorten(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max);
    }
}
