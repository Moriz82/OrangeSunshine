package moriz.orangesunshine.client.render.shader;

import moriz.orangesunshine.entity.drug.*;
import moriz.orangesunshine.entity.drug.hallucination.HallucinationManager;
import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import net.minecraft.client.Minecraft;

public interface ShaderContext {
    static HallucinationManager hallucinations() {
        return DrugProperties.of(MinecraftClient.getInstance().player).getHallucinations();
    }

    static DrugProperties properties() {
        return DrugProperties.of(MinecraftClient.getInstance().player);
    }

    static float drug(DrugType type) {
        return properties().getDrugValue(type);
    }

    static float modifier(Drug.AggregateModifier type) {
        return properties().getModifier(type);
    }

    static float ticks() {
        return MinecraftClient.getInstance().player.age + tickDelta();
    }

    static float tickDelta() {
        return MinecraftClient.getInstance().getTickDelta();
    }

    static long time() {
        return MinecraftClient.getInstance().world.getTime();
    }

    static float viewDistace() {
        return MinecraftClient.getInstance().options.getViewDistance().getValue() * 16;
    }
}
