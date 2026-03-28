package moriz.orangesunshine;

import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRules;

public interface PSGameRules {
    String DO_SLEEP_DEPRIVATION_NAME = "orangesunshine:do_sleep_deprivation";
    GameRuleCategory DO_SLEEP_DEPRIVATION_CATEGORY = GameRuleCategory.SPAWNING;
    boolean DO_SLEEP_DEPRIVATION_DEFAULT = false;
    Holder<GameRule<Boolean>> DO_SLEEP_DEPRIVATION = new Holder<>();

    @SuppressWarnings("unchecked")
    private static GameRule<Boolean> registerBoolean(String name, GameRuleCategory category, boolean defaultValue) {
        try {
            java.lang.reflect.Method m = GameRules.class.getDeclaredMethod("registerBoolean", String.class, GameRuleCategory.class, boolean.class);
            m.setAccessible(true);
            return (GameRule<Boolean>) m.invoke(null, name, category, defaultValue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register game rule " + name, e);
        }
    }

    static void bootstrap() {
        if (DO_SLEEP_DEPRIVATION.value == null) {
            DO_SLEEP_DEPRIVATION.value = registerBoolean(
                    DO_SLEEP_DEPRIVATION_NAME,
                    DO_SLEEP_DEPRIVATION_CATEGORY,
                    DO_SLEEP_DEPRIVATION_DEFAULT
            );
        }
    }

    static GameRule<Boolean> sleepDeprivationRule() {
        if (DO_SLEEP_DEPRIVATION.value == null) {
            throw new IllegalStateException("Game rule not initialized: " + DO_SLEEP_DEPRIVATION_NAME);
        }
        return DO_SLEEP_DEPRIVATION.value;
    }

    final class Holder<T> {
        private T value;
    }
}
