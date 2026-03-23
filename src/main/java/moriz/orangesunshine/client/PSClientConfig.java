package moriz.orangesunshine.client;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import moriz.orangesunshine.entity.drug.DrugType;
import org.joml.Vector2f;
import net.minecraft.world.phys.Vec2;

import net.minecraft.resources.Identifier;

public class PSClientConfig {
    public PSClientConfig.Audio audio = new Audio();
    public PSClientConfig.Visual visual = new Visual();

    public static class Audio {
        public String[] drugsWithBackgroundMusic = DrugType.REGISTRY.keySet().stream().map(Identifier::toString).toArray(String[]::new);
        private transient Set<String> drugsWithBackgroundMusicSet;

        private Set<String> loadMusicSet() {
            if (drugsWithBackgroundMusicSet == null) {
                drugsWithBackgroundMusicSet = Arrays.stream(drugsWithBackgroundMusic == null ? new String[0] : drugsWithBackgroundMusic)
                        .distinct()
                        .collect(Collectors.toSet());
            }
            return drugsWithBackgroundMusicSet;
        }

        public boolean hasBackgroundMusic(DrugType drugType) {
            return loadMusicSet().contains(drugType.id().toString());
        }

        public boolean setHasBackgroundMusic(DrugType drugType, boolean value) {
            Set<String> musicSet = loadMusicSet();
            if (value) {
                musicSet.add(drugType.id().toString());
            } else {
                musicSet.remove(drugType.id().toString());
            }
            drugsWithBackgroundMusic = musicSet.toArray(String[]::new);
            return value;
        }
    }

    public static class Visual {
        public float dofFocalPointNear = 0.2F;
        public float dofFocalBlurNear = 0;
        public float dofFocalPointFar = 128;
        public float dofFocalBlurFar = 0;

        public float pauseMenuBlur = 0;

        public boolean shader2DEnabled = true;
        public boolean shader3DEnabled = true;
        // (Sollace) made transient because this config was disabled before
        public transient boolean doShadows = false;

        public boolean doHeatDistortion = true;
        public boolean doWaterDistortion = true;
        public boolean doMotionBlur = true;

        public float sunFlareIntensity = 0.25F;
        public int shadowPixelsPerChunk = 256;

        public boolean waterOverlayEnabled = true;
        public boolean hurtOverlayEnabled = true;
        /** Scales alcohol/warmth/power overlays (0.5–3). */
        public float visualEffectIntensity = 1.0F;
        /** Logs drug sync packets and periodic client drug strength to the game log (for QA). */
        public boolean visualDebugLogging = false;
        /** Debug: disable the underwater_distortion post chain while isolating blackout issues. */
        public boolean debugDisableUnderwaterDistortion = false;
        /** Debug: disable the simple_effects_depth pass while isolating blackout issues. */
        public boolean debugDisableSimpleEffectsDepth = false;
        /** Master multiplier for drug background music (0–1). */
        public float drugMusicVolume = 1.0F;
        public Vec2 digitalEffectPixelRescale = new Vec2(0.05F, 0.05F);

        private transient float[] digitalEffectPixelRescaleF;

        public float[] getDigitalEffectPixelResize() {
            if (digitalEffectPixelRescaleF == null) {
                digitalEffectPixelRescaleF = new float[] { digitalEffectPixelRescale.x, digitalEffectPixelRescale.y };
            }
            return digitalEffectPixelRescaleF;
        }
    }
}
