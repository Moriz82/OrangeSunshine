package moriz.orangesunshine.fabric.client;

import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.entity.drug.influence.DrugInfluence;
import moriz.orangesunshine.entity.drug.type.SimpleDrug;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

/**
 * F7 overlay showing live drug values, pending influences, and modifier readouts.
 * Toggle with F7 (registered in OrangeSunshineFabricClient).
 */
public final class DrugDebugHud {
    private static boolean visible = false;

    public static boolean isVisible() {
        return visible;
    }

    public static void toggle() {
        visible = !visible;
    }

    public static void render(GuiGraphics gui, Minecraft mc) {
        if (!visible || mc.player == null) return;

        DrugProperties props = DrugProperties.of(mc.player);
        int x = 4;
        int y = 4;
        final int LINE = 9;
        final int COL_ACTIVE   = 0x00FF88; // green — visually active
        final int COL_BUILDING = 0xFFFF55; // yellow — desired > active (still building)
        final int COL_TRACE    = 0xAAAAAA; // grey — near-zero trace
        final int COL_HEADER   = 0xFFDD44;
        final int COL_DIM      = 0x888888;

        // ── header ───────────────────────────────────────────────────────
        gui.drawString(mc.font, "§e[OrangeSunshine Drug Debug]  §7F7 to hide", x, y, COL_HEADER);
        y += LINE + 1;

        // ── drug values ──────────────────────────────────────────────────
        List<String> activeLines   = new ArrayList<>();
        List<String> traceLines    = new ArrayList<>();

        for (DrugType type : DrugType.REGISTRY) {
            Drug drug = props.getDrug(type);
            double active  = drug.getActiveValue();
            double desired = (drug instanceof SimpleDrug sd) ? sd.getDesiredValue() : active;

            if (active < 0.001 && desired < 0.001) continue;

            String name = type.id().getPath();
            String bar  = progressBar(active, 10);
            String line;
            int color;

            if (active >= 0.01) {
                line  = String.format("§a%-18s §7des=§f%.3f §7act=§a%.3f §7%s", name, desired, active, bar);
                color = COL_ACTIVE;
            } else {
                line  = String.format("§e%-18s §7des=§e%.3f §7act=§7%.3f  (building)", name, desired, active);
                color = COL_BUILDING;
            }

            if (active >= 0.001) activeLines.add(line);
            else                  traceLines.add(line);
        }

        if (activeLines.isEmpty() && traceLines.isEmpty()) {
            gui.drawString(mc.font, "§7No active drugs — consume something first", x, y, COL_DIM);
            y += LINE;
        } else {
            gui.drawString(mc.font, "§7--- drugs ---", x, y, COL_DIM);
            y += LINE;
            for (String l : activeLines) { gui.drawString(mc.font, l, x, y, 0xFFFFFF); y += LINE; }
            for (String l : traceLines)  { gui.drawString(mc.font, l, x, y, COL_TRACE); y += LINE; }
        }

        // ── modifiers ────────────────────────────────────────────────────
        y += 2;
        gui.drawString(mc.font, "§7--- key modifiers ---", x, y, COL_DIM);
        y += LINE;
        gui.drawString(mc.font, String.format("§7wobble=§f%.3f  tremble=§f%.3f  alcohol=§f%.3f",
                props.getModifier(Drug.VIEW_WOBBLYNESS),
                props.getModifier(Drug.VIEW_TREMBLE_STRENGTH),
                props.getModifier(Drug.SOUND_VOLUME)), x, y, 0xFFFFFF);
        y += LINE;
        gui.drawString(mc.font, String.format("§7color-hall=§f%.3f  move-hall=§f%.3f  ctx-hall=§f%.3f",
                props.getModifier(Drug.COLOR_HALLUCINATION_STRENGTH),
                props.getModifier(Drug.MOVEMENT_HALLUCINATION_STRENGTH),
                props.getModifier(Drug.CONTEXTUAL_HALLUCINATION_STRENGTH)), x, y, 0xFFFFFF);
        y += LINE;
        gui.drawString(mc.font, String.format("§7speed=§f%.3f  dig=§f%.3f  blur=§f%.3f",
                props.getModifier(Drug.SPEED),
                props.getModifier(Drug.DIG_SPEED),
                props.getModifier(Drug.MOTION_BLUR)), x, y, 0xFFFFFF);

        // ── pending influences ────────────────────────────────────────────
        // DrugProperties doesn't expose influences directly; we use the debug toString
        // we can access via the DrugProperties.onTick side-effect log below.
        // For now show count hint only.
        y += LINE + 2;
        gui.drawString(mc.font, "§7(check log for influence pipeline — enable visualDebugLogging in Mod Menu)", x, y, COL_DIM);
    }

    private static String progressBar(double value, int width) {
        int filled = (int) Math.round(Math.min(1.0, value) * width);
        return "[" + "=".repeat(filled) + " ".repeat(width - filled) + "]";
    }
}
