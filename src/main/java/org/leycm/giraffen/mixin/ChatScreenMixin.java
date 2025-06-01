package org.leycm.giraffen.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import org.leycm.giraffen.command.CommandRegistration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void interceptCommand(String message, boolean addToHistory, CallbackInfo ci) {
        CommandRegistration CommandSystem;
        if (message.startsWith(CommandRegistration.getPrefix())) {
            ci.cancel();
        }
    }
}

