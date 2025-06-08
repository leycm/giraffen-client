package org.leycm.giraffen.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.leycm.giraffen.module.modules.cosmetics.CapeLoaderModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {

    @Shadow
    protected abstract PlayerListEntry getPlayerListEntry();

    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)
    public void onGetSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
        PlayerListEntry playerListEntry = this.getPlayerListEntry();
        if (!CapeLoaderModule.getInstance().isRunning()) return;
        if (playerListEntry == null) return;

        boolean isLocalPlayer = Objects.equals(
                playerListEntry.getProfile().getId(),
                MinecraftClient.getInstance().getSession().getUuidOrNull()
        );



        if (isLocalPlayer) {
            SkinTextures original = cir.getReturnValue();

            Identifier customCape = CapeLoaderModule.getInstance().isRunning() ?
                    CapeLoaderModule.getInstance().getCape() :
                    original.capeTexture();

            SkinTextures modified = new SkinTextures(
                    original.texture(),
                    original.textureUrl(),
                    customCape,
                    customCape,
                    original.model(),
                    original.secure()
            );

            cir.setReturnValue(modified);
        }

    }
}
