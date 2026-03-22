package moriz.orangesunshine.fabric.client;

import moriz.orangesunshine.client.render.DrugRenderer;
import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.entity.drug.hallucination.HallucinationManager;
import moriz.orangesunshine.entity.drug.type.SimpleDrug;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/**
 * F7 overlay showing live drug levels and shader FX channel values.
 * Mirrors the layout of the 1.12.2 DrugDebugOverlay.
 */
public final class DrugDebugHud {
    private static boolean visible = false;

    public static boolean isVisible() { return visible; }
    public static void toggle() { visible = !visible; }

    public static void render(GuiGraphics gui, Minecraft mc) {
        if (!visible || mc.player == null) return;

        DrugProperties props = DrugProperties.of(mc.player);
        HallucinationManager h = props.getHallucinations();
        float td = 0f; // tickDelta — use 0 for display; hallucination values converge each tick

        int screenW = mc.getWindow().getGuiScaledWidth();
        final int COL_W = 155;
        int x = screenW - COL_W;
        int y = 2;
        final int LINE = mc.font.lineHeight + 1;

        // ── SECTION 1: DRUGS ────────────────────────────────────────────
        gui.drawString(mc.font, "§6=== DRUGS ===", x, y, 0xFFFFFF); y += LINE;

        boolean anyDrug = false;
        for (DrugType type : DrugType.REGISTRY) {
            Drug drug = props.getDrug(type);
            double act = drug.getActiveValue();
            double des = (drug instanceof SimpleDrug sd) ? sd.getDesiredValue() : act;
            if (act < 0.001 && des < 0.001) continue;
            anyDrug = true;

            String name = shorten(type.id().getPath(), 12);
            String bar  = buildBar((float) act, 6);
            int col = act > 0.7f ? 0x55FF55 : (act > 0.3f ? 0xFFAA00 : 0xFF5555);
            gui.drawString(mc.font, String.format("§f%-12s %s", name, bar), x, y, col);
            y += LINE;
            gui.drawString(mc.font, String.format("  §7des=§f%.3f §7act=§f%.3f", des, act), x, y, 0xAAAAAA);
            y += LINE;
        }
        if (!anyDrug) {
            gui.drawString(mc.font, "§7none (/give <drug>)", x, y, 0x888888); y += LINE;
        }
        y += 2;

        // ── SECTION 2: FX CHANNELS ──────────────────────────────────────
        gui.drawString(mc.font, "§b=== FX CHANNELS ===", x, y, 0xFFFFFF); y += LINE;

        y = chan(gui, mc, x, y, LINE, "DESAT",    h.getDesaturation(td),               0x4499FF);
        y = chan(gui, mc, x, y, LINE, "SUPER_SAT",h.getColorIntensification(td),        0x4499FF);
        y = chan(gui, mc, x, y, LINE, "SLOW_COL", h.getSlowColorRotation(td),           0x4499FF);
        y = chan(gui, mc, x, y, LINE, "QUICK_COL",h.getQuickColorRotation(td),          0x4499FF);
        y = chan(gui, mc, x, y, LINE, "BLOOM",    h.getBloom(td),                       0xFFFF44);
        y = chan(gui, mc, x, y, LINE, "BIG_WVS",  h.getBigWaveStrength(td),             0x44FFAA);
        y = chan(gui, mc, x, y, LINE, "SML_WVS",  h.getSmallWaveStrength(td),           0x44FFAA);
        y = chan(gui, mc, x, y, LINE, "WIGGLE",   h.getWiggleWaveStrength(td),          0x44FFAA);
        y = chan(gui, mc, x, y, LINE, "ENTITIES", h.getHallucinationStrength(td),       0xFF88FF);
        y = chan(gui, mc, x, y, LINE, "WOBBLE",   props.getModifier(Drug.VIEW_WOBBLYNESS),        0xFF8844);
        y = chan(gui, mc, x, y, LINE, "TREMBLE",  props.getModifier(Drug.VIEW_TREMBLE_STRENGTH),  0xFF8844);
        y = chan(gui, mc, x, y, LINE, "MOT_BLUR", props.getModifier(Drug.MOTION_BLUR),            0xFFFF88);
        y = chan(gui, mc, x, y, LINE, "MENU_BLUR",DrugRenderer.INSTANCE.getMenuBlur(),            0xFFFF88);
        y = chan(gui, mc, x, y, LINE, "HEAT_DIST",DrugRenderer.INSTANCE.getEnvironmentalEffects().getHeatDistortion(), 0xFF6644);
        y = chan(gui, mc, x, y, LINE, "WATER",    DrugRenderer.INSTANCE.getEnvironmentalEffects().getWaterDistortion(), 0x44AAFF);
        y += 2;

        // ── SECTION 3: SHADER STATUS ─────────────────────────────────────
        gui.drawString(mc.font, "§c=== SHADER STATUS ===", x, y, 0xFFFFFF); y += LINE;
        gui.drawString(mc.font, "§cSTUB §7- no GL effects run", x, y, 0xFF4444); y += LINE;
        gui.drawString(mc.font, "§7PostChain needs 1.21.11 port", x, y, 0x888888); y += LINE;
        y += 2;
        gui.drawString(mc.font, "§7[F7] hide", x, y, 0x555555);
    }

    /** Draws one FX channel row; grey=zero, colored=active. Returns new y. */
    private static int chan(GuiGraphics gui, Minecraft mc, int x, int y, int line,
                            String label, float value, int activeColor) {
        boolean active = value > 0.001f;
        String bar  = buildBar(Math.min(Math.abs(value), 1f), 5);
        String text = String.format("%-9s %s %s", label, bar, fmt(value));
        gui.drawString(mc.font, text, x, y, active ? activeColor : 0x444444);
        return y + line;
    }

    private static String buildBar(float v, int width) {
        int filled = Math.max(0, Math.min(width, Math.round(v * width)));
        return "[" + "#".repeat(filled) + " ".repeat(width - filled) + "]";
    }

    private static String fmt(float v) { return String.format("%.2f", v); }

    private static String shorten(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max);
    }
}
