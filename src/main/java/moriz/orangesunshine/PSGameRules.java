package moriz.orangesunshine;

import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRules;

public interface PSGameRules {
    GameRule<Boolean> DO_SLEEP_DEPRIVATION = registerBoolean("orangesunshine:do_sleep_deprivation", GameRuleCategory.SPAWNING, false);

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

    static void bootstrap() { }
}
