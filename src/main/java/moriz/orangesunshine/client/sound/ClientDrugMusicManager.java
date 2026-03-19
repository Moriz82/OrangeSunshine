package moriz.orangesunshine.client.sound;

import java.util.Comparator;
import java.util.Optional;

import moriz.orangesunshine.PSSounds;
import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import org.jetbrains.annotations.Nullable;

import moriz.orangesunshine.client.OrangeSunshineClient;
import moriz.orangesunshine.entity.drug.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

/**
 * Created by Sollace on 11 June 2023
 */
public class ClientDrugMusicManager {
    public static final float PLAY_THRESHOLD = 0.01F;

    private Optional<MovingSoundDrug> activeSound = Optional.empty();

    public void update(DrugProperties properties) {
        DrugType activeDrug = getActiveSound().map(MovingSoundDrug::getType).orElse(null);
        Comparator<DrugType> comparator = Comparator.comparing(type -> properties.getDrugValue(type));
        DrugType.REGISTRY
            .stream()
            .filter(OrangeSunshineClient.getConfig().audio::hasBackgroundMusic)
            .filter(type -> properties.getDrugValue(type) >= PLAY_THRESHOLD)
            .sorted(comparator.reversed())
            .findFirst()
            .filter(type -> type != activeDrug)
            .ifPresent(drugType -> activeSound = Optional.ofNullable(startPlayingSound(properties, drugType)));
    }

    private Optional<MovingSoundDrug> getActiveSound() {
        if (activeSound.isPresent()) {
            activeSound = activeSound.filter(sound -> !sound.isDone());
        }
        return activeSound;
    }

    private MovingSoundDrug startPlayingSound(DrugProperties properties, DrugType type) {
        OrangeSunshine.LOGGER.info("Playing drug background music for " + type.id());
        SoundEvent sound = Registries.SOUND_EVENT.getOrEmpty(OrangeSunshine.id("drug." + type.id().getPath())).orElse(PSSounds.DRUG_GENERIC);
        SoundManager manager = MinecraftClient.getInstance().getSoundManager();

        @Nullable
        WeightedSoundSet soundSet = manager.get(sound.getId());
        if (soundSet == null || soundSet.getSound(properties.asEntity().getRandom()) == null) {
            sound = PSSounds.DRUG_GENERIC;
        }

        if (sound == PSSounds.DRUG_GENERIC) {
            OrangeSunshine.LOGGER.info("Drug " + type.id() + " has no sound, using the generic version instead");
        }

        MovingSoundDrug newSound = new MovingSoundDrug(sound, SoundCategory.AMBIENT, properties, type);
        activeSound.ifPresent(MovingSoundDrug::markCompleted);
        manager.play(newSound);
        return newSound;
    }
}
