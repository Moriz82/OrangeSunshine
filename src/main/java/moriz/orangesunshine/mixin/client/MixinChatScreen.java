package moriz.orangesunshine.mixin.client;

import moriz.orangesunshine.entity.drug.MessageDistorter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
abstract class MixinChatScreen {
    /** Distort outgoing chat messages when under the influence. */
    @Inject(method = "normalize", at = @At("RETURN"), cancellable = true, require = 0)
    public void onNormalize(String chatText, CallbackInfoReturnable<String> info) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        info.setReturnValue(MessageDistorter.INSTANCE.distortOutgoingMessage(mc.player, info.getReturnValue()));
    }
}
