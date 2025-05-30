package org.leycm.giraffen.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.leycm.giraffen.module.modules.movment.AirJumpModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Redirect(
            method = "tickMovement()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;isOnGround()Z"
            )
    )
    private boolean onIsOnGround(ClientPlayerEntity instance) {
        if (AirJumpModule.getInstance().isRunning()) {
            return true; // Fake being on ground when AirJump is active
        }
        return instance.isOnGround(); // Normal ground check otherwise
    }
}
