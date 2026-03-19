package moriz.orangesunshine.entity.drug.sound;

import moriz.orangesunshine.PSSounds;
import moriz.orangesunshine.entity.drug.*;
import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.util.MathUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

/**
 * Created by lukas on 22.11.14.
 * Updated by Sollace on 15 Jan 2023
 */
public class DrugMusicManager {
    public static final float PLAY_THRESHOLD = 0.01F;

    final DrugProperties properties;

    private int delayUntilHeartbeat;
    private int delayUntilBreath;
    private boolean lastBreathWasIn;

    private float prevHeartbeatPulseStrength;
    private float heartbeatPulseStrength;
    private float targetHeartbeatPulseStrength;

    public DrugMusicManager(DrugProperties properties) {
        this.properties = properties;
    }

    public void update() {
        Player entity = properties.asEntity();

        if (delayUntilHeartbeat > 0) {
            delayUntilHeartbeat--;
        }

        if (delayUntilBreath > 0) {
            delayUntilBreath--;
        }

        prevHeartbeatPulseStrength = heartbeatPulseStrength;
        heartbeatPulseStrength = MathUtils.approach(heartbeatPulseStrength, targetHeartbeatPulseStrength, 0.2F);
        if (targetHeartbeatPulseStrength > 0) {
            targetHeartbeatPulseStrength -= 0.02F;
        }

        if (delayUntilHeartbeat == 0) {
            float heartbeatVolume = properties.getModifier(Drug.HEART_BEAT_VOLUME);
            if (heartbeatVolume > 0) {
                float speed = properties.getModifier(Drug.HEART_BEAT_SPEED);

                delayUntilHeartbeat = speed <= 1 ? 1 : Mth.floor(35F / (speed - 1F));
                targetHeartbeatPulseStrength = 1;
                entity.level().playSound(entity, entity.getX(), entity.getY(), entity.getZ(),
                        PSSounds.ENTITY_PLAYER_HEARTBEAT,
                        SoundSource.AMBIENT, heartbeatVolume, speed);
            }
        }

        if (delayUntilBreath == 0) {
            lastBreathWasIn = !lastBreathWasIn;

            float breathVolume = properties.getModifier(Drug.BREATH_VOLUME);
            if (breathVolume > 0) {
                float speed = properties.getModifier(Drug.BREATH_SPEED);
                delayUntilBreath = Mth.floor(30F / speed);
                entity.level().playSound(entity, entity, PSSounds.ENTITY_PLAYER_BREATH, SoundSource.PLAYERS,
                        breathVolume,
                        speed * 0.05F + 0.9F + (lastBreathWasIn ? 0.15F : 0)
                );
            }
        }
    }

    public float getHeartbeatPulseStrength(float delta) {
        return Mth.lerp(delta, prevHeartbeatPulseStrength, heartbeatPulseStrength);
    }
}
