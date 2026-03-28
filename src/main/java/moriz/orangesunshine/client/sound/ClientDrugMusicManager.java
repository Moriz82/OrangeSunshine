package moriz.orangesunshine.client.sound;

import java.util.HashMap;
import java.util.Map;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.client.PSClientConfig;
import moriz.orangesunshine.client.render.shader.ShaderContext;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;

public final class ClientDrugMusicManager {
    /** Minimum normalized drug activity before ambient drug sounds may play. */
    public static final double PLAY_THRESHOLD = 1.0E-5;
    private static final Map<String, SoundInstance> PLAYING = new HashMap<>();

    private ClientDrugMusicManager() {
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            PLAYING.clear();
            return;
        }
        if (DrugProperties.of(mc.player) == null) {
            PLAYING.clear();
            return;
        }

        float master = PSClientConfig.getConfig().visual.drugMusicVolume;

        tickDrug(DrugType.SLEEP_DEPRIVATION, 0.00001F, "sleep_deprivation", OrangeSunshine.id("music.sleep_deprivation"), master);
        tickDrug(DrugType.POWER, 0.00002F, "power", OrangeSunshine.id("music.power"), master);
        tickDrug(DrugType.BATH_SALTS, 0.00003F, "bath_salts", OrangeSunshine.id("music.bath_salts"), master);
        tickDrug(DrugType.ZERO, 0.00004F, "zero", OrangeSunshine.id("music.zero"), master);
        tickDrug(DrugType.SLEEP_DEPRIVATION, 0.00005F, "sleep_deprivation_2", OrangeSunshine.id("music.sleep_deprivation_2"), master);
    }

    private static void tickDrug(DrugType type, float freq, String key, Identifier soundId, float volumeMultiplier) {
        Minecraft mc = Minecraft.getInstance();
        float drug = ShaderContext.drug(type);
        float level = Math.min(1F, drug / freq);
        float volume = level * volumeMultiplier;

        if (volume > 0.01F) {
            if (!PLAYING.containsKey(key)) {
                SoundInstance instance = new SimpleSoundInstance(
                        soundId,
                        SoundSource.MUSIC,
                        volume,
                        1.0F,
                        RandomSource.create(),
                        true,
                        0,
                        SoundInstance.Attenuation.NONE,
                        0.0,
                        0.0,
                        0.0,
                        true
                );
                mc.getSoundManager().play(instance);
                PLAYING.put(key, instance);
            }
        } else {
            SoundInstance old = PLAYING.remove(key);
            if (old != null) {
                mc.getSoundManager().stop(old);
            }
        }
    }
}
