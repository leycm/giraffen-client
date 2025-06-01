package org.leycm.giraffen.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.jetbrains.annotations.NotNull;
import org.leycm.giraffen.Client;
import org.leycm.giraffen.command.CommandRegistration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientHandlerPlayNetworkMixin {

    private static boolean bypass = false;

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(@NotNull String message, CallbackInfo ci){
        if (bypass) return;

        String sendMessage = message.replaceAll("(?<!\\\\)\\\\", "");

        if (CommandRegistration.execute(message)) {
            Client.MC.getCommandHistoryManager().add(message);
            ci.cancel();
        } else if (!sendMessage.equals(message)) {
            // Delay sending to avoid recursion
            ci.cancel();
            Client.MC.getCommandHistoryManager().add(message);
            Client.MC.execute(() -> {
                bypass = true;
                try {
                    assert Client.MC.player != null;
                    Client.MC.player.networkHandler.sendChatMessage(sendMessage);
                } finally {
                    bypass = false;
                }
            });
        }
    }
}

