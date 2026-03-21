package moriz.orangesunshine.client.render.shader;

import java.io.IOException;
import java.util.List;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface CoreShaderRegistrationCallback {
    Event<CoreShaderRegistrationCallback> EVENT = EventFactory.createArrayBacked(CoreShaderRegistrationCallback.class, callbacks -> {
        return (manager, shaderList) -> {
            for (var callback : callbacks) {
                callback.call(manager, shaderList);
            }
        };
    });

    void call(Object manager, List<Object> shaderList) throws IOException;
}
