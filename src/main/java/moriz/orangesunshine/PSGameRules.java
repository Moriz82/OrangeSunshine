package moriz.orangesunshine;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public interface PSGameRules {
    GameRule<Boolean> DO_SLEEP_DEPRIVATION = GameRuleBuilder.forBoolean(false)
            .category(GameRuleCategory.SPAWNING)
            .buildAndRegister(OrangeSunshine.id("do_sleep_deprivation"));

    static void bootstrap() { }
}
