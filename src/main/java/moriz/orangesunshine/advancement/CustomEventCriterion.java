package moriz.orangesunshine.advancement;

import org.jetbrains.annotations.Nullable;

public class CustomEventCriterion {
    public Trigger createTrigger(String event) {
        return player -> {
        };
    }

    public interface Trigger {
        void trigger(@Nullable Object player);
    }
}
