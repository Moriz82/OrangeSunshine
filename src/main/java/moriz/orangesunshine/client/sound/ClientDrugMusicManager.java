package moriz.orangesunshine.client.sound;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.client.OrangeSunshineClient;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.config.JsonConfig;
import net.minecraft.client.resources.sounds.SoundEngine;
import net.minecraft.client.resources.sounds.SoundManager;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.Identifier;

import java.util.Map;

public class ClientDrugMusicManager {
    private static final Map<String, SoundInstance> playingSounds = new HashMap<>();

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            playingSounds.clear();
            return;
        }

        DrugProperties drugProperties = DrugProperties.of(mc.player);
        if (drugProperties == null) {
            playingSounds.clear();
            return;
        }

        float audioVolume = OrangeSunshineClient.getConfig().visual.drugMusicVolume;

        // Process active sounds
        playingSounds.forEach((key, sound) -> {
            if (sound.isStopped() || sound.getSource().equals(SoundSource.MASTER)) {
                mc.getSoundManager().stop(sound);
                playingSounds.remove(key);
            }
        });

        // Start new sounds
        tickDrugMusic(DrugType.SLEEP_DEPRIVATION, 0.00001F, "sleep_deprivation", OrangeSunshine.id("music.sleep_deprivation"), audioVolume);
        tickDrugMusic(DrugType.POWER, 0.00002F, "power", OrangeSunshine.id("music.power"), audioVolume);
        tickDrugMusic(DrugType.BATH_SALTS, 0.00003F, "bath_salts", OrangeSunshine.id("music.bath_salts"), audioVolume);
        tickDrugMusic(DrugType.ZERO, 0.00004F, "zero", OrangeSunshine.id("music.zero"), audioVolume);
        tickDrugMusic(DrugType.SLEEP_DEPRIVATION, 0.00005F, "sleep_deprivation_2", OrangeSunshine.id("music.sleep_deprivation_2"), audioVolume);
    }

    private static void tickDrugMusic(DrugType type, float freq, String key, Identifier sound, float volumeMultiplier) {
        Minecraft mc = Minecraft.getInstance();
        float drugLevel = ShaderContext.drug(type);
        float volume = Math.min(1, drugLevel / freq);

        if (volume > 0.01F) {
            SoundInstance sound = playingSounds.get(key);
            if (sound == null) {
                sound = SimpleSoundInstance.create(sound, SoundSource.MASTER, volume * volumeMultiplier);
                mc.getSoundManager().play(sound);
                playingSounds.put(key, sound);
            } else {
                mc.getSoundManager().updateSource(sound, volume * volumeMultiplier, 0); // Volume adjustment, pitch is 1
            }
        } else if (playingSounds.containsKey(key)) {
            mc.getSoundManager().stop(playingSounds.get(key));
            playingSounds.remove(key);
        }
    }

    private static class SimpleSoundInstance implements SoundInstance {
        private final Identifier sound;
        private final SoundSource source;
        private final float volume;
        private final float pitch;
        private final boolean loop;
        private final int delay;

        private SimpleSoundInstance(Identifier sound, SoundSource source, float volume, float pitch, boolean loop, int delay) {
            this.sound = sound;
            this.source = source;
            this.volume = volume;
            this.pitch = pitch;
            this.loop = loop;
            this.delay = delay;
        }

        public static SimpleSoundInstance create(Identifier sound, SoundSource source, float volume) {
            return new SimpleSoundInstance(sound, source, volume, 1.0F, true, 0);
        }

        @Override
        public Identifier getLocation() {
            return sound;
        }

        @Override
        public SoundSource getSource() {
            return source;
        }

        @Override
        public boolean isLooping() {
            return loop;
        }

        @Override
        public int getDelay() {
            return delay;
        }

        @Override
        public float getVolume() {
            return volume;
        }

        @Override
        public float getPitch() {
            return pitch;
        }

        @Override
        public SoundInstance.Marker getMarker() {
            return SoundInstance.Marker.ZERO;
        }

        @Override
        public boolean isStopped() {
            // TODO: implement actual stopped logic
            return false;
        }

        @Override
        public void stop() {
            // TODO: implement actual stop logic
        }
    }
}
