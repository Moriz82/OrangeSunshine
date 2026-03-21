package moriz.orangesunshine.client.sound;

import java.util.Optional;

import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import moriz.orangesunshine.util.MathUtils;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * Created by lukas on 22.11.14.
 */
public class MovingSoundDrug extends AbstractTickableSoundInstance {
    private final DrugProperties properties;
    private final DrugType drugType;

    public MovingSoundDrug(SoundEvent event, SoundSource category, DrugProperties properties, DrugType drugType) {
        super(event, category, Random.create());
        this.properties = properties;
        this.drugType = drugType;
        this.repeat = true;
    }

    public void markCompleted() {
        setDone();
    }

    public DrugType getType() {
        return drugType;
    }

    @Override
    public void tick() {
        volume = getTargetVolume();

        if (Mth.approximatelyEquals(volume, 0) || properties.asEntity().isRemoved()) {
            setDone();
            return;
        }

        x = (float) properties.asEntity().getX();
        y = (float) properties.asEntity().getY();
        z = (float) properties.asEntity().getZ();
    }

    private float getTargetVolume() {
        double activeValue = Optional.of(drugType)
                .map(properties::getDrug)
                .filter(drug -> drug.getType() == drugType)
                .map(Drug::getActiveValue)
                .orElse(0D);
        if (activeValue <= ClientDrugMusicManager.PLAY_THRESHOLD) {
            return 0;
        }
        return MathUtils.inverseLerp(Mth.clamp((float)activeValue, 0, 1), 0, 0.4F);
    }
}
