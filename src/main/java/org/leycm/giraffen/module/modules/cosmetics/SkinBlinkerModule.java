package org.leycm.giraffen.module.modules.cosmetics;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerModelPart;
import org.leycm.giraffen.module.Modules;
import org.leycm.giraffen.module.common.Module;
import org.leycm.giraffen.module.common.ThreadModule;

public class SkinBlinkerModule extends ThreadModule {

    public SkinBlinkerModule() {
        super("Skin Blinker", "cosmetics", "skin-blinker");
    }

    @Override
    protected void onThreadCall() {
        for (PlayerModelPart part : PlayerModelPart.values()) {
            MinecraftClient.getInstance().options.setPlayerModelPart(part, !MinecraftClient.getInstance().options.isPlayerModelPartEnabled(part));
        }

        MinecraftClient.getInstance().options.sendClientSettings();
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    public static Module getInstance() {
        return Modules.getModule("skin-blinker");
    }
}
