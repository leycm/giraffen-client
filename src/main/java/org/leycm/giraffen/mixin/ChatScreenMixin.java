package org.leycm.giraffen.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.CommandHistoryManager;
import org.jetbrains.annotations.NotNull;
import org.leycm.giraffen.Client;
import org.leycm.giraffen.command.CommandRegistration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void interceptCommand(@NotNull String message, boolean addToHistory, CallbackInfo ci) {
        String sendMessage = message.replaceAll("(?<!\\\\)\\\\", "");

        if (CommandRegistration.execute(message)) {
            if (addToHistory) {
                Client.MC.getCommandHistoryManager().add(message);
            }
            ci.cancel();
        } else {
            assert Client.MC.player != null;
            Client.MC.player.networkHandler.sendChatMessage(sendMessage);
            if (addToHistory) {
                Client.MC.getCommandHistoryManager().add(message);
            }
            ci.cancel();
        }

    }
}