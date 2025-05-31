package org.leycm.giraffen.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.leycm.giraffen.GiraffenClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientHandlerPlayNetworkMixin {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci) throws CommandSyntaxException {
        if (message.startsWith(GiraffenClient.commandHandler.getPrefix())) {
            GiraffenClient.commandHandler.dispatch(message.substring(1));
            ci.cancel();
        }
    }
}