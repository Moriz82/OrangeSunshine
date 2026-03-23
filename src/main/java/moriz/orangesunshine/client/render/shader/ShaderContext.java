package moriz.orangesunshine.client.render.shader;

import moriz.orangesunshine.entity.drug.*;
import moriz.orangesunshine.entity.drug.hallucination.HallucinationManager;
import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import net.minecraft.client.Minecraft;

public interface ShaderContext {
    static HallucinationManager hallucinations() {
        return DrugProperties.of(Minecraft.getInstance().player).getHallucinations();
    }

    static DrugProperties properties() {
        return DrugProperties.of(Minecraft.getInstance().player);
    }

    static float drug(DrugType type) {
        return properties().getDrugValue(type);
    }

    static float modifier(Drug.AggregateModifier type) {
        return properties().getModifier(type);
    }

    static float ticks() {
        return (Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.tickCount) + tickDelta();
    }

    static float tickDelta() {
        return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaTicks();
    }

    static long time() {
        return Minecraft.getInstance().level == null ? 0 : Minecraft.getInstance().level.getGameTime();
    }

    static float viewDistace() {
        return Minecraft.getInstance().options.getEffectiveRenderDistance() * 16;
    }
}
