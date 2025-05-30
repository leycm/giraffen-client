package org.leycm.giraffen.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.leycm.giraffen.GiraffenClient;
import org.leycm.giraffen.module.modules.cosmetics.CapeLoaderModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {

    @Shadow
    protected abstract PlayerListEntry getPlayerListEntry();

    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)
    public void onGetSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
        if (!CapeLoaderModule.getInstance().isRunning()) return;

        PlayerListEntry playerListEntry = this.getPlayerListEntry();
        String capeId = CapeLoaderModule.getInstance().getSetting("cape.in-use.id", String.class, "none");
        String capeType = CapeLoaderModule.getInstance().getSetting("cape.in-use.type", String.class, "default");
        Identifier cape = null;

        if(!capeId.equalsIgnoreCase("none") &&
                !capeId.isEmpty() &&
                !capeType.isEmpty()) {

            if (capeType.equalsIgnoreCase("default")) {
                String path = "cape/" + capeType + "/" + capeId + ".png";
                cape = Identifier.of(GiraffenClient.MOD_ID, path);

            } else {
                String path = "cape/dynamic/" + capeId + ".png";
                cape = Identifier.of(GiraffenClient.MOD_ID, path);
            }
        }

        if (playerListEntry != null) {
            if (Objects.equals(playerListEntry.getProfile().getId(), MinecraftClient.getInstance().getSession().getUuidOrNull())) {
                SkinTextures original = cir.getReturnValue();

                SkinTextures modified = new SkinTextures(
                        original.texture(),
                        original.textureUrl(),
                        cape,
                        original.elytraTexture(),
                        original.model(),
                        original.secure()
                );

                cir.setReturnValue(modified);
            }
        }
    }
}
