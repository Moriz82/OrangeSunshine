package moriz.orangesunshine.entity.drug.hallucination;

import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;

public interface Personality {
    Component getName(RandomSource random);

    void supplyMessage(RandomSource random, Consumer<Component> responseSender);

    void onMessageReceived(String sender, Component message, RandomSource random, boolean fromPlayer, Consumer<Component> responseSender);
}
