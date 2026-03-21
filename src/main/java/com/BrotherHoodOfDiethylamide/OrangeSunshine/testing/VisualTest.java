package com.BrotherHoodOfDiethylamide.OrangeSunshine.testing;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugInstance;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.*;
import java.util.Map;

/**
 * Visual regression test.  Enabled via -Dorangesunshine.visualTests=true.
 *
 * Screenshots are taken DIRECTLY from the Minecraft framebuffer during a render
 * frame (RenderGameOverlayEvent.Post) so they always capture the actual game
 * output, regardless of which window has desktop focus.
 *
 * Flow:
 *   1. Create a flat creative world.
 *   2. Wait 50 ticks for the renderer to settle.
 *   3. Request baseline screenshot (captured next render frame).
 *   4. Apply every drug at potency=1.0 for 600 ticks.
 *   5. Wait 250 ticks for ADSR to peak.
 *   6. Request peak screenshot (captured next render frame).
 *   7. Write visual_test_done flag → shell script stops recording.
 *   8. Clear drugs, exit.
 *
 * Screenshots are written to:
 *   <gameDir>/visual_test_baseline.png
 *   <gameDir>/visual_test_peak.png
 */
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = OrangeSunshine.MODID)
public class VisualTest {

    private static final boolean ENABLED = Boolean.getBoolean("orangesunshine.visualTests");

    private enum State {
        INIT,
        WAIT_WORLD,
        WARMUP,
        BASELINE_REQUEST,
        BASELINE_WAIT_CAPTURE,
        APPLY_DRUGS,
        RAMP_UP,
        PEAK_REQUEST,
        PEAK_WAIT_CAPTURE,
        DONE_SIGNAL,
        CLEANUP,
        FINISHED
    }

    private static State state = State.INIT;
    private static int ticks = 0;

    // Set by tick handler, cleared by render handler after screenshot is taken.
    private static volatile String captureRequest = null;
    // Set by render handler to signal the tick handler that capture is done.
    private static volatile boolean captureCompleted = false;

    // ─── Tick handler ──────────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (!ENABLED || event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getMinecraft();

        switch (state) {

            case INIT: {
                if (mc.world == null && mc.currentScreen != null) {
                    OrangeSunshine.logger.info("[VISUAL] Creating visual test world...");
                    WorldSettings ws = new WorldSettings(12345L, GameType.CREATIVE, false, false, WorldType.FLAT);
                    ws.enableCommands();
                    mc.launchIntegratedServer("__visualtest__", "VisualTest", ws);
                    state = State.WAIT_WORLD;
                    ticks = 0;
                }
                break;
            }

            case WAIT_WORLD: {
                ticks++;
                if (mc.player != null && mc.world != null && !mc.isGamePaused()) {
                    OrangeSunshine.logger.info("[VISUAL] World loaded after " + ticks + " ticks");
                    state = State.WARMUP;
                    ticks = 0;
                }
                if (ticks > 1200) {
                    OrangeSunshine.logger.error("[VISUAL] World load timed out");
                    writeFile(new File("."), "visual_test_done", "timeout");
                    FMLCommonHandler.instance().exitJava(1, false);
                }
                break;
            }

            case WARMUP: {
                ticks++;
                if (ticks >= 50) {
                    state = State.BASELINE_REQUEST;
                    ticks = 0;
                }
                break;
            }

            case BASELINE_REQUEST: {
                OrangeSunshine.logger.info("[VISUAL] Requesting baseline screenshot...");
                captureCompleted = false;
                captureRequest = "baseline";
                state = State.BASELINE_WAIT_CAPTURE;
                ticks = 0;
                break;
            }

            case BASELINE_WAIT_CAPTURE: {
                ticks++;
                if (captureCompleted) {
                    OrangeSunshine.logger.info("[VISUAL] Baseline screenshot captured");
                    captureCompleted = false;
                    state = State.APPLY_DRUGS;
                    ticks = 0;
                } else if (ticks > 200) {
                    OrangeSunshine.logger.warn("[VISUAL] Baseline capture timeout — continuing");
                    state = State.APPLY_DRUGS;
                    ticks = 0;
                }
                break;
            }

            case APPLY_DRUGS: {
                EntityPlayer player = mc.player;
                for (Map.Entry<String, Drug> entry : DrugRegistry.DRUGS.entrySet()) {
                    Drug.addDrug(player, new DrugInstance(entry.getValue(), 0, 1.0f, 600));
                }
                OrangeSunshine.logger.info("[VISUAL] Applied all " + DrugRegistry.DRUGS.size() + " drugs");
                state = State.RAMP_UP;
                ticks = 0;
                break;
            }

            case RAMP_UP: {
                ticks++;
                if (ticks >= 250) { // ~12 seconds — sufficient for ADSR to peak
                    state = State.PEAK_REQUEST;
                    ticks = 0;
                }
                break;
            }

            case PEAK_REQUEST: {
                OrangeSunshine.logger.info("[VISUAL] Requesting peak screenshot...");
                captureCompleted = false;
                captureRequest = "peak";
                state = State.PEAK_WAIT_CAPTURE;
                ticks = 0;
                break;
            }

            case PEAK_WAIT_CAPTURE: {
                ticks++;
                if (captureCompleted) {
                    OrangeSunshine.logger.info("[VISUAL] Peak screenshot captured");
                    captureCompleted = false;
                    state = State.DONE_SIGNAL;
                    ticks = 0;
                } else if (ticks > 200) {
                    OrangeSunshine.logger.warn("[VISUAL] Peak capture timeout — continuing");
                    state = State.DONE_SIGNAL;
                    ticks = 0;
                }
                break;
            }

            case DONE_SIGNAL: {
                OrangeSunshine.logger.info("[VISUAL] Writing done signal...");
                writeFile(new File("."), "visual_test_done", "ok");
                state = State.CLEANUP;
                ticks = 0;
                break;
            }

            case CLEANUP: {
                ticks++;
                if (ticks >= 20) {
                    if (mc.player != null) {
                        Drug.clearDrugs(mc.player);
                    }
                    OrangeSunshine.logger.info("[VISUAL] Done. Exiting.");
                    state = State.FINISHED;
                    new Thread(() -> {
                        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                        deleteDir(new File(new File("."), "saves/__visualtest__"));
                        FMLCommonHandler.instance().exitJava(0, false);
                    }).start();
                }
                break;
            }

            case FINISHED:
                break;
        }
    }

    // ─── Render handler — captures screenshots from the live framebuffer ───────

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!ENABLED) return;
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        String req = captureRequest;
        if (req == null) return;
        captureRequest = null;  // claim the request

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null) {
            captureCompleted = true;
            return;
        }

        String filename = "visual_test_" + req + ".png";
        File gameDir = new File(".");

        try {
            ScreenShotHelper.saveScreenshot(
                    gameDir,
                    filename,
                    mc.displayWidth,
                    mc.displayHeight,
                    mc.getFramebuffer()
            );
            OrangeSunshine.logger.info("[VISUAL] Screenshot saved: " + new File(gameDir, "screenshots/" + filename).getAbsolutePath());
        } catch (Exception e) {
            OrangeSunshine.logger.error("[VISUAL] Screenshot failed for " + req, e);
        }

        captureCompleted = true;
    }

    private static void writeFile(File gameDir, String filename, String content) {
        try (FileWriter fw = new FileWriter(new File(gameDir, filename))) {
            fw.write(content);
        } catch (IOException e) {
            OrangeSunshine.logger.error("[VISUAL] Failed to write " + filename, e);
        }
    }

    private static void deleteDir(File dir) {
        if (!dir.exists()) return;
        File[] files = dir.listFiles();
        if (files != null) for (File f : files) {
            if (f.isDirectory()) deleteDir(f);
            else f.delete();
        }
        dir.delete();
    }
}
